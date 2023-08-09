package com.example.WorldChatProject.cateChat.controller;

import com.example.WorldChatProject.cateChat.WebSocketConfig;
import com.example.WorldChatProject.cateChat.dto.CateRoomDTO;
import com.example.WorldChatProject.cateChat.dto.ResponseDTO;
import com.example.WorldChatProject.cateChat.entity.CateChat;
import com.example.WorldChatProject.cateChat.entity.CateRoom;
import com.example.WorldChatProject.cateChat.service.CateChatService;
import com.example.WorldChatProject.cateChat.service.CateRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class CateRestController {

    private final WebSocketConfig webSocketConfig;
    private final CateRoomService cateRoomService;
    private final CateChatService cateChatService;

    //입장 가능한 모든 채팅방 목록 가져오기
//    @GetMapping("cateChat")
//    public ResponseEntity<?> cateChatRoomList() {
//        List<CateRoom> cateRoomList = cateRoomService.getAllCateChatRoom();
//        ResponseDTO<CateRoomDTO> responseDTO = new ResponseDTO<>();
//
//        List<CateRoomDTO> cateRoomDTOList = new ArrayList<>();
//
//        try {
//            for (CateRoom cateRoom : cateRoomList) {
//                cateRoomDTOList.add(cateRoom.toCateRoomDTO());
//            }
//            responseDTO.setItems(cateRoomDTOList);
//            responseDTO.setStatusCode(HttpStatus.OK.value());
//
//            return ResponseEntity.ok().body(responseDTO);
//
//        } catch (Exception e) {
//            responseDTO.setErrorMessage(e.getMessage());
//            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
//
//            return ResponseEntity.badRequest().body(responseDTO);
//        }
//
//    }

    //채팅방 입장
    @GetMapping("/cateChat/{cateId}/enter")
    public ResponseEntity<?> checkIfCateChatIsFull(@PathVariable String cateId,Model model) {
        CateRoom cateRoom = cateRoomService.getChatRoom(Long.parseLong(cateId));

        boolean isFull = cateRoom.getMaxUserCnt() <= cateRoom.getCateUserCnt();

        return ResponseEntity.ok(Collections.singletonMap("isFull", isFull));
    }

    //입장한 유저 수
    @GetMapping("/connected-users/{cateId}")
    public ResponseEntity<Long> getConnectedUsers(@PathVariable Long cateId) {
        CateRoom cateRoom = cateRoomService.getChatRoom(cateId);

        return ResponseEntity.ok(cateRoom.getCateUserCnt());
    }


//    @GetMapping("/cateChat/{cateId}/search")
//    public ResponseEntity<List<CateChat>> searchCateChat(@PathVariable String cateId, @RequestParam String keyword) {
//        List<CateChat> searchCateChat = cateChatService.searchByContent(Long.parseLong(cateId), keyword);
//
//        return ResponseEntity.ok(searchCateChat);
//    }


}
