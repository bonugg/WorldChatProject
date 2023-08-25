package com.example.WorldChatProject.webChat.dto;

import lombok.Data;

@Data
public class RequestDto {
    private String sender;
    private String receiver;
    private String type;
}