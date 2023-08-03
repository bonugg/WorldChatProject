package com.example.WorldChatProject.frdChat.dto.chat;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrdChatMsgDTO {
    private long id;
    private Integer writerId;
    private String chat;

}
