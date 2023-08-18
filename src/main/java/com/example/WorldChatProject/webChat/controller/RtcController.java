//package com.example.WorldChatProject.webChat.controller;
//
//import com.example.WorldChatProject.webChat.dto.WebSocketMessage;
//import com.example.WorldChatProject.webChat.service.ChatService.RtcChatService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@Slf4j
//public class RtcController {
//
//    private final RtcChatService rtcChatService;
//
//    @PostMapping("/webrtc/usercount")
//    public String webRTC(@ModelAttribute WebSocketMessage webSocketMessage) {
//        log.info("MESSAGE : {}", webSocketMessage.toString());
//        return Boolean.toString(rtcChatService.findUserCount(webSocketMessage));
//    }
//
//
//}
//
