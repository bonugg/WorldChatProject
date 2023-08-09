package com.example.WorldChatProject.cateChat.dto;

import com.example.WorldChatProject.cateChat.MessageType;
import com.example.WorldChatProject.cateChat.entity.CateChat;
import com.example.WorldChatProject.cateChat.entity.CateRoom;
import com.example.WorldChatProject.user.entity.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

@Data
@Builder
public class CateChatDTO {

    private Long cateChatId;
    private String cateChatContent;
    private String cateChatRegdate;
    private MessageType type;
    private Long cateId;
    private String sender;

    private String username;

    //DTO를 엔티티로 변환하는 메소드
    public CateChat toCateChat() {
        CateChat cateChat = CateChat.builder()
                                    .cateChatId(this.cateChatId)
                                    .cateChatContent(this.cateChatContent)
                                     //null값이므로 엔티티로 전달하면 안된다
//                                  .cateChatRegdate(this.cateChatRegdate)
                                    .type(this.type)
                                    .sender(this.username)
                                    .cateRoom(this.toCateChat().getCateRoom())
                                    .build();

        return cateChat;
    }






}
