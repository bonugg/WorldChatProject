package com.example.WorldChatProject.randomChat.dto;

import com.example.WorldChatProject.randomChat.MessageType;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.user.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RandomChatDTO {

    private MessageType type;
    private long randomChatId;
    private String content;
    private String time;
    private long randomRoomId;
    private long sender;


}
