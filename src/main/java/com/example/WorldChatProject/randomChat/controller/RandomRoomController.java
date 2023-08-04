package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.WebSocketMessage;
import com.example.WorldChatProject.randomChat.dto.RandomRoomDTO;
import com.example.WorldChatProject.randomChat.dto.ResponseDTO;
import com.example.WorldChatProject.randomChat.entity.RandomChat;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.dto.UserDTO;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;

//@Controller
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/random")
@CrossOrigin(origins = "http://localhost:3001")
public class RandomRoomController{
    //채팅방을 조회, 생성, 입장을 관리하는 Controller
    private final RandomRoomService service;

    // 랜덤채팅 대기 및 입장 처리
    @PostMapping("/room")
        public RandomRoomDTO startRandomChat(@RequestBody UserDTO userDTO) {
//        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//        String username = principal.getUsername();
        System.out.println(userDTO.getUserName());
        log.info("랜덤채팅 시작 중");
        try{
            log.info("User {} requested random chat.", userDTO.getUserName());
            RandomRoom room = service.match(userDTO.getUserName());
            RandomRoomDTO randomRoomDTO = room.toDTO();
            randomRoomDTO.setSuccess(true);
            log.info("created room info : {}", randomRoomDTO.getRandomRoomName());
//            return ResponseEntity.status(HttpStatus.OK).body(randomRoomDTO);
            return randomRoomDTO;
        }catch (Exception e){
            log.info("not created randomRoom: {}", e.getMessage());
            RandomRoomDTO errorDTO = RandomRoomDTO.builder()
                                                  .isSuccess(false)
                                                  .errorMessage(e.getMessage())
                                                  .build();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
            return errorDTO;
        }
    }



}
