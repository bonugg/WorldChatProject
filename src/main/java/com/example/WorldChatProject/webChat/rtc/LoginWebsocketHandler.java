package com.example.WorldChatProject.webChat.rtc;

import com.example.WorldChatProject.webChat.dto.ChatRoomDto;
import com.example.WorldChatProject.webChat.dto.ChatRoomMap;
import com.example.WorldChatProject.webChat.dto.UserSessionManager;
import com.example.WorldChatProject.webChat.dto.WebSocketMessage;
import com.example.WorldChatProject.webChat.service.ChatService.ChatServiceMain;
import com.example.WorldChatProject.webChat.service.ChatService.RtcChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class LoginWebsocketHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    UserSessionManager manager = UserSessionManager.getInstance();

    // 연결 끊어졌을 때 이벤트처리
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("[ws] Session has been closed with status [{} {}]", status, session);
    }

    private String getUserNameFromSession(WebSocketSession session) {
        // 클라이언트에서 주소에 포함된 userName을 가져오는 방법
        String query = Objects.requireNonNull(session.getUri()).getQuery();
        if (query.isEmpty()) {
            return "";
        }
        // 쿼리 파라미터를 분석하여 userName 값 추출 (간단한 방법을 사용, 더 정교한 처리가 필요할 수 있음)
        Map<String, String> queryPairs = Arrays.stream(query.split("&"))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));

        String userName = queryPairs.get("userName");
        return userName;
    }

    // 소켓 연결되었을 때 이벤트 처리
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("웹소켓 연결 됐지롱");
        System.out.println("들어온 세션 정보" + session);
        String userName = getUserNameFromSession(session);
        if (userName != null && !userName.trim().isEmpty()) {
//            userSessionMapByUsername.put(userName, session);
            manager.addUserSession(userName, session);
            System.out.println("저장되는 이름: " + userName);
            System.out.println("로그인중인 유저: " + manager.getAllKeys());
        }

        // 소켓 메시지 처리

    }
//로그아웃 준비중..
//    public void webRTCLogout(String userName)throws IOException {
//        if (userName == null) {
//            throw new IllegalArgumentException("userName cannot be null");
//        }
//
//        WebSocketSession session = userSessionMapByUsername.get(userName);
//        if (session != null && session.isOpen()) {
//            session.close(); // 연결 끊기
//        }
//
//        userSessionMapByUsername.remove(userName);
//    }
}
