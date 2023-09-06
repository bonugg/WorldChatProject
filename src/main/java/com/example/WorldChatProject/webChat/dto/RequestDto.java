package com.example.WorldChatProject.webChat.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequestDto {
    private String sender;
    private String receiver;
    private String type;
    private String roomId;
    private Long receiverId;
    private String requesterId;
    private Long friendsId;
    private List<String> receivers;
}