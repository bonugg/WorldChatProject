package com.example.WorldChatProject.frdChat.controller;

import com.example.WorldChatProject.frdChat.dto.FrdChatMessageDTO;
import com.example.WorldChatProject.frdChat.dto.ResponseDTO;

import com.example.WorldChatProject.frdChat.entity.FrdChatMessage;
import com.example.WorldChatProject.frdChat.service.FrdChatMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FrdChatController {

    private final SimpMessagingTemplate template;
    private final FrdChatMessageService frdChatMessageService;

    @MessageMapping("/friendchat")
    public void receivePrivateMessage (FrdChatMessage frdChatMessage, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        String user = (String) sessionAttributes.get("user");
        String userProfile = (String) sessionAttributes.get("userProfile");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);

        frdChatMessage.setCreatedAt(formattedTime);
        frdChatMessage.setSender(user);
        frdChatMessageService.save(frdChatMessage);
        System.out.println(frdChatMessage.getRoomId() + "채팅컨트롤러의 방아이디 찍히게");
        FrdChatMessageDTO frdChatMessageDTO = frdChatMessage.toFrdChatMessageDTO();
        frdChatMessageDTO.setUserProfile(userProfile);

        template.convertAndSend("/frdSub/" + frdChatMessage.getRoomId(), frdChatMessageDTO);
    }

    @GetMapping("/chatroom/{roomId}")
    @ResponseBody
    public ResponseEntity<?> getChatMessages(@PathVariable Long roomId) {
        ResponseDTO<FrdChatMessage> response = new ResponseDTO<>();
        try {
            List<FrdChatMessage> frdChatMessageList = frdChatMessageService.findByRoomId(roomId);
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
}
