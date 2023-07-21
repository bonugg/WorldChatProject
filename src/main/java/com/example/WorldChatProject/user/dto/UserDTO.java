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
    public List<String> getRoleList(){
        if(this.userRoles.length() > 0){
            return Arrays.asList(this.userRoles.split(","));
        }
        return new ArrayList<>();
    }
}
