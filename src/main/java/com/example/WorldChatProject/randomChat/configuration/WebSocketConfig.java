package com.example.WorldChatProject.randomChat.configuration;

import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // 채팅방에 접속한 사용자들의 정보를 저장
    private final Map<String, Set<String>> connectedUsers = new ConcurrentHashMap<>();

    // STOMP를 사용하여 대화형 웹소켓 연결을 하는 endpoint 등록 및 stomp sub/pub endpoint 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        //stomp (채팅방)접속 주소 url(=/random-chat/roomId)
        registry.addEndpoint("/random/room") //연결될 endpoint
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    public PrincipalDetails user(Authentication authentication, Map<String, Object> attributes, HttpServletRequest request) {
                        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
                        if (principal != null) {
                            String roomId = ((ServletServerHttpRequest) request).getServletRequest().getRequestURI().split("/")[2];
                            attributes.put("roomId", roomId);
                            connectedUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(principal.getUsername());
                            //connectedUser key: roomId && connectedUser set: userId
                            //roomId에 해당하는 사용자 집합이 connectedUsers에 없으면 새로운 집합을 생성하고 사용자 ID를 추가
                            //있다면 해당 집합에 사용자 ID를 추가
                            // roomId에 해당하는 채팅방에서 접속한 사용자의 ID를 connectedUsers에 추가
                        }
                        return principal;
                    }
                })
                .withSockJS();  //SocketJs 연결 설정
    }

    //클라이언트와 서버 사이의 메시지 전송을 처리
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        //메시지 구독 요청 url (메시지 받을 때)
        registry.enableSimpleBroker("/randomSub");
        //메시지 발행 요청 url(메시지 보낼 때)
        registry.setApplicationDestinationPrefixes("/randomPub");
    }

    //세션 종료 시
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) sha.getSessionAttributes().get("username");
        String roomId = (String) sha.getSessionAttributes().get("roomId");
        Set<String> usersInRoom = connectedUsers.get(roomId);
        if (usersInRoom != null) {
            usersInRoom.remove(username);
        }
    }


}
