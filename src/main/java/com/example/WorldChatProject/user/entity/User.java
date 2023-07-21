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
    public UserDTO EntityToDTO() {
        UserDTO userDTO = UserDTO.builder()
                .userId(this.userId)
                .userName(this.userName)
                .userPwd(this.userPwd)
                .userEmail(this.userEmail)
                .userRoles(this.userRoles)
                .build();
        return userDTO;
    }
}
