package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.listener.WebSocketManager;
import com.example.WorldChatProject.randomChat.dto.RandomRoomDTO;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/randomRoom")
public class RandomRoomController{
    //채팅방을 생성, 입장, 퇴장, 삭제를 관리하는 Controller
    private final RandomRoomService service;
    private final WebSocketManager webSocketManager;

    // 랜덤채팅 대기 및 입장 처리
    @PostMapping("/enter")
    public Map<String, Object> enter(Authentication authentication) {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        log.info("Start random Chat");
        try{
            log.info("User {} requested random chat.", principal.getUser().getUserNickName());

            User user = principal.getUser().DTOToEntity();
//            String sessionId = webSocketManager.getSessionIdByUsername(user.getUserName());
//            if(sessionId != null) {
//                //채팅방 생성 이전에 웹소켓 해제
//                webSocketManager.disconnectWebSocket(user.getUserName());
//            }
//            //웹소켓 세션이 없이 채팅방 유지되었을 경우 채팅방 삭제
            webSocketManager.removeUserAndManageRooms(user);

            Map<String, Object> result = new HashMap<>();

            RandomRoom room = service.matchStart(principal.getUsername());
            RandomRoomDTO randomRoomDTO = room.toDTO();
            log.info("created random room info : {}", randomRoomDTO.getRandomRoomId());
            result.put("randomRoomDTO", randomRoomDTO);
            result.put("userNickName", principal.getUser().getUserNickName());
            System.out.println(result);
            return result;

        }catch (Exception e){
            log.info("not created random room: {}", e.getMessage());
            RandomRoomDTO errorDTO = RandomRoomDTO.builder()
                    .errorMessage(e.getMessage())
                    .build();
            Map<String, Object> result = new HashMap<>();
            result.put("randomRoomDTO", errorDTO);
            result.put("userNickName", "");
            return result;
        }
    }

    @DeleteMapping("/leave")
    public Map<String, Object> leave(Authentication authentication) {
        //사용자의 정보를 authentication에서 받아온다.
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        log.info("End random Chat");
        Map<String, Object> result = new HashMap<>();
        try {
            User user = principal.getUser().DTOToEntity();
            String sessionId = webSocketManager.getSessionIdByUsername(user.getUserName());
            if (sessionId != null) {
                //채팅방 생성 이전에 웹소켓 해제
                webSocketManager.disconnectWebSocket(user.getUserName());
            }
            //웹소켓 세션이 없이 채팅방 유지되었을 경우 채팅방 삭제
            webSocketManager.removeUserAndManageRooms(user);
            result.put("status", "success");
            return result;

        } catch (Exception e) {
            log.info("not deleted random room: {}", e.getMessage());
            result.put("status", "error");
            return result;

        }
    }





}