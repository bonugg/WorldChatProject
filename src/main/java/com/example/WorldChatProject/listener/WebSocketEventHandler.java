package com.example.WorldChatProject.listener;

import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventHandler {
    private final RandomRoomService randomRoomService;
    private final UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();
    //웹소켓 연결 시 userSessionMap의 키에는 userName, value에는 sessionId가 들어감.

    // 웹소켓 연결 이벤트 발생 시
    @EventListener
    public void handleWebSocketConnectLinstenner(SessionConnectEvent event) {
        log.info("====================WebSocket Connect Event occcured==================");
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> messageHeaders = headerAccessor.getMessageHeaders();
        Map<String, String> sessionAttributes = (Map<String, String>) messageHeaders.get("simpSessionAttributes");

        String userName = sessionAttributes.get("userName");
        String sessionId = headerAccessor.getSessionId();

        if(!userSessionMap.containsValue(sessionId)) {
            //userSessionMap 중복체크 -> userSessionMap에 없으면 넣어줌
            userSessionMap.put(userName, sessionId);
        }

        for(Map.Entry<String, String> entry : userSessionMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        log.info("connect sessionAttributes : {}", sessionAttributes);
        log.info("connect sessionId: {}", sessionId);
    }

    //세션 종료 시
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("====================WebSocket Disconnect Event occcured==================");
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> messageHeaders = headerAccessor.getMessageHeaders();
        Map<String, String> sessionAttributes = (Map<String, String>) messageHeaders.get("simpSessionAttributes");

        String userName = null;
        String sessionId = headerAccessor.getSessionId();

        log.info("disconnect sessionAttributes : {}", sessionAttributes);
        log.info("disconnect sessionId: {}", sessionId);

        if (sessionAttributes == null) {
            //웹소켓 연결 실패로 sessionAttribute가 널일 때
            //userName과 sessionId를 userSessionMap에서 가지고 오자.
            getKey(userSessionMap, sessionId);
            userName = sessionAttributes.get("userName");
        }else {
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
        log.info("====================Forced WebSocket Terminated==================");
        User user = userRepository.findByUserName(userName).get();
        if(user == null) {
            log.error("User not found");
            return;
        }

        //userSessionMap에 있는 사람만 disconnect 시킴
        if(!userSessionMap.containsValue(userName)) {
            log.warn("not found user in UserSessionMap");

        }

        //웹소켓 연결 종료 처리를 위한 헤더 객체 생성
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
        // 원하는 속성 설정
        Map<String, Object> newSessionAttributes = new HashMap<>();
        newSessionAttributes.put("userName", user.getUserName());
        newSessionAttributes.put("user",user.getUserNickName());
        headerAccessor.setSessionAttributes(newSessionAttributes);
        headerAccessor.setSessionId(userSessionMap.get(userName));

        log.info("Forced terminated sessionAttributes : {}", headerAccessor.getSessionAttributes());
        log.info("Forced terminated sessionId: {}", headerAccessor.getSessionId());

        // 헤더를 사용하여 메시지 생성
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());

        // 연결 종료 정보 설정
        CloseStatus closeStatus = new CloseStatus(1000, "disconnect user");

        // 이벤트 발생
        eventPublisher.publishEvent(new SessionDisconnectEvent(this, message, userName, closeStatus));
    }

    public static <K, V> K getKey(Map<K, V> map, V value) {
        for(K key : map.keySet()) {
            if(value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }

}


