package com.example.WorldChatProject.listener;

import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListenner {
    private final RandomRoomService randomRoomService;
    private final UserRepository userRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();
    //웹소켓 연결 시 userSessionMap의 키에는 userName, value에는 sessionId가 들어감.
    //sessionId는 스프링에서 웹소켓 세션 Id를 자동으로 생성하고 관리함 따라서 header에 직접 넣지 않아도 괜찮음

    // 웹소켓 연결 이벤트 발생 시
    @EventListener
    public void handleWebSocketConnectLinstenner(SessionConnectedEvent event) {
        log.info("====================WebSocket Connect Event occcured==================");
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> messageHeaders = headerAccessor.getMessageHeaders();
        GenericMessage genericMessage = (GenericMessage)messageHeaders.get("simpConnectMessage");
        Map<String, Object> genericMessageHeaders = genericMessage.getHeaders();
        JSONObject jsonObject = new JSONObject(genericMessageHeaders);
        Object simpSessionAttributes = jsonObject.get("simpSessionAttributes");
        JSONObject jsonSessionAttributes = new JSONObject((Map) simpSessionAttributes);

        log.info("connect sessionAttributes : {}", jsonSessionAttributes);
        String userName = jsonSessionAttributes.get("userName").toString();
        String sessionId = headerAccessor.getSessionId();
        userSessionMap.put(userName, sessionId);
    }

    // 웹소켓 연결 실패 시 발생되는 이벤트
    @EventListener
    public void handleWebSocketFailureListener(SessionConnectEvent event) {
        log.info("====================WebSocket Connect Failure Event occurred==================");
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> messageHeaders = headerAccessor.getMessageHeaders();
        Map<String, String> sessionAttributes = (Map<String, String>) messageHeaders.get("simpSessionAttributes");
        log.info("disconnect sessionAttributes : {}", sessionAttributes);
        System.out.println(headerAccessor);

        String userName = sessionAttributes.get("userName");
        String sessionId = headerAccessor.getSessionId();

        //userSessionMap에 sessionId 없으면 sessionId랑 userName추가.
        if(!userSessionMap.containsKey(sessionId)) {
            userSessionMap.put(userName, sessionId);
        }

    }

    public static <K, V> K getKey(Map<K, V> map, V value) {
        for(K key : map.keySet()) {
            if(value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }

    //세션 종료 시
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("====================WebSocket Disconnect Event occcured==================");
        System.out.println("event.getMessage: " + event.getMessage());
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> messageHeaders = headerAccessor.getMessageHeaders();
        Map<String, String> sessionAttributes = (Map<String, String>) messageHeaders.get("simpSessionAttributes");
        log.info("disconnect sessionAttributes : {}", sessionAttributes);

        String userName = null;
        String sessionId = headerAccessor.getSessionId();

        if (sessionAttributes == null) {
            //웹소켓 연결 실패로 sessionAttribute가 널일 때
            //userName과 sessionId를 userSessionMap에서 가지고 오자.
            getKey(userSessionMap, sessionId);
            userName = sessionAttributes.get("userName");
        }

                // 유저 조회
        Optional<User> userOptional = userRepository.findByUserName(userName);
        if (!userOptional.isPresent()) {
            log.error("User not found");
            return;
        }
        log.info("{} is disconnected", userName);
        userSessionMap.remove(userName);
        User user = userOptional.get();

        //채팅방 조회
        RandomRoom room = randomRoomService.findRoomByUserId(user.getUserId());
        if (room == null) {
            log.error("Random room not found");
            return;
        }

        long roomId = room.getRandomRoomId();
        long userId = user.getUserId();

        // 채팅방에서 유저 삭제
        randomRoomService.removeUserFromRoom(userId, roomId);

        //채팅방에 상대방 있는지 조회
        Optional<User> other = randomRoomService.findUserFromRoom(room);
        if (other.isPresent()) {
            //채팅방에 상대방 존재하므로 상대방의 웹소켓 연결 해제
            //해제되면 자동으로 웹소켓 disconnection event발생되어 이 메소드 다시 실행됨.
            disconnectUser(other.get().getUserName());
        } else {
            //채팅방에 아무도 없으므로 채팅방 삭제
            randomRoomService.deleteRoom(roomId);
        }


    }

    private void disconnectUser(String userName) {
        log.info("====================Forced WebSocket Disconnect==================");
        log.info("{}'s websocket disconnection is processing", userName);
        //웹소켓 연결 종료 처리를 위한 헤더 객체 생성
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);

        // 원하는 속성 설정
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("userName", userName);
        sessionAttributes.put("sessionId", userSessionMap.get(userName));
        headerAccessor.setSessionAttributes(sessionAttributes);
        headerAccessor.setSessionId(userSessionMap.get(userName));
        log.info("headerAccessor: {}", headerAccessor);

        // 헤더를 사용하여 메시지 생성
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());

        // 연결 종료 정보 설정
        CloseStatus closeStatus = new CloseStatus(1000, "disconnect user");

        // 이벤트 발생
        eventPublisher.publishEvent(new SessionDisconnectEvent(this, message, userName, closeStatus));
    }

}
