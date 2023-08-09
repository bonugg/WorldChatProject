package com.example.WorldChatProject.user.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.WorldChatProject.user.dto.UserDTO;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.security.entity.RefreshToken;
import com.example.WorldChatProject.user.security.repository.RefreshTokenRepository;
import com.example.WorldChatProject.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter{
	
	private UserRepository userRepository;
	private RefreshTokenRepository refreshTokenRepository;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String token = null;
		String userName2 = null;
		try {
			String header = request.getHeader(JwtProperties.HEADER_STRING);
			userName2 = request.getHeader("userName");
			if(header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
				chain.doFilter(request, response);
				return;
			}
			token = request.getHeader(JwtProperties.HEADER_STRING)
					.replace(JwtProperties.TOKEN_PREFIX, "");
			String userName = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
					.getClaim("username").asString();
			if(userName != null) {
				UserDTO userDTO= userRepository.findByUserName(userName).get().EntityToDTO();

				PrincipalDetails principalDetails = new PrincipalDetails(userDTO);
				Authentication authentication =
						new UsernamePasswordAuthenticationToken(
								principalDetails,
								null, // 패스워드는 모르니까 null 처리, 인증 용도 x
								principalDetails.getAuthorities());

				// 권한 관리를 위해 세션에 접근하여 값 저장
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			chain.doFilter(request, response);
		}catch (TokenExpiredException te){
			if(userName2 != null){
				UserRefreshToken(response, userName2);
			}
		}
	}

	public ResponseEntity UserRefreshToken(HttpServletResponse response, String userName){
		RefreshToken refreshTokenGet = refreshTokenRepository.findByKeyId(userName).get();

		String refreshTokenHeader = refreshTokenGet.getRefreshToken();
		if (refreshTokenHeader == null || !refreshTokenHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
			log.info("리프레쉬 토큰 만료");
			response.addHeader("refresh", "finish");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레쉬 토큰 만료");
		}

		try {
			String refreshToken = refreshTokenHeader.replace(JwtProperties.TOKEN_PREFIX, "");
			DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(refreshToken);
			Boolean isRefreshToken = decodedJWT.getClaim("refresh").asBoolean();
			if (!isRefreshToken) {
				log.info("존재하지 않는 리프레쉬 토큰");
				response.addHeader("refresh", "finish");
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

			return ResponseEntity.ok().body("123");
		} catch (JWTVerificationException e) {
			log.info("리프레쉬 토큰 만료");
			response.addHeader("refresh", "finish");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레쉬 토큰 만료");
		}
	}
	
}
