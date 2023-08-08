package com.example.WorldChatProject.cateChat.controller;

import com.example.WorldChatProject.cateChat.dto.CateChatDTO;
import com.example.WorldChatProject.cateChat.entity.CateChat;
import com.example.WorldChatProject.cateChat.entity.CateRoom;
import com.example.WorldChatProject.cateChat.service.CateChatService;
import com.example.WorldChatProject.cateChat.service.CateRoomService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class CateChatController {

    private final CateChatService cateChatService;
    private final CateRoomService cateRoomService;


    @MessageMapping("/categoryChat/{cateId}/sendMessage")
    @SendTo("/topic/{cateId}")
    public CateChatDTO sendMessage(@Payload CateChatDTO cateChatDTO, @PathVariable String cateId) {

//            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

//            String username = principal.getUsername();
//            cateChatDTO.setSender(username);

        cateChatDTO.setSender(cateChatDTO.getUsername());

        // 현재 시간 가져오기
        LocalDateTime now = LocalDateTime.now();

        // 시간 값을 "HH:mm" 형식으로 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);

        cateChatDTO.setCateChatRegdate(formattedTime);

        //메시지 db에 저장 후 저장된 곳에서 sender값을 가져와 username에 설정하기
        CateChatDTO savedMessage = cateChatService.saveMessage(cateChatDTO);
        savedMessage.setUsername(savedMessage.getSender());

        return savedMessage;
    }



    @MessageMapping("/categoryChat/{cateId}/addUser")
    @SendTo("/topic/{cateId}")
    //@Payload => 클라이언트에서 보낸 메시지 데이터가 자동으로 CateChatDTO 객체로 매핑된다
    public CateChatDTO addUser(@Payload CateChatDTO cateChatDTO, SimpMessageHeaderAccessor headerAccessor) {
//            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

//            String username = principal.getUsername();
//            cateChatDTO.setSender(username);

        String userName = cateChatDTO.getSender();
        // 채팅방 유저+1
        cateRoomService.plusUserCnt(cateChatDTO.getCateId(), userName);

        cateChatDTO.setUsername(userName);


        // 현재 시간 가져오기
        LocalDateTime now = LocalDateTime.now();

        // 시간 값을 "HH:mm" 형식으로 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);

        cateChatDTO.setCateChatRegdate(formattedTime);

        CateChatDTO savedMessage = cateChatService.saveMessage(cateChatDTO);
        savedMessage.setUsername(savedMessage.getSender());


        return savedMessage;

    }




}
