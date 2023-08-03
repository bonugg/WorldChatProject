package com.example.WorldChatProject.user.entity;

import com.example.WorldChatProject.user.dto.UserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //유저 primary key
    private long userId;
    //아이디
    private String userName;
    //패스워드
    private String userPwd;
    //이메일
    private String userEmail;
    //권한
    private String userRoles;
    //이름
    private String userTrueName;
    //닉네임
    private String userNickName;
    //번호
    private String userPhone;
    //국적
    private String userNationality;
    //상태메시지
    private String userMessage = ""; // 빈 문자열로 기본값 설정

    public UserDTO EntityToDTO() {
        UserDTO userDTO = UserDTO.builder()
                .userId(this.userId)
                .userName(this.userName)
                .userPwd(this.userPwd)
                .userEmail(this.userEmail)
                .userRoles(this.userRoles)
                .userTrueName(this.userTrueName)
                .userNickName(this.userNickName)
                .userPhone(this.userPhone)
                .userNationality(this.userNationality)
                .userMessage(this.userMessage)
                .build();
        return userDTO;
    }
}