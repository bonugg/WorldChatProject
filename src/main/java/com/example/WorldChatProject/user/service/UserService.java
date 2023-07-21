package com.example.WorldChatProject.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.security.entity.RefreshToken;
import com.example.WorldChatProject.user.security.jwt.JwtProperties;
import com.example.WorldChatProject.user.security.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void UserLogout(HttpSession session, HttpServletRequest request, HttpServletResponse response){
        log.info("로그아웃 시작");
        session.invalidate();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //principal 안에는 유저의 정보가 담겨있음
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        
        if (authentication != null) {
            //리프레쉬 토큰 초기화 후 로그아웃
            RefreshToken refreshToken = refreshTokenRepository.findByKeyId(principal.getUsername()).get();
            refreshToken.setRefreshToken("");
            refreshTokenRepository.save(refreshToken);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            log.info("로그아웃 성공");
        }
    }

    public void UserJoin(User user){
        user.setUserPwd(bCryptPasswordEncoder.encode(user.getUserPwd()));
        //user 권한 부여
        user.setUserRoles("ROLE_USER");
        userRepository.save(user);
    }

    public ResponseEntity<String> UserIdCheck(String userName){
        if(userRepository.findByUserName(userName).isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body("fail");
        }else {
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        }
    }

    public ResponseEntity UserRefreshToken(HttpServletResponse response, String userName){
        RefreshToken refreshTokenGet = refreshTokenRepository.findByKeyId(userName).get();

        String refreshTokenHeader = refreshTokenGet.getRefreshToken();
        if (refreshTokenHeader == null || !refreshTokenHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            log.info("리프레쉬 토큰 만료");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레쉬 토큰 만료");
        }

        try {
            String refreshToken = refreshTokenHeader.replace(JwtProperties.TOKEN_PREFIX, "");
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(refreshToken);
            Boolean isRefreshToken = decodedJWT.getClaim("refresh").asBoolean();
            if (!isRefreshToken) {
                log.info("존재하지 않는 리프레쉬 토큰");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 리프레쉬 토큰");
            }
            User user = userRepository.findByUserName(decodedJWT.getSubject()).get();

            // 새로운 엑세스 토큰 생성
            String newAccessToken = JWT.create()
                    .withSubject(user.getUserName())
                    .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
                    .withClaim("id", user.getUserId())
                    .withClaim("username", user.getUserName())
                    .sign(Algorithm.HMAC512(JwtProperties.SECRET));

            log.info("엑세스 토큰 생성 성공");
            response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + newAccessToken);

            return ResponseEntity.ok().build();
        } catch (JWTVerificationException e) {
            log.info("리프레쉬 토큰 만료");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레쉬 토큰 만료");
        }
    }

}
