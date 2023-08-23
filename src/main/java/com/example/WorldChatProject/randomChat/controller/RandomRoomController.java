package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.randomChat.dto.RandomRoomDTO;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.dto.UserDTO;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/randomRoom")
public class RandomRoomController{
    //채팅방을 생성, 입장, 퇴장, 삭제를 관리하는 Controller
    private final RandomRoomService service;

    // 랜덤채팅 대기 및 입장 처리
    @PostMapping("/enter")
        public RandomRoomDTO enter(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        log.info("Start random Chat");
        try{
            log.info("User {} requested random chat.", principal.getUser().getUserNickName());
            RandomRoom room = service.matchStart(principal.getUsername());
            RandomRoomDTO randomRoomDTO = room.toDTO();
            log.info("created random room info : {}", randomRoomDTO.getRandomRoomId());
            return randomRoomDTO;
        }catch (Exception e){
            log.info("not created random room: {}", e.getMessage());
            RandomRoomDTO errorDTO = RandomRoomDTO.builder()
                                                  .errorMessage(e.getMessage())
                                                  .build();
            return errorDTO;
        }
    }


}
