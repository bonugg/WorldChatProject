package com.example.WorldChatProject.listener;

import java.util.Map;

public interface WebSocketManager {

    //웹소켓 연결 정보를 userSessionMap에 저장
    void connectManange(Map<String, String> sessionAttributes, String sessionId);

    //웹소켓 연결 해제 정보로 userSessionMap에서 삭제
    // & 채팅방 삭제
    void disconnectManage(Map<String, String> sessionAttributes, String sessionId);

    //웹소켓 연결 강제 해제
    void disconnectUser(String userName);
}
