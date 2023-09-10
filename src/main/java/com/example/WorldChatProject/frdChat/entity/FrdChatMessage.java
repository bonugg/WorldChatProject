package com.example.WorldChatProject.frdChat.entity;

import com.example.WorldChatProject.frdChat.dto.FrdChatMessageDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(FrdChatMessageListener.class)
public class FrdChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long roomId;
    private String sender;
    private String receiver;
    private String message;
    private String createdAt;
    private boolean checkRead;

    /* 파일 업로드 관련 변수 */
    private String s3DataUrl; // 파일 업로드 url
    private String fileName; // 파일이름
    private String fileDir; // s3 파일 경로

    //좋아요
    private boolean isLiked;

    public FrdChatMessageDTO toFrdChatMessageDTO() {
        FrdChatMessageDTO frdChatMessageDTO = FrdChatMessageDTO.builder()
                .id(this.id)
                .roomId(this.roomId)
                .sender(this.sender)
                .message(this.message)
                .sender(this.sender)
                .receiver(this.receiver)
                .createdAt(String.valueOf(this.createdAt))
                .checkRead(this.checkRead)
                .s3DataUrl(this.s3DataUrl)
                .fileName(this.fileName)
                .fileDir(this.fileDir)
                .build();

        return frdChatMessageDTO;
    }
}
