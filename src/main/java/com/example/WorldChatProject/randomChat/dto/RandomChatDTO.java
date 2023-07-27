package com.example.WorldChatProject.randomChat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RandomChatDTO {
    public enum MessageType{
        CHAT, ENTER, LEAVE;
    }
    private MessageType type;
    private String randomRoomId;
    private String sender;
    private String content;
    private String time;

}
