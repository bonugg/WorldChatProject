package com.example.WorldChatProject.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.WorldChatProject.frdChat.service.FrdChatRoomHistoryService;
import com.example.WorldChatProject.frdChat.service.FrdChatRoomService;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.repository.RandomRoomRepository;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.dto.UserDTO;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.security.jwt.JwtProperties;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final UserRepository userRepository;
    private final FrdChatRoomService frdChatRoomService;
    private final FrdChatRoomHistoryService frdChatRoomHistoryService;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint( "/random") //https://localhost:9002/random/
                .withSockJS(); //SockJS 사용을 위한 설정
        registry.addEndpoint("/CateChat")
                .withSockJS();
        registry.addEndpoint("/friendchat")
                .withSockJS();
    }


    //클라이언트와 서버 사이의 메시지 전송을 처리
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        //메시지 구독 요청 url경로 등록(메시지 받을 때)
        registry.enableSimpleBroker("/randomSub", "/cateSub","/frdSub"); //topic
        //클라이언트의 메시지 발행 요청 url경로（요청주소 prefix) 등록(메시지 보낼 때)
        registry.setApplicationDestinationPrefixes("/randomPub", "/catePub","/frdPub"); //app
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new StompHeaderChannelInterceptor());
    }

    @Order(Ordered.HIGHEST_PRECEDENCE + 99)
    class StompHeaderChannelInterceptor implements ChannelInterceptor {

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            StompCommand command = accessor.getCommand();

            if (command != null && command.equals(StompCommand.CONNECT)) {
                String token = accessor.getFirstNativeHeader("Authorization");

                log.info("Token: " + token);

                token = token.replace(JwtProperties.TOKEN_PREFIX, "");
                String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                        .getClaim("username").asString();

                if (username != null) {
                    UserDTO userDTO = userRepository.findByUserName(username).get().EntityToDTO();
                    //헤더에 유저 닉네임값을 저장 이후 각 컨트롤러에서 호출하여 어떤 사용자가 채팅을 보냈는지 구분
                    accessor.getSessionAttributes().put("userId", userDTO.getUserId());
                    accessor.getSessionAttributes().put("user", userDTO.getUserNickName());
                    accessor.getSessionAttributes().put("userName", userDTO.getUserName());
                    if(userDTO.getUserProfileName() != null ){
                        accessor.getSessionAttributes().put("userProfile", userDTO.getUserProfileName());
                    }
                }
                String friendChat = accessor.getFirstNativeHeader("friendChat");
                //1. 채팅방에 접속한 사람 넣기
                if(friendChat != null){
                    String userNickName = (String) accessor.getSessionAttributes().get("user");
                    Long roomId = Long.parseLong(accessor.getFirstNativeHeader("roomId"));
                    String sessionId = accessor.getSessionId();
                    frdChatRoomHistoryService.enteredRoom(userNickName, roomId, sessionId);

                    //2. 온.오프라인 상태 감지하기
                    Long userId = (Long) accessor.getSessionAttributes().get("userId");
                    frdChatRoomHistoryService.detectOtherUser(roomId, userId);
                }
            }
            else if (command !=null && command.equals(StompCommand.DISCONNECT)) {
                String userNickName = (String) accessor.getSessionAttributes().get("user");
                System.out.println("닉네임안들어오나? : " + userNickName);
                String sessionId = accessor.getSessionId();
                frdChatRoomHistoryService.leaveRoom(userNickName, sessionId);
            }

            return message;
        }
    }
}
