package com.example.WorldChatProject.user.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.security.dto.LoginRequestDto;
import com.example.WorldChatProject.user.security.entity.RefreshToken;
import com.example.WorldChatProject.user.security.repository.RefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private final AuthenticationManager authenticationManager;

	private final RefreshTokenRepository refreshTokenRepository;

	// 인증 요청시에 실행되는 함수 => /login
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		// request에 있는 username과 password를 파싱해서 자바 Object로 받기
		ObjectMapper om = new ObjectMapper();
		LoginRequestDto loginRequestDto = null;
		try {
			//로그인 요청을 하면 request에 담은 값을 loginRequestDto에 담는다
			loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 유저네임패스워드 토큰 생성
		//토큰에 리퀘스트 dto에 담긴 유저이름과 비밀번호 담는다
		UsernamePasswordAuthenticationToken authenticationToken = 
				new UsernamePasswordAuthenticationToken(
						loginRequestDto.getUsername(),
						loginRequestDto.getPassword());
		//토큰에 인증 정보 담아서 리턴한다.
		Authentication authentication =
				authenticationManager.authenticate(authenticationToken);

		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		return authentication;
	}

	// JWT Token 생성해서 response에 담아주기
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		PrincipalDetails principalDetailis = (PrincipalDetails) authResult.getPrincipal();

		//access 토큰 생성
		String jwtToken = JWT.create()
				.withSubject(principalDetailis.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
				.withClaim("id", principalDetailis.getUser().getUserId())
				.withClaim("username", principalDetailis.getUser().getUserName())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));

		//엑세스 토큰 헤더에 저장 및 유저 아이디 저장
		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);
		response.addHeader("username", principalDetailis.getUser().getUserName());

		// 리프레쉬 토큰 생성
		String refreshToken = JWT.create()
				.withSubject(principalDetailis.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.REFRESH_EXPIRATION_TIME))
				.withClaim("refresh", true)
				.withClaim("username", principalDetailis.getUser().getUserName())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));

		// 리프레쉬 토큰 db 저장
		if(refreshTokenRepository.findByKeyId(principalDetailis.getUsername()).isPresent()){
			RefreshToken refreshToken1 = refreshTokenRepository.findByKeyId(principalDetailis.getUsername()).get();
			refreshToken1.setRefreshToken(JwtProperties.TOKEN_PREFIX+refreshToken);
			refreshTokenRepository.save(refreshToken1);
		}else {
			RefreshToken refreshToken2 = new RefreshToken();
			refreshToken2.setKeyId(principalDetailis.getUsername());
			refreshToken2.setRefreshToken(JwtProperties.TOKEN_PREFIX+refreshToken);
			refreshTokenRepository.save(refreshToken2);
		}
	}
	
}
