package com.example.WorldChatProject.frdChat.controller;

import com.example.WorldChatProject.frdChat.dto.FrdChatMessageDTO;
import com.example.WorldChatProject.frdChat.dto.FrdChatRoomDTO;
import com.example.WorldChatProject.frdChat.dto.FrdChatRoomHistoryDTO;
import com.example.WorldChatProject.frdChat.dto.ResponseDTO;

import com.example.WorldChatProject.frdChat.entity.FrdChatMessage;
import com.example.WorldChatProject.frdChat.entity.FrdChatRoom;
import com.example.WorldChatProject.frdChat.entity.FrdChatRoomHistory;
import com.example.WorldChatProject.frdChat.entity.FrdChatUpdateMessage;
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

        //내가 접속한 방에 상대가 접속했는지 알아보기
        //1. 내가 접속한 방 번호가 어디지?
        long roomId = frdChatMessage.getRoomId();
        //2. 그럼 내 기준으로 그 방의 상대방은 누구지?
        long otherUserId = frdChatRoomService.getOtherUser(roomId, userId);

        User otherUser = userService.findById(otherUserId);
        //3. 상대방이 접속 해있는지 볼까?
        FrdChatRoomHistory checkHistory = frdChatRoomHistoryService.checkOtherUser(roomId, otherUser.getUserNickName());
        if(checkHistory == null) {
            frdChatMessage.setCheckRead(false);
        } else {
            frdChatMessage.setCheckRead(true);
        }
        frdChatMessageService.save(frdChatMessage);

        FrdChatMessageDTO frdChatMessageDTO = frdChatMessage.toFrdChatMessageDTO();
        frdChatMessageDTO.setUserProfile(userProfile);

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

    @PutMapping("/chatroom/{roomId}")
    @ResponseBody
    public ResponseEntity<?> updateRead(@PathVariable Long roomId, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long userId = principal.getUser().getUserId();
        return frdChatMessageService.updateRead(roomId, userId);
    }

    //상대 들어오는 이벤트 발생하면 발동!
    @EventListener
    public void handleUpdatedMessagesEvent(FrdChatUpdateMessage event) {
        String status = event.getMsg();
        Long roomId = event.getRoomId();
        if(status.equals("online")) {
            String statusMessage = "{\"type\":\"status\", \"content\":\"online\"}";
            template.convertAndSend("/frdSub/" + roomId, statusMessage);
        } else if (status.equals("offline")) {
            String statusMessage = "{\"type\":\"status\", \"content\":\"offline\"}";
            template.convertAndSend("/frdSub/" + roomId, statusMessage);
        }
    }

    @GetMapping("/chatroom/check-other/{roomId}")
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
