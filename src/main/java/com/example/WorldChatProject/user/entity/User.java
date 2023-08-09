package com.example.WorldChatProject.user.entity;

import com.example.WorldChatProject.cateChat.entity.CateRoom;
import com.example.WorldChatProject.user.dto.UserDTO;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String userProfileName;
    private String userProfilePath;
    private String userProfileOrigin;
  
    public List<String> getRoleList() {

        if (this.userRoles.length() > 0) {
            return Arrays.asList(this.userRoles.split(","));
        }
        return new ArrayList<>();
    }
  
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
                .userProfileName(this.userProfileName)
                .userProfilePath(this.userProfilePath)
                .userProfileOrigin(this.userProfileOrigin)
                .build();
        return userDTO;
    }
}