package com.example.WorldChatProject.cateChat.controller;

import com.example.WorldChatProject.cateChat.dto.CateRoomDTO;
import com.example.WorldChatProject.cateChat.entity.CateChat;
import com.example.WorldChatProject.cateChat.entity.CateRoom;
import com.example.WorldChatProject.cateChat.service.CateChatService;
import com.example.WorldChatProject.cateChat.service.CateRoomService;
import com.example.WorldChatProject.cateChat.service.CateUserListService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CateRoomController {

    private final CateRoomService cateRoomService;
    private final CateChatService cateChatService;
    private final CateUserListService cateUserListService;

    @ResponseBody
    @GetMapping("/cateChatApi")
    public PrincipalDetails user2(Authentication authentication) {
        //principal 안에는 유저의 정보가 담겨있음
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return principal;
    }

    //로그인하기
    @GetMapping("/login")
    public String login() {
        return "login_page";
    }

    //입장 가능한 모든 채팅방 목록 가져오기
    @GetMapping("/cateChat")
    public String cateChatRoomList(Model model) {
        List<CateRoom> cateRoomList = cateRoomService.getAllCateChatRoom();
        model.addAttribute("cateRoomList", cateRoomList);
        //채팅방 생성 순서를 최근순으로 반환
        Collections.reverse(cateRoomList);
        return "cateChat";
    }

    //채팅방 생성 페이지로 이동
    @GetMapping("/cateChat/createRoom")
    public String createRoom() {
        return "cateRoomPlus";
    }

    //채팅방 만들기
    @PostMapping("/cateChat/createCateRoom")
    //@ModelAttribute CateRoom cateRoom은
    //HTTP POST 요청으로 전송된 데이터를 CateRoom 객체에 바인딩하는 역할
    public String createCateRoom(@ModelAttribute CateRoom cateRoom) {
        cateRoomService.createCateRoom(cateRoom);

        return "redirect:/cateChat";
    }

    //채팅방 입장
    @GetMapping("/cateChat/{cateId}")
    public String cateChat(@PathVariable String cateId,@RequestParam(value = "userName", required = false)String userName, Model model) {
        Long cateRoomId = Long.parseLong(cateId);

        CateRoom cateRoom = cateRoomService.getChatRoom(cateRoomId);

        List<CateChat> cateChatList = cateChatService.getMessagesByCateRoom(cateRoomId);

        cateUserListService.save(cateRoomId, userName);

        List<String> userList = cateUserListService.findAllUserNamesByCateId(cateRoomId);

//        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//        String username = principal.getUsername();

        model.addAttribute("cateUserList", userList);
        model.addAttribute("cateRoom", cateRoom);
        model.addAttribute("cateChatList", cateChatList);


        return "cateChatting";
    }

    //채팅방 나가기
    @GetMapping("/leave/{cateId}")
    public void leaveUser(@PathVariable String cateId, @RequestParam(value = "userName", required = false)String userName) {
        cateRoomService.minusUserCnt(Long.valueOf(cateId));
        cateChatService.deleteCateChatByCateId(cateId);
        cateUserListService.deleteCateUserList(userName);
    }
}
