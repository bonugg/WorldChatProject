package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.randomChat.dto.RandomRoomDTO;
import com.example.WorldChatProject.randomChat.dto.ResponseDTO;
import com.example.WorldChatProject.randomChat.entity.RandomChat;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/random")
public class RandomRoomController extends TextWebSocketHandler {
    //채팅방을 조회, 생성, 입장을 관리하는 Controller
    private final RandomRoomService service;

    // 랜덤채팅 대기 및 입장 처리
    @PostMapping("/room")
    public RandomRoomDTO startRandomChat(@RequestBody RandomChat message) {
        String username = message.getSender();
        System.out.println("랜덤채팅 시작 중");
//        String username = user.getUserName();
        try{
            log.info("User {} requested random chat.", username);
            RandomRoom room = service.match(username);
            RandomRoomDTO randomRoomDTO = room.toDTO();
            log.info("created room info : {}", randomRoomDTO.getRandomRoomName());
            return randomRoomDTO;
        }catch (Exception e){
            return RandomRoomDTO.builder()
                    .isSuccess(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }



}
