package com.example.WorldChatProject.frdChat.controller;

import com.example.WorldChatProject.frdChat.dto.FrdChatMessageDTO;
import com.example.WorldChatProject.frdChat.dto.ResponseDTO;

import com.example.WorldChatProject.frdChat.entity.FrdChatMessage;


import com.example.WorldChatProject.frdChat.entity.FrdChatRoomHistory;
import com.example.WorldChatProject.frdChat.dto.FrdChatUpdateMessage;
import com.example.WorldChatProject.frdChat.service.FrdChatMessageService;
import com.example.WorldChatProject.frdChat.service.FrdChatRoomHistoryService;
import com.example.WorldChatProject.frdChat.service.FrdChatRoomService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@RestController
@RequiredArgsConstructor
@Slf4j
public class FrdChatController {

    private final SimpMessagingTemplate template;
    private final FrdChatMessageService frdChatMessageService;

    private final FrdChatRoomHistoryService frdChatRoomHistoryService;
    private final FrdChatRoomService frdChatRoomService;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @MessageMapping("/friendchat")
    public void receivePrivateMessage (FrdChatMessage frdChatMessage, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes,
                                       SimpMessageHeaderAccessor accessor) {

        String user = (String) sessionAttributes.get("user");
        String userProfile = (String) sessionAttributes.get("userProfile");
        Long userId = (Long) sessionAttributes.get("userId");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);

        frdChatMessage.setCreatedAt(formattedTime);
        frdChatMessage.setSender(user);

        //이건 현재 채팅치는 상황에서 읽음 안읽음을 나타내준다. 상대가없을때 보낸 메시지들의 상태 변환은 이벤트 리스너로 처리
        //내가 접속한 방에 상대가 접속했는지 알아보기
        //1. 내가 접속한 방 번호가 어디지?
        long roomId = frdChatMessage.getRoomId();
        //2. 그럼 내 기준으로 그 방의 상대방은 누구지?
        long otherUserId = frdChatRoomService.getOtherUser(roomId, userId);
        User otherUser = userService.findById(otherUserId);
        frdChatMessage.setReceiver(otherUser.getUserName());
        //3. 상대방이 접속 해있는지 볼까?
        FrdChatRoomHistory checkHistory = frdChatRoomHistoryService.checkOtherUser(roomId, otherUser.getUserNickName());
        if(checkHistory == null) {
            frdChatMessage.setCheckRead(false);
        } else {
            frdChatMessage.setCheckRead(true);
        }
        //세팅된걸로 저장도하고
        frdChatMessageService.save(frdChatMessage);

        FrdChatMessageDTO frdChatMessageDTO = frdChatMessage.toFrdChatMessageDTO();
        frdChatMessageDTO.setUserProfile(userProfile);
        //메시지도 보낸다.
        template.convertAndSend("/frdSub/" + frdChatMessage.getRoomId(), frdChatMessageDTO);
    }

    @GetMapping("/chatroom/{roomId}")
    @ResponseBody
    public ResponseEntity<?> getChatMessages(@PathVariable Long roomId) {
        ResponseDTO<FrdChatMessage> response = new ResponseDTO<>();
        try {
            List<FrdChatMessage> frdChatMessageList = frdChatMessageService.getChatMessages(roomId);
            response.setItems(frdChatMessageList);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    //안읽음 -> 읽음으로 표시한후 리스트 반환하는 api
    @PutMapping("/chatroom/{roomId}")
    @ResponseBody
    public ResponseEntity<?> updateRead(@PathVariable Long roomId, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long userId = principal.getUser().getUserId();
        return frdChatMessageService.updateRead(roomId, userId);
    }

    //상대 들어오는 이벤트 발생하면 발동!(상대가 들어오면 frdChatRoomHistoy.detecOtherUser가 실행됌. 그걸 채팅방에 전달)
    @EventListener
    public void handleUpdatedMessagesEvent(FrdChatUpdateMessage event) {
        String status = event.getMsg();
        Long roomId = event.getRoomId();
        //json형태로 만들어서 보내줘야한다. 그래서 type과 content를 지정해서 보내준다.
        if(status.equals("online")) {
            String statusMessage = "{\"type\":\"status\", \"content\":\"online\"}";
            template.convertAndSend("/frdSub/" + roomId, statusMessage);
        } else if (status.equals("offline")) {
            String statusMessage = "{\"type\":\"status\", \"content\":\"offline\"}";
            template.convertAndSend("/frdSub/" + roomId, statusMessage);
        }
    }

    //이거 필요없어짐. 원래 내가 접속했을때 상대가 없으면 메시지 상태를 상대 메시지의 상태를 false로 주려했지만, 상대가 들어오는걸 실시간으로 감지하게 만들었기 때문에
    @GetMapping("/chatroom/check-other/{roomId}")
    @ResponseBody
    public ResponseEntity<?> checkOther(@PathVariable Long roomId, Authentication authentication) {
        System.out.println("이게 문제일수도?" + roomId);
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        long userId = principal.getUser().getUserId();

        try {
            //1. 첨에 상대가 누군지 알아내고
            Long otherUserId = frdChatRoomService.getOtherUser(roomId, userId);
            User otherUser = userService.findById(otherUserId);
            //2. 상대의 히스토리가 있는지 확인
            FrdChatRoomHistory checkHistory = frdChatRoomHistoryService.checkOtherUser(roomId, otherUser.getUserNickName());
            Map<String, String> returnMap = new HashMap<>();
            if(checkHistory == null) {
                returnMap.put("msg", "friend is offline");
            } else {
                returnMap.put("msg", "friend is online");
            }
            responseDTO.setItem(returnMap);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/chatroom/unread-messages")
    @ResponseBody
    public ResponseEntity<?> getUnreadCount(@RequestBody User user, Authentication authentication) {
        System.out.println("유저아이디 넘어옴?" + user.getUserNickName());
        //내가 로그인했을때 안읽은 메시지니까 넘어온 아이디가 보낸사람이다...
        String senderNickName = user.getUserNickName();
        System.out.println(senderNickName + "보낸사람 닉네임");
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String receiver = principal.getUser().getUserName();
        System.out.println(receiver + "받은사람 아이디");

        return frdChatMessageService.getUnreadCount(senderNickName, receiver);
    }

    @EventListener(SessionConnectEvent.class)
    public void onConnect(SessionConnectEvent event) {
        log.info("=========웹소켓 연결=========");
    }

    @EventListener(SessionDisconnectEvent.class)
    public void onDisconnect(SessionDisconnectEvent event) {
        //1. 채팅방에 접속한 사람 넣기

        log.info("======웹소켓 연결 종료======");
    }
}
