package com.example.WorldChatProject.user.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class ResponseDTO<T> {
    private List<T> items;
    private Page<T> pageItems;
    private T item;
    private String errorMessage;
    private int statusCode;
}
