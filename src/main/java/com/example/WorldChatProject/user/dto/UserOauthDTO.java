package com.example.WorldChatProject.user.dto;

import com.example.WorldChatProject.user.entity.User;
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
public class UserOauthDTO {
    private String email;
    private String sub;
    private String name;
    private String picture;
    private String nationally;
}

