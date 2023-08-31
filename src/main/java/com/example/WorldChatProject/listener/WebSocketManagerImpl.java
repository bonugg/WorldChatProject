package com.example.WorldChatProject.listener;

import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomFileService;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketManagerImpl implements WebSocketManager{
    private final RandomRoomService randomRoomService;
    private final UserRepository userRepository;
    private final RandomFileService randomFileService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

    //WebSocketHandler로부터 전달 받은 웹소켓 연결 정보를 userSessionMap에 저장
    @Override
    public void connectManange(Map<String, String> sessionAttributes, String sessionId) {
        
        String userName = sessionAttributes.get("userName");

        if(!userSessionMap.containsValue(sessionId)) {
            //userSessionMap 중복체크 -> userSessionMap에 없으면 넣어줌
            userSessionMap.put(userName, sessionId);
        }

        for(Map.Entry<String, String> entry : userSessionMap.entrySet()) {
            //확인 차 출력 용
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

    }

    //웹소켓 끊겼을 때 방 삭제 관리
    public void disconnectManage(Map<String, String> sessionAttributes, String sessionId) {
        String userName = null;

        if (sessionAttributes == null) {
            //웹소켓 연결 실패로 sessionAttribute가 널일 때
            //userName과 sessionId를 userSessionMap에서 가지고 오자.
            userName = getKey(userSessionMap, sessionId);
        }else {
            userName = sessionAttributes.get("userName");
        }

        Optional<User> userOptional = userRepository.findByUserName(userName);
        if (!userOptional.isPresent()) {
            log.error("User not found");
            return;
        }
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
            //채팅방에 아무도 없음
            //채팅방에 존재하는 파일들 삭제
            randomFileService.deleteFilesFromBucket(roomId);
            randomFileService.deleteFilesByRoomIdFromDB(roomId);
            //채팅방 삭제
            randomRoomService.deleteRoom(roomId);
        }


    }

    //웹소켓 해제
    @Override
    public void disconnectUser(String userName) {
        log.info("====================Forced WebSocket Terminated==================");
        Optional<User> userOptional = userRepository.findByUserName(userName);
        if (!userOptional.isPresent()) {
            log.error("User not found");
            return;
        }
        User user = userOptional.get();

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
