package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.randomChat.dto.RandomChatDTO;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;


@Controller
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "https://localhost:3001")
public class RandomChatController {
    //채팅을 수신(sub), 송신(pub) 하기 위한 Controller
    private final SimpMessagingTemplate template;
    private final RandomRoomService randomRoomService;

    @MessageMapping("/randomChat/{randomRoomId}")
    @SendTo("/randomChat/{randomRoomId}")
    public void sendMessage(@Payload RandomChatDTO chatDTO, @DestinationVariable("randomRoomId") String randomRoomId) {
        log.info("CHAT : {}", chatDTO);
        chatDTO.setContent(chatDTO.getContent());
        // /randomSub/randomRoomId 에 있는 구독 클라이언트에게 메시지 전송
        template.convertAndSend("/randomSub/randomChat/" + randomRoomId, chatDTO);
    }

    @MessageMapping("/randomChat/{randomRoomId}/enter")
    @SendTo("/randomChat/{randomRoomId}")
    public void enter(@Payload RandomChatDTO chatDTO, @DestinationVariable("randomRoomId") String randomRoomId) {
        RandomRoom room = randomRoomService.find(chatDTO.getRandomRoomId());
        String roomName = room.getRandomRoomName();
        chatDTO.setContent(roomName + "에 입장하셨습니다.");
        log.info("{}: {}", chatDTO.getType(), chatDTO.getContent());
        template.convertAndSend("/randomSub/randomChat/" + randomRoomId, chatDTO);
    }


}