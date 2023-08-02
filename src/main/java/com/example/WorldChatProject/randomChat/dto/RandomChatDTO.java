package com.example.WorldChatProject.randomChat.dto;

import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.user.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RandomChatDTO {
    public enum MessageType{
        CHAT, ENTER, LEAVE;
    }
    private MessageType type;
    private long randomChatId;
    private String content;
    private String time;
    private long randomRoomId;
    private long sender;


}
