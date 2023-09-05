package com.example.WorldChatProject.cateChat.dto;

import com.example.WorldChatProject.cateChat.MessageType;
import com.example.WorldChatProject.cateChat.entity.CateChat;
import com.example.WorldChatProject.cateChat.entity.CateRoom;
import com.example.WorldChatProject.user.entity.User;
import lombok.*;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CateChatDTO {

    private Long cateChatId;
    private String cateChatContent;
    private String cateChatRegdate;
    private MessageType type;
    private Long cateId;
    private String sender;

    private String userProfile;
    private String newAccessToken;
    private String refresh;

    /* 파일 업로드 관련 변수 */
    private String s3DataUrl; // 파일 업로드 url
    private String fileName; // 파일이름
    private String fileDir; // s3 파일 경로

    //좋아요
    private boolean isLiked;
    private int likeCount;



    //DTO를 엔티티로 변환하는 메소드
    public CateChat toCateChat() {
        CateChat cateChat = CateChat.builder()
                .cateChatId(this.cateChatId)
                .cateChatContent(this.cateChatContent)
                //null값이므로 엔티티로 전달하면 안된다
//                                  .cateChatRegdate(this.cateChatRegdate)
                .type(this.type)
                .sender(this.sender)
                .cateRoom(this.toCateChat().getCateRoom())
                .build();

        return cateChat;
    }






}
