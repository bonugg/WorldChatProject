package com.example.WorldChatProject.webChat.rtc;

import com.example.WorldChatProject.frdChat.service.FrdChatMessageService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.webChat.dto.UserSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class LoginWebsocketHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    UserSessionManager manager = UserSessionManager.getInstance();
    private final UserRepository userRepository;


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

    public void sendMessageToUser(String username, String message) {
        System.out.println("이건 핸들러 메시지 : " + message);
        System.out.println("이건 핸들러 유저닉네임 : " + username);
        WebSocketSession session = manager.getUserSession(username);
        System.out.println("핸들러에서 해당 유저의 세션이 존재하는지? : " + session);
        if(session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 소켓 연결되었을 때 이벤트 처리
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("웹소켓 연결 됐지롱");
        System.out.println("들어온 세션 정보" + session);
        String userName = getUserNameFromSession(session);
        if (userName != null && !userName.trim().isEmpty()) {
//            userSessionMapByUsername.put(userName, session);
            if(manager.getUserSession(userName)!=null){
                manager.removeUserSession(userName);
                log.info("삭제됨");
            }
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
