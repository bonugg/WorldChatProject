package com.example.WorldChatProject.frdChat.controller;

import com.example.WorldChatProject.frdChat.dto.chat.ChatDTO;
import com.example.WorldChatProject.frdChat.dto.chat.FrdChatMsgDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat")
    public void sendMessage(ChatDTO chatDTO, SimpMessageHeaderAccessor accessor) {
        simpMessagingTemplate.convertAndSend("/sub/chat/"+chatDTO.getChannelId(), chatDTO);
    }
}
