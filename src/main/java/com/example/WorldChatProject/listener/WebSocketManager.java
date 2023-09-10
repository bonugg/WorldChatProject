package com.example.WorldChatProject.listener;

import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.user.entity.User;

import java.util.Map;

public interface WebSocketManager {

    //웹소켓 연결 정보를 userSessionMap에 저장
    void connectManange(Map<String, String> sessionAttributes, String sessionId);

    //userSessionMap에 웹소켓 연결 정보 삭제
    void disconnectManage(Map<String, String> sessionAttributes, String sessionId);

    //세션있는지 없는지 체크하는 메소드
    //userName으로 userSessionMap에 있는 sessionId 반환하는 메소드
    String getSessionIdByUsername(String userName);

    //sessionAttributes로
    String getUsernameBySessionId(Map<String, String> sessionAttributes, String sessionId);

    //채팅방에서 유저 삭제
    //구체적인 처리는 handleRoom메소드가 처리
    void removeUserAndManageRooms(User user);

    /*채팅방 관리
    1. 해당 유저 삭제
    상대방 존재 여부에 따라 상대방 웹소켓 해제 또는 채팅방 삭제*/
    void handleRoom(RandomRoom room, long userId);

    //파일 삭제, 방 삭제 하는 메소드
    void deleteFilesAndRooms(long roomId);

    //웹소켓 강제 종료 메소드
    void disconnectWebSocket(String userName);
}