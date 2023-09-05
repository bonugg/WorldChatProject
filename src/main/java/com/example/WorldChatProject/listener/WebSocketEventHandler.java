package com.example.WorldChatProject.listener;

import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventHandler {

    private final WebSocketManager webSocketManager;
    private final UserService userService;
    private final UserRepository userRepository;

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
        log.warn("disconnect close status: {} ", event.getCloseStatus());
        System.out.println(headerAccessor);

        Map<String, Object> messageHeaders = headerAccessor.getMessageHeaders();
        Map<String, String> sessionAttributes = (Map<String, String>) messageHeaders.get("simpSessionAttributes");

        String sessionId = headerAccessor.getSessionId();

        log.info("disconnect sessionAttributes : {}", sessionAttributes);
        log.info("disconnect sessionId: {}", sessionId);

        //웹소켓 해제 이벤트 발생하면 WebSocketManger에게 정보를 전달하여
        //웹소켓 해제 프로세스 처리
        webSocketManager.disconnectManage(sessionAttributes, sessionId);
        

        }

}


