package com.example.WorldChatProject.webChat.controller;

import com.example.WorldChatProject.webChat.dto.RequestDto;
import com.example.WorldChatProject.webChat.dto.UserSessionManager;
import com.example.WorldChatProject.webChat.dto.WebSocketMessage;
import com.example.WorldChatProject.webChat.service.ChatService.RtcChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.RequestDate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RtcController {

    private final RtcChatService rtcChatService;
    @PostMapping("/webrtc/usercount")
    public String webRTC(@ModelAttribute WebSocketMessage webSocketMessage) {
        log.info("MESSAGE : {}", webSocketMessage.toString());
        return Boolean.toString(rtcChatService.findUserCount(webSocketMessage));
    }
    @PostMapping("/webrtc/logout")
    public void webRTCLogout(String userName)throws IOException {
        rtcChatService.RTCLogout(userName);
    }
    @PostMapping("/webrtc/request")
    public void requestRTC(@RequestBody  RequestDto request){
        System.out.println("보낸이:" + request.getSender() + "받는이:" + request.getReceiver());
        System.out.println("요청 메시지!"+rtcChatService.sendRequest(request.getSender(), request.getReceiver()));
    }
    @PostMapping("/webrtc/exitrooms")
    public void exitRooms(String roomName){
        rtcChatService.exitRtcRoom(roomName);
    }


}

