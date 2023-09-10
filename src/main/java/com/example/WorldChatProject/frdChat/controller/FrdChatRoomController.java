package com.example.WorldChatProject.frdChat.controller;

import com.example.WorldChatProject.frdChat.dto.ResponseDTO;
import com.example.WorldChatProject.frdChat.entity.FrdChatRoom;
import com.example.WorldChatProject.frdChat.service.FrdChatMessageService;
import com.example.WorldChatProject.frdChat.service.FrdChatRoomService;
import com.example.WorldChatProject.friends.entity.Friends;
import com.example.WorldChatProject.friends.service.FriendsService;
import com.example.WorldChatProject.user.dto.UserDTO;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chat")
public class FrdChatRoomController {

    private final UserService userService;
    private final FriendsService friendsService;
    private final FrdChatRoomService frdChatRoomService;
    private final FrdChatMessageService frdChatMessageService;

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody UserDTO userDTO, Authentication authentication) {
        ResponseDTO<Map<String, Object>> response = new ResponseDTO();

        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            User user = principal.getUser().DTOToEntity();
            User user2 = userService.findById(userDTO.DTOToEntity().getUserId());
//            FrdChatRoom checkFrdChatRoom = frdChatRoomService.findByFriends1OrFriends2(user, user2);
            FrdChatRoom checkFrdChatRoom2 = frdChatRoomService.findByFriends1AndFriends2(user2, user);
            FrdChatRoom checkFrdChatRoom3 = frdChatRoomService.findByFriends1AndFriends2(user, user2);
            Map<String, Object> returnMap = new HashMap<>();
                if(checkFrdChatRoom2 == null && checkFrdChatRoom3 == null) {
                    FrdChatRoom frdChatRoom = new FrdChatRoom();
                    frdChatRoom.setFriends1(user);
                    frdChatRoom.setFriends2(user2);
                    frdChatRoom.setCreatedAt(LocalDateTime.now());
                    frdChatRoomService.save(frdChatRoom);
                    returnMap.put("msg", "room created");
                } else {
                    returnMap.put("msg", "room already exists");
                }

            FrdChatRoom checkFrdChatRoom = frdChatRoomService.findRoomByFriends1OrFriends2(user, user2);
            FrdChatRoom frdChatRoom = frdChatRoomService.findById(checkFrdChatRoom.getId());

            returnMap.put("userNickName", principal.getUser().getUserNickName());
            returnMap.put("userProfile", principal.getUser().getUserProfileName());
            returnMap.put("userProfileOther", user2.getUserProfileName());
            returnMap.put("chatroom", frdChatRoom.entityToDTO());
            response.setItem(returnMap);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }
    }


//    @GetMapping("/chatroom-list")
//    public ResponseEntity<?> getChatroomList(Authentication authentication) {
//        ResponseDTO<FrdChatRoom> response = new ResponseDTO<>();
//        try {
//            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//            User user = principal.getUser().DTOToEntity();
////            List<ChatRoom> chatRoomList = chatRoomService.findByFriends1AndStatement(user, ACTIVATED);
////            List<FrdChatRoom> frdChatRoomList = frdChatRoomService.findByFriends1(user);
//            List<FrdChatRoom> frdChatRoomList = frdChatRoomService.findChatRoomByUser(user);
//            System.out.println(frdChatRoomList + "채팅방리스트나오냐?");
//            response.setItems(frdChatRoomList);
//            response.setStatusCode(HttpStatus.OK.value());
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            response.setErrorMessage(e.getMessage());
//            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @GetMapping("/{roomId}")
//    public ResponseEntity<?> getChatRoom(@PathVariable Long roomId) {
//        ResponseDTO<FrdChatRoom> response = new ResponseDTO<>();
//        System.out.println(roomId + "이게 음 채팅방 아이디");
//        try {
//            FrdChatRoom frdChatRoom = frdChatRoomService.findById(roomId);
//            System.out.println(frdChatRoom +"이건뭐가 좀 나오냐아아ㅏ.채팅방조오오옴");
//            response.setItem(frdChatRoom);
//            response.setStatusCode(HttpStatus.OK.value());
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//            response.setErrorMessage(e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }


}
