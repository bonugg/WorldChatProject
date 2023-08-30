package com.example.WorldChatProject.webChat.config;

import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.webChat.rtc.LoginWebsocketHandler;
import com.example.WorldChatProject.webChat.rtc.SignalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket // 웹 소켓에 대해 자동 설정
@RequiredArgsConstructor
public class WebRtcConfig implements WebSocketConfigurer {
    /* TODO WebRTC 관련 */
    private final SignalHandler signalHandler;
    private final LoginWebsocketHandler loginWebsocketHandler;

    // signal 로 요청이 왔을 때 아래의 WebSockerHandler 가 동작하도록 registry 에 설정
    // 요청은 클라이언트 접속, close, 메시지 발송 등에 대해 특정 메서드를 호출한다
    @Override
    @CrossOrigin
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalHandler, "/signal")
                .setAllowedOrigins("*");
        registry.addHandler(loginWebsocketHandler, "/test")
                .setAllowedOrigins("*");
        registry.addHandler(signalHandler, "/voice")
				.setAllowedOrigins("*");
    }

    // 웹 소켓에서 rtc 통신을 위한 최대 텍스트 버퍼와 바이너리 버퍼 사이즈를 설정한다?
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(520000);
        container.setMaxBinaryMessageBufferSize(520000);
        return container;
    }
}