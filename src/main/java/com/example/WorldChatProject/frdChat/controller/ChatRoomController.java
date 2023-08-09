package com.example.WorldChatProject.frdChat.controller;

import com.example.WorldChatProject.frdChat.dto.ResponseDTO;
import com.example.WorldChatProject.frdChat.entity.ChatRoom;
import com.example.WorldChatProject.frdChat.service.ChatMessageService;
import com.example.WorldChatProject.frdChat.service.ChatRoomService;
import com.example.WorldChatProject.friends.service.FriendsService;
import com.example.WorldChatProject.user.dto.UserDTO;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final UserService userService;
    private final FriendsService friendsService;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody UserDTO userDTO, Authentication authentication) {
        ResponseDTO<Map<String, String>> response = new ResponseDTO();

        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            User user = principal.getUser().DTOToEntity();
            System.out.println(user + "이건 로그인한 유저");
            System.out.println(userDTO + "엑시오스에서 넘겨준 값");
            User user2 = userService.findById(userDTO.DTOToEntity().getUserId());
            System.out.println(user2 + "채팅 신청 받은 유저저저저저저");
            ChatRoom checkChatRoom = chatRoomService.findByFriends1AndFriends2(user, user2);
            ChatRoom checkChatRoom2 = chatRoomService.findByFriends1AndFriends2(user2, user);
            Map<String, String> returnMap = new HashMap<>();
            if(checkChatRoom == null && checkChatRoom2 == null) {
                ChatRoom chatRoom = new ChatRoom();
                chatRoom.setFriends1(user);
                chatRoom.setFriends2(user2);
                chatRoom.setCreatedAt(LocalDateTime.now());

                chatRoomService.save(chatRoom);

                ChatRoom chatRoom2 = new ChatRoom();
                chatRoom2.setFriends1(user2);
                chatRoom2.setFriends2(user);
                chatRoom2.setCreatedAt(LocalDateTime.now());

                chatRoomService.save(chatRoom2);
                returnMap.put("msg", "room created");
            } else {
                returnMap.put("msg", "room already exists");
            }
            response.setItem(returnMap);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/chatroom-list")
    public ResponseEntity<?> getChatroomList(Authentication authentication) {
        ResponseDTO<ChatRoom> response = new ResponseDTO<>();
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            User user = principal.getUser().DTOToEntity();
//            List<ChatRoom> chatRoomList = chatRoomService.findByFriends1AndStatement(user, ACTIVATED);
            List<ChatRoom> chatRoomList = chatRoomService.findByFriends1(user);
            System.out.println(chatRoomList + "나오냐?");
            response.setItems(chatRoomList);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getChatRoom(@PathVariable Long roomId) {
        ResponseDTO<ChatRoom> response = new ResponseDTO<>();
        System.out.println(roomId + "이게 음 채팅방 아이디");
        try {
            ChatRoom chatRoom = chatRoomService.findById(roomId);
            System.out.println(chatRoom+"이건뭐가 좀 나오냐아아ㅏ");

            response.setItem(chatRoom);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


}
