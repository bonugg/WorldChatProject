package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.randomChat.dto.RandomRoomDTO;
import com.example.WorldChatProject.randomChat.dto.ResponseDTO;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/random")
@RequiredArgsConstructor
public class RandomRoomController {
    //채팅방을 조회, 생성, 입장을 관리하는 Controller
    private final RandomRoomService service;

    // 랜덤채팅 대기 및 입장 처리
    @PostMapping("/room")
    public ResponseEntity<?> startRandomChat(@AuthenticationPrincipal String username) {
        ResponseDTO<RandomRoomDTO> response = new ResponseDTO<>();
        try{
            RandomRoom room = service.match(username);
            RandomRoomDTO randomRoomDTO = room.toDTO();
            response.setItem(randomRoomDTO);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }
    }



}
