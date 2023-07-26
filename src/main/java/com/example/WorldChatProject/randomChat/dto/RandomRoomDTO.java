package com.example.WorldChatProject.randomChat.dto;

import com.example.WorldChatProject.user.entity.User;
import lombok.Data;

@Data
public class RandomRoomDTO {

    public enum MessageType{
        CHAT, ENTER, LEAVE;
    }

    private MessageType type;
    private long randomId;
    private String randomName;
    private String randomNum;
    private User user;
    private String message;
    private String randomChatTime;

}
