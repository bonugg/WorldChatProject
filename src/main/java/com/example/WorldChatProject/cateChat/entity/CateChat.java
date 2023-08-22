package com.example.WorldChatProject.cateChat.entity;

import com.example.WorldChatProject.cateChat.MessageType;
import com.example.WorldChatProject.cateChat.dto.CateChatDTO;
import com.example.WorldChatProject.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CateChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long cateChatId; //채팅 식별자

    @Column
    private String cateChatContent; //채팅 내용

    @Column
    private String cateChatRegdate; //채팅 보낸 시간

    @Column
    private String sender;

    @Column
    private String s3DataUrl;

    @Enumerated(EnumType.STRING)
    private MessageType type; //채팅 타입

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "cateId")
    private CateRoom cateRoom; //join -> cateId(방번호)


    //엔티티를 DTO로 변환하는 메소드
    public CateChatDTO toCateChatDTO() {
        CateChatDTO cateChatDTO = CateChatDTO.builder()
                                             .cateChatId(this.cateChatId)
                                             .cateChatContent(this.cateChatContent)
                                             .cateChatRegdate(this.cateChatRegdate)
                                             .type(this.type)
                                             .sender(this.sender)
                                             .cateId((cateRoom.getCateId()))
                                             .s3DataUrl(this.s3DataUrl)
                                             .build();

        return cateChatDTO;
    }


}
