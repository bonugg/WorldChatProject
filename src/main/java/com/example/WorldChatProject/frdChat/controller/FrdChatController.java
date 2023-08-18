package com.example.WorldChatProject.frdChat.controller;

import com.example.WorldChatProject.frdChat.dto.FrdChatMessageDTO;
import com.example.WorldChatProject.frdChat.dto.ResponseDTO;

import com.example.WorldChatProject.frdChat.entity.FrdChatMessage;
import com.example.WorldChatProject.frdChat.service.FrdChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FrdChatController {

    private final SimpMessagingTemplate template;
    private final FrdChatMessageService frdChatMessageService;
    private Map<String, String> sessions = new HashMap<>();

    @MessageMapping("/friendchat")
    public void receivePrivateMessage (FrdChatMessage frdChatMessage, SimpMessageHeaderAccessor accessor) {
        String sender = sessions.get(accessor.getSessionId());
        frdChatMessage.setSender(sender);
        System.out.println(sender + "보낸사람보낸사람보낸사람보낸사람보낸사람보낸사람보낸사람보낸사람");
        frdChatMessageService.save(frdChatMessage);

        System.out.println(frdChatMessage.getRoomId() + "채팅컨트롤러의 방아이디 찍히게");
        log.info(String.valueOf(frdChatMessage));

        template.convertAndSend("/frdSub/" + frdChatMessage.getRoomId(), frdChatMessage);
    }

    @GetMapping("/chatroom/{roomId}")
    @ResponseBody
    public ResponseEntity<?> getChatMessages(@PathVariable Long roomId) {
        ResponseDTO<FrdChatMessage> response = new ResponseDTO<>();
        try {
            List<FrdChatMessage> frdChatMessageList = frdChatMessageService.getChatMessages(roomId);
            log.info(frdChatMessageList.toString() + "나와줘제발...");
            response.setItems(frdChatMessageList);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @EventListener(SessionConnectEvent.class)
    public void onConnect(SessionConnectEvent event) {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        System.out.println(sessionId);
        log.info("세션 아이디래요~~" + sessionId);
        String headersString = event.getMessage().getHeaders().get("nativeHeaders").toString();
        System.out.println("헤더헤더헤더" + headersString);

        String[] splitByUser = headersString.split("User=\\[");
        if (splitByUser.length > 1) {
            String userId = splitByUser[1].split("]")[0];
            System.out.println(userId);
            log.info("유저 아이디래요~~", userId);
            sessions.put(sessionId, userId);
        } else {
            log.warn("Unable to extract userId from nativeHeaders: {}", headersString);
        }
    }

    @EventListener(SessionDisconnectEvent.class)
    public void onDisconnect(SessionDisconnectEvent event) {
        sessions.remove(event.getSessionId());
    }

}
