package com.example.WorldChatProject.webChat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// WebRTC 연결 시 사용되는 클래스
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage {
    private String from; // 보내는 유저 UUID
    private String type; // 메시지 타입
    private String data; // roomId
    private Object candidate; // 상태
    private Object sdp; // sdp 정보
    private String chatType; // voice and video
}
