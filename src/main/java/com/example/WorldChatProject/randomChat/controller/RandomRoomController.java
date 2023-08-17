package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.randomChat.dto.RandomRoomDTO;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/random")
public class RandomRoomController{
    //채팅방을 생성, 입장, 퇴장, 삭제를 관리하는 Controller
    private final RandomRoomService service;

    // 랜덤채팅 대기 및 입장 처리
    @PostMapping("/room")
        public RandomRoomDTO enter(@RequestBody UserDTO userDTO) {
        log.info("Start random Chat");
        try{
            log.info("User {} requested random chat.", userDTO.getUserNickName());
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
