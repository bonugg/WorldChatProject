package com.example.WorldChatProject.frdChat.controller;

import com.example.WorldChatProject.frdChat.entity.ChatMessage;
import com.example.WorldChatProject.frdChat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    private ChatMessage receivePublicMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }


    @MessageMapping("/ws")
    public void receivePrivateMessage (ChatMessage chatMessage) {
        chatService.saveLog(chatMessage);
        System.out.println(chatMessage.getRoomId());
        template.convertAndSend("/topic/" + chatMessage.getRoomId(), chatMessage);
    }


}
