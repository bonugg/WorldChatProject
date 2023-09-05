package com.example.WorldChatProject.randomChat.dto;

import com.example.WorldChatProject.randomChat.MessageType;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RandomChatDTO {

    private MessageType type;
    private UUID randomChatId;
    private String content;
    private String time;
    private long randomRoomId;
    private String sender;
    private long userId;

    //파일이름추가
    private String fileName;
    private String fileOrigin;

    private boolean isLiked;

}
