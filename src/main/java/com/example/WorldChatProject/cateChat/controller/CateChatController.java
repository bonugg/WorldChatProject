package com.example.WorldChatProject.cateChat.controller;

import com.example.WorldChatProject.cateChat.dto.CateChatDTO;
import com.example.WorldChatProject.cateChat.entity.CateChat;
import com.example.WorldChatProject.cateChat.entity.CateRoom;
import com.example.WorldChatProject.cateChat.entity.CateUserList;
import com.example.WorldChatProject.cateChat.service.CateChatService;
import com.example.WorldChatProject.cateChat.service.CateRoomService;
import com.example.WorldChatProject.cateChat.service.CateUserListService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.Header;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CateChatController {

    private final CateChatService cateChatService;
    private final CateRoomService cateRoomService;
    private final CateUserListService cateUserListService;


    @MessageMapping("/categoryChat/{cateId}/sendMessage")
    @SendTo("/cateSub/{cateId}")
    public CateChatDTO sendMessage(@Payload CateChatDTO cateChatDTO, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        String user = (String) sessionAttributes.get("user");
        String userProfile = (String) sessionAttributes.get("userProfile");

        // 현재 시간 가져오기
        LocalDateTime now = LocalDateTime.now();

        // 시간 값을 "HH:mm" 형식으로 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);

        cateChatDTO.setCateChatRegdate(formattedTime);

        //메시지 db에 저장 후 저장된 곳에서 sender값을 가져와 username에 설정하기
        CateChatDTO savedMessage = cateChatService.saveMessage(cateChatDTO);
        savedMessage.setUserProfile(userProfile);
        savedMessage.setSender(user);

        return savedMessage;
    }


    @MessageMapping("/categoryChat/{cateId}/addUser")
    @SendTo("/cateSub/{cateId}")
    //@Payload => 클라이언트에서 보낸 메시지 데이터가 자동으로 CateChatDTO 객체로 매핑된다
    public Map<String, Object> addUser(@Payload CateChatDTO cateChatDTO, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) throws JsonProcessingException {

        String user = (String) sessionAttributes.get("user");
        log.info("addUser");
        // 채팅방 유저+1
        cateRoomService.plusUserCnt(cateChatDTO.getCateId());
        List<String> cateUserList = cateUserListService.findAllUserNamesByCateId(cateChatDTO.getCateId());

        cateChatDTO.setCateChatContent(user+" entered");

        // 현재 시간 가져오기
        LocalDateTime now = LocalDateTime.now();

        // 시간 값을 "HH:mm" 형식으로 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);

        cateChatDTO.setCateChatRegdate(formattedTime);

        CateChatDTO savedMessage = cateChatService.saveMessage(cateChatDTO);
        savedMessage.setSender(user);

        // 결과를 Map에 추가해서 반환
        Map<String, Object> result = new HashMap<>();
        result.put("cateChatDTO", savedMessage);
        result.put("cateUserList", cateUserList);

        return result;
    }

    @MessageMapping("/categoryChat/{cateId}/leaveUser")
    @SendTo("/cateSub/{cateId}")
    //@Payload => 클라이언트에서 보낸 메시지 데이터가 자동으로 CateChatDTO 객체로 매핑된다
    public Map<String, Object> leaveUser(@Payload CateChatDTO cateChatDTO, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) throws JsonProcessingException {

        String user = (String) sessionAttributes.get("user");
        String userName = (String) sessionAttributes.get("userName");
        log.info("leaveUser");
        // 채팅방 유저-1
        cateRoomService.minusUserCnt(cateChatDTO.getCateId());
        cateUserListService.delete(cateChatDTO.getCateId(), userName);
        List<String> cateUserList = cateUserListService.findAllUserNamesByCateId(cateChatDTO.getCateId());

        cateChatDTO.setCateChatContent(user + " has left");

        // 현재 시간 가져오기
        LocalDateTime now = LocalDateTime.now();

        // 시간 값을 "HH:mm" 형식으로 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);

        cateChatDTO.setCateChatRegdate(formattedTime);

        CateChatDTO savedMessage = cateChatService.saveMessage(cateChatDTO);
        savedMessage.setSender(user);

        Map<String, Object> result = new HashMap<>();
        result.put("cateChatDTO", savedMessage);
        result.put("cateUserList", cateUserList);

        return result;
    }
}