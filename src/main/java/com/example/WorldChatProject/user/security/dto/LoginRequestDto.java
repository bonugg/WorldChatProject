package com.example.WorldChatProject.user.security.dto;

import lombok.Data;

@Data
//로그인 요청 시 username에 아이디 password에 비밀번호 담아서 요청
public class LoginRequestDto {
	private String username;
	private String password;
}
