package com.example.WorldChatProject.friends.dto;

import com.example.WorldChatProject.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackListDTO {
    private long id;
    private User hater;
    private User hated;
}
