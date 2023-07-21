package com.example.WorldChatProject.user.security.jwt;

public interface JwtProperties {
	String SECRET = "loose"; // 서버만 알고 있는 개인키
	int EXPIRATION_TIME = 300000; // 10초 (1/1000초)
	String TOKEN_PREFIX = "Bearer ";
	String HEADER_STRING = "Authorization";

//	long REFRESH_EXPIRATION_TIME = 604800000L; // 7일 (1/1000초)
	long REFRESH_EXPIRATION_TIME = 1200000; // 7일 (1/1000초)
	String REFRESH_HEADER_STRING = "RefreshAuthorization";
}
