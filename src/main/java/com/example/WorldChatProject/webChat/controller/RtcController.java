
package com.example.WorldChatProject.webChat.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.WorldChatProject.friends.entity.Friends;
import com.example.WorldChatProject.friends.service.FriendsService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.WorldChatProject.webChat.dto.ChatRoomDto;
import com.example.WorldChatProject.webChat.dto.ChatRoomMap;
import com.example.WorldChatProject.webChat.dto.RequestDto;
import com.example.WorldChatProject.webChat.dto.WebSocketMessage;
import com.example.WorldChatProject.webChat.service.ChatService.RtcChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RtcController {

    private final RtcChatService rtcChatService;
    private final UserService userService;
    private final FriendsService friendsService;

    @PostMapping("/webrtc/getFriendsList")
    public ResponseEntity<?> getFriendsList(@RequestBody String userName) throws NullPointerException{
        List<User> friends = new ArrayList<>();
        List<Friends> friendsList = friendsService.getFriendsByUserId(userService.findUserName(userName).orElseThrow().getUserId());
//        log.info("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"+friendsList);
        for(Friends f : friendsList){
            friends.add(f.getFriends());
        }
        log.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + userService.findUserName(userName).get());
        friends.add(userService.findUserName(userName).get());
        log.info("ssssssssssssssssssssss" + friends);
        return ResponseEntity.ok(friends);
    }

    @PostMapping("/webrtc/usercount")
    public String webRTC(@ModelAttribute WebSocketMessage webSocketMessage) {
        log.info("MESSAGE : {}", webSocketMessage.toString());
        return Boolean.toString(rtcChatService.findUserCount(webSocketMessage));
    }

    @PostMapping("/webrtc/logout")
    public void webRTCLogout(@RequestBody String userName) throws IOException {
        rtcChatService.RTCLogout(userName);
    }

    @PostMapping("/webrtc/request")
    public void requestRTC(@RequestBody RequestDto request) {
        log.info("접속 비동기 요청" + request.getType());
        if (request.getType().equals("online")) {
            System.out.println(rtcChatService.sendOnline(request.getSender()));
        } else {
            System.out.println("보낸이:" + request.getSender() + "받는이:" + request.getReceiver());
            System.out.println("요청 메시지!" + rtcChatService.sendRequest(request.getSender(), request.getReceiver(), request.getType()));
        }
    }

    @PostMapping("/webrtc/decline")
    public void declineRTC(@RequestBody RequestDto request) {
        rtcChatService.declineRTC(request.getSender(), request.getReceiver());
        String roomId = request.getRoomId();
        rtcChatService.exitRtcRoom(roomId);
    }

    @PostMapping("/online")
    public ResponseEntity<Boolean> online(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        boolean isOnline;
        if (rtcChatService.checkOnline(name)) {
            isOnline = true;
        } else {
            isOnline = false;
            log.info("유저 검색 결과: " + rtcChatService.checkOnline(name));
        }
        return ResponseEntity.ok(isOnline);
    }
//    @PostMapping("/webrtc/exitrooms")
//    public void exitRooms(@RequestBody String roomName){
//        rtcChatService.exitRtcRoom(roomName);
//    }


}

