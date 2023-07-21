package com.example.WorldChatProject.user.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.WorldChatProject.user.dto.UserDTO;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter{
	
	private UserRepository userRepository;
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			String header = request.getHeader(JwtProperties.HEADER_STRING);
			if(header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
				chain.doFilter(request, response);
				return;
			}
			String token = request.getHeader(JwtProperties.HEADER_STRING)
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
			log.info("만료된 토큰");
		}
	}
	
}
