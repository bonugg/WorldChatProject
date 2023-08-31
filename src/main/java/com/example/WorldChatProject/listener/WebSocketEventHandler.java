package com.example.WorldChatProject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventHandler {

    private final WebSocketManager webSocketManager;

    // 웹소켓 연결 이벤트 발생 시
    @EventListener
    public void handleWebSocketConnectLinstenner(SessionConnectEvent event) {
        log.info("====================WebSocket Connect Event occcured==================");
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> messageHeaders = headerAccessor.getMessageHeaders();
        Map<String, String> sessionAttributes = (Map<String, String>) messageHeaders.get("simpSessionAttributes");
        
        String sessionId = headerAccessor.getSessionId();

        log.info("connect sessionAttributes : {}", sessionAttributes);
        log.info("connect sessionId: {}", sessionId);

        //웹소켓 연결 이벤트 발생하면 WebSocketManger에게 정보를 전달.
        //WebSocketManager는 전달 받은 웹소켓 연결 정보를 userSessionMap에 저장
        webSocketManager.connectManange(sessionAttributes, sessionId);

    }

    //웹소켓 연결 종료 시
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("====================WebSocket Disconnect Event occcured==================");
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> messageHeaders = headerAccessor.getMessageHeaders();
        Map<String, String> sessionAttributes = (Map<String, String>) messageHeaders.get("simpSessionAttributes");

        String sessionId = headerAccessor.getSessionId();

        log.info("disconnect sessionAttributes : {}", sessionAttributes);
        log.info("disconnect sessionId: {}", sessionId);

        //웹소켓 해제 이벤트 발생하면 WebSocketManger에게 정보를 전달.
        webSocketManager.disconnectManage(sessionAttributes, sessionId);
    }

}


