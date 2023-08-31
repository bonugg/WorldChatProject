package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.randomChat.dto.RandomChatDTO;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


@Controller
@RequiredArgsConstructor
@Slf4j
public class RandomChatController {
    //채팅을 수신(sub), 송신(pub) 하기 위한 Controller
    private final SimpMessagingTemplate template;
    private final RandomRoomService randomRoomService;

    @MessageMapping("/randomChat/{randomRoomId}")
    @SendTo("/randomChat/{randomRoomId}")
    public void sendMessage(@Payload RandomChatDTO chatDTO, @DestinationVariable("randomRoomId") String randomRoomId, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        String user = (String) sessionAttributes.get("user");
        long userId = (long) sessionAttributes.get("userId");
        chatDTO.setSender(user);
        chatDTO.setUserId(userId);
        log.info("CHAT : {}", chatDTO);
        chatDTO.setContent(chatDTO.getContent());
        // /randomSub/randomRoomId 에 있는 구독 클라이언트에게 메시지 전송
        template.convertAndSend("/randomSub/randomChat/" + randomRoomId, chatDTO);
    }

    @MessageMapping("/randomChat/{randomRoomId}/enter")
    @SendTo("/randomChat/{randomRoomId}")
    public void enter(@Payload RandomChatDTO chatDTO, @DestinationVariable("randomRoomId") String randomRoomId, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        String user = (String) sessionAttributes.get("user");
        chatDTO.setSender(user);
        RandomRoom room = randomRoomService.findRoomById(chatDTO.getRandomRoomId());
        String roomName = room.getRandomRoomName();
        chatDTO.setContent(roomName + " ROOM");
        log.info("{}: {}", chatDTO.getType(), chatDTO.getContent());
        template.convertAndSend("/randomSub/randomChat/" + chatDTO.getRandomRoomId(), chatDTO);
    }

    @MessageMapping("/randomChat/{randomRoomId}/leave")
    @SendTo("/randomChat/{randomRoomId}")
    public void leave(@Payload RandomChatDTO chatDTO, @DestinationVariable("randomRoomId") String randomRoomId, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        String user = (String) sessionAttributes.get("user");
        chatDTO.setSender(user);
        chatDTO.setContent("LEAVED ROOM");
        log.info("{}: {}", chatDTO.getType(), chatDTO.getContent());
        template.convertAndSend("/randomSub/randomChat/" + chatDTO.getRandomRoomId(), chatDTO);
    }

    public RandomChatDTO changeLike(@RequestBody RandomChatDTO randomChatDTO) {
        RandomChatDTO newRandomChatDTO = new RandomChatDTO();
        long randomChatId = randomChatDTO.getRandomChatId();
        String sender = randomChatDTO.getSender();
        int boforeCnt = randomChatDTO.getLikeCount();
        randomChatDTO.setLikeCount(boforeCnt + 1);

        log.info("{} like status changed by {}, count: {} ", randomChatId, sender, randomChatDTO.getLikeCount());

        return randomChatDTO;
    }

}