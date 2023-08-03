package com.example.WorldChatProject.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private long userId;
    private String userName;
    private String userPwd;
    private String userEmail;
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
    private String userMessage;
    private String userProfileName;
    private String userProfilePath;
    private String userProfileOrigin;
    public List<String> getRoleList(){
        if(this.userRoles.length() > 0){
            return Arrays.asList(this.userRoles.split(","));
        }
        return new ArrayList<>();
    }
}
