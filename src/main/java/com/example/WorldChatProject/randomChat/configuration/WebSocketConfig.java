package com.example.WorldChatProject.randomChat.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // STOMP를 사용하여 대화형 웹소켓 연결을 하는 endpoint 등록 및 stomp sub/pub endpoint 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        //stomp (채팅방)접속 주소 url(=/ws-stomp)
        registry.addEndpoint("/random-chat/{roomId}") //연결될 endpoint
                .withSockJS();  //SocketJs 연결 설정

    }

    //클라이언트와 서버 사이의 메시지 전송을 처리
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        //메시지 구독 요청 url (메시지 받을 때)
        registry.enableSimpleBroker("/sub");
        //메시지 발행 요청 url(메시지 보낼 때)
        registry.setApplicationDestinationPrefixes("/pub");
    }


}
