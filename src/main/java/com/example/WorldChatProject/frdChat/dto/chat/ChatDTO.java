package com.example.WorldChatProject.frdChat.dto.chat;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

@Data
public class ChatDTO {
    private Integer channelId;
    private Integer writerId;
    private String chat;
}
