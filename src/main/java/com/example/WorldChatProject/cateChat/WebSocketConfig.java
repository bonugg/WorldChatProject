//package com.example.WorldChatProject.cateChat;
//
//
//import com.example.WorldChatProject.user.security.jwt.JwtAuthenticationFilter;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.event.EventListener;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//import org.springframework.web.util.UriComponents;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.io.IOException;
//import java.security.Principal;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Configuration
////Websocket 기반의 메시지 처리와 메시지 브로커를 활성화하는 데 사용하는 어노테이
//@EnableWebSocketMessageBroker
//@Slf4j
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//
//    //현재 연결되어 있는 유저 인원수 구하기
//    private final Map<String, Set<String>> connectedUsers = new ConcurrentHashMap<>();
//
//    public int getConnectedUsersSize(String roomId) {
//        Set<String> usersInRoom = connectedUsers.get(roomId);
//        return usersInRoom != null ? usersInRoom.size() : 0;
//    }
//
//
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        //메시지 브로커를 사용하여 /topic으로 시작하는(topic을 구독하는) 클라이언트들에게 메시지를 뿌림
//        config.enableSimpleBroker("/topic");
//
//        // 메시지를 발행하는 요청 url => 즉 메시지 보낼 때
//        // @MessageMapping 어노테이션이 적용된 메소드를 호출하기 위한 경로(prefix)설정
//        //클라이언트가 메시지를 보낼 때, 메시지 핸들러 메소드를 찾기 위한 경로(prefix)를 설정하는 부분
//        //클라이언트가 WebSocket을 통해 메시지를 보낼 때, "/app"을 접두어로 사용하는 경로가 지정
//        config.setApplicationDestinationPrefixes("/app");
//    }
//
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        log.info("시작");
//        registry.addEndpoint("/websocket-app/{roomId}")
//                .setHandshakeHandler(new DefaultHandshakeHandler() {
//
//                    @Override
//                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
//                        Principal principal = request.getPrincipal();
//                        if (principal != null) {
//                            String roomId = ((ServletServerHttpRequest) request).getServletRequest().getRequestURI().split("/")[2];
//                            attributes.put("roomId", roomId);
//                            connectedUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(principal.getName());
//                        }
//                        return principal;
//                    }
//                })
//                .withSockJS();
//    }
//
//
//    //Spring 프레임워크에서 이벤트가 발생할 때 해당 이벤트를 처리하기 위해 이벤트 핸들러 메서드를 정의할 수 있다
////    @EventListener
////    //SessionDisconnectEvent는 WebSocket 세션이 종료되었을 때 발생하는 Spring Framework의 이벤트
////    public void handleSessionDisconnect(SessionDisconnectEvent event) {
////        //StompHeaderAccessor는 STOMP(WebSocket) 메시지의 헤더를 쉽게 읽고 쓸 수 있도록 도와주는 클래스
////        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
////        //WebSocket 세션에 저장된 "username" 속성을 가져옴
////        String username = (String) sha.getSessionAttributes().get("username");
////        //WebSocket 세션에 저장된 "roomId" 속성을 가져옴
////        String roomId = (String) sha.getSessionAttributes().get("roomId");
////        Set<String> usersInRoom = connectedUsers.get(roomId);
////        if (usersInRoom != null) {
////            usersInRoom.remove(username);
////        }
////    }
//
//
//
//
//
//
//}
