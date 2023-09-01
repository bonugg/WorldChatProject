package com.example.WorldChatProject.listener;

import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomFileService;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketManagerImpl implements WebSocketManager {
    private final RandomRoomService randomRoomService;
    private final UserRepository userRepository;
    private final RandomFileService randomFileService;
    private final ApplicationEventPublisher eventPublisher;

    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();
    //userSessionMap[ username: sessionId ]

    //WebSocketHandler로부터 전달 받은 웹소켓 연결 정보를 userSessionMap에 저장
    @Override
    public void connectManange(Map<String, String> sessionAttributes, String sessionId) {

        String userName = sessionAttributes.get("userName");
        if (!userSessionMap.containsValue(sessionId)) {
            //userSessionMap 중복체크 -> userSessionMap에 없으면 넣어줌
            userSessionMap.put(userName, sessionId);
        }

        log.info("User Session Map: {}", userSessionMap);
    }

    //WebSocketHandler로부터 전달 받은 웹소켓 연결 정보를 userSessionMap에서 삭제
    @Override
    public void disconnectManage(Map<String, String> sessionAttributes, String sessionId) {
        String userName = (sessionAttributes == null) ? getKey(userSessionMap, sessionId)
                : sessionAttributes.get("userName");

        Optional<User> userOptional = userRepository.findByUserName(userName);
        if (!userOptional.isPresent()) {
            log.error("User not found");
            return;

    }
        userSessionMap.remove(userName);
        log.info("User Session Map: {}", userSessionMap);
        if(userSessionMap.containsValue(sessionId)) {
            removeUserAndManageRooms(userOptional.get());
        }

    }

    //채팅방에서 유저 삭제
    @Override
    public void removeUserAndManageRooms(User user) {
        //채팅방 조회
        List<RandomRoom> rooms = randomRoomService.findAllRoomByUserId(user.getUserId());

        if (rooms == null) {
            log.error("Random room not found");
            return;
        }

        for (RandomRoom room : rooms) {
            handleRoom(room, user.getUserId());
        }
    }


    //채팅방 관리.
    @Override
    public void handleRoom(RandomRoom room, long userId) {

        long roomId = room.getRandomRoomId();
        
        //해당 유저를 채팅방에서 삭제
        randomRoomService.removeUserFromRoom(userId, roomId);
        
        Optional<User> other = randomRoomService.findUserFromRoom(room);

        if (other.isPresent()) {
            //채팅방에 상대방 존재하면 다른 사람 웹소켓 연결 해제
            User user = other.get();
            String otherName = user.getUserName();
            String otherSessionId = getSessionIdByUsername(otherName);
            disconnectWebSocket(otherName, otherSessionId);
        } else {
            //채팅방에 상대방 존재하지 않으면 채팅파일과 채팅방 삭제
            deleteFilesAndRooms(roomId);
        }
    }

    //파일 삭제, 방 삭제하는 메소드
    @Override
    public void deleteFilesAndRooms(long roomId) {
        randomFileService.deleteFilesFromBucket(roomId);
        randomFileService.deleteFilesByRoomIdFromDB(roomId);
        randomRoomService.deleteRoom(roomId);
    }

    //웹소켓 강제 종료 메소드
    @Override
    public void disconnectWebSocket(String userName, String sessionId) {
        log.info("====================Forced WebSocket Terminated==================");
        Optional<User> userOptional = userRepository.findByUserName(userName);

        if (!userOptional.isPresent()) {
            return;
        }
        User user = userOptional.get();

        // 웹소켓 연결 종료 처리
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);

        Map<String, Object> newSessionAttributes = new HashMap<>();
        newSessionAttributes.put("userName", user.getUserName());
        newSessionAttributes.put("user", user.getUserNickName());

        headerAccessor.setSessionAttributes(newSessionAttributes);
        headerAccessor.setSessionId(sessionId);

        log.info("Forced terminated sessionAttributes : {}", headerAccessor.getSessionAttributes());

        // 헤더를 사용하여 메시지 생성
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());
        // 연결 종료 정보 설정
        CloseStatus closeStatus = new CloseStatus(1000, "disconnect user");

        // 이벤트 발생
        eventPublisher.publishEvent(new SessionDisconnectEvent(this, message, user.getUserName(), closeStatus));
    }

    //웹소켓 해재 이벤트로부터 받은 정보로 userName 반환 메소드
    public String getUsernameBySessionId(Map<String, String> sessionAttributes, String sessionId) {
        String userName = (sessionAttributes == null) ?
                getKey(userSessionMap, sessionId) : sessionAttributes.get("userName");
        return userName;
    }

    //userSessionMap에 해당 유저의 sessionId 체크
    @Override
    public String getSessionIdByUsername(String userName) {
        String sessionId = userSessionMap.get(userName);
        if(sessionId == null) {
            log.info("not found {}'s sessionId in UserSessionMap", userName);
            return null;
        }
        return sessionId;
    }


    //userSessionMap에서 userName을 조회하는 메소드
    private String getKey(Map<String, String> userSessionMap, String sessionId) {
        for (String userName : userSessionMap.keySet()) {
            if (sessionId.equals(userSessionMap.get(userName))) {
                return userName;
            }
        }
        return null;
    }


}
