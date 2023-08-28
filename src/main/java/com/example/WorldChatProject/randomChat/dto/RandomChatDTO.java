package com.example.WorldChatProject.randomChat.dto;

import com.example.WorldChatProject.cateChat.entity.CateChat;
import com.example.WorldChatProject.randomChat.MessageType;
import com.example.WorldChatProject.randomChat.entity.RandomChat;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.user.entity.User;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RandomChatDTO {

    private MessageType type;
    private long randomChatId;
    private String content;
    private String time;
    private long randomRoomId;
    private String sender;

    //파일이름추가
    private String fileName;
    private String fileOrigin;

}
