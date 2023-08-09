package com.example.WorldChatProject.frdChat.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseDTO<T> {
    private List<T> items;
    private T item;
    private String errorMessage;
    private int statusCode;
}
