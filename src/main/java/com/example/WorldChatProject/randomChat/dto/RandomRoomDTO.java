package com.example.WorldChatProject.randomChat.dto;

import com.example.WorldChatProject.user.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RandomRoomDTO {

    private long randomRoomId;
    private String randomRoomName;
    private User user1;
    private User user2;
    private String errorMessage;

}
