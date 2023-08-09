package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.randomChat.dto.RandomRoomDTO;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        System.out.println(userDTO.getUserName());
        log.info("랜덤채팅 시작 중");
        try{
            log.info("User {} requested random chat.", userDTO.getUserName());
            RandomRoom room = service.match(userDTO.getUserName());
            RandomRoomDTO randomRoomDTO = room.toDTO();
            randomRoomDTO.setSuccess(true);
            log.info("created random room info : {}", randomRoomDTO.getRandomRoomName());
            return randomRoomDTO;
        }catch (Exception e){
            log.info("not created random room: {}", e.getMessage());
            RandomRoomDTO errorDTO = RandomRoomDTO.builder()
                                                  .isSuccess(false)
                                                  .errorMessage(e.getMessage())
                                                  .build();
            return errorDTO;
        }
    }



}
