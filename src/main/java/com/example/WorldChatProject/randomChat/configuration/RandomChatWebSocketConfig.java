package com.example.WorldChatProject.randomChat.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.WorldChatProject.user.security.jwt.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class RandomChatWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // 채팅방에 접속한 사용자들의 정보를 저장
    private final Map<String, Set<String>> connectedUsers = new ConcurrentHashMap<>();
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint( "/random") //https://localhost:9002/random/
                .setAllowedOrigins("https://localhost:3001") //Cors 설정
                //.setAllowedOriginPatterns("https://localhost:3001")
                //.addInterceptors(new JwtHandshakeInterceptor())
                .withSockJS(); //SockJS 사용을 위한 설정
    }

    //클라이언트와 서버 사이의 메시지 전송을 처리
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        //메시지 구독 요청 url경로 등록(메시지 받을 때)
        // /randomSub으로 시작하는 메시지를 구독하는 클라이언트에게 해당 메시지 전달가능
        registry.enableSimpleBroker("/randomSub"); //topic
        //클라이언트의 메시지 발행 요청 url경로（요청주소 prefix) 등록(메시지 보낼 때)
        //클라이언트에서 서버로 메시지를 보낼 때
        registry.setApplicationDestinationPrefixes("/randomPub"); //app
    }

    //세션 종료 시
//    @EventListener
//    public void handleSessionDisconnect(SessionDisconnectEvent event) {
//        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
//        String username = (String) sha.getSessionAttributes().get("username");
//        String roomId = (String) sha.getSessionAttributes().get("roomId");
//        Set<String> usersInRoom = connectedUsers.get(roomId);
//        if (usersInRoom != null) {
//            usersInRoom.remove(username);
//        }
//    }

    private static class JwtHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Map<String, Object> attributes) {
            // Extract and validate the JWT token from the request headers
            String token = request.getHeaders().getFirst(JwtProperties.HEADER_STRING);
            if (token != null && token.startsWith(JwtProperties.TOKEN_PREFIX)) {
                try {
                    String jwtToken = token.replace(JwtProperties.TOKEN_PREFIX, "");
                    String userName = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                            .build()
                            .verify(jwtToken)
                            .getClaim("username")
                            .asString();

                    if (userName != null) {
                        attributes.put("username", userName);
                        return true;
                    }
                } catch (TokenExpiredException te){
                    log.info("만료된 토큰");
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                } catch (Exception e) {
                    // Token validation failed
                }
            }
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Exception exception) {
        }

    }


}
