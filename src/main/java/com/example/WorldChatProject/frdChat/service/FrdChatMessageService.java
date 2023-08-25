package com.example.WorldChatProject.frdChat.service;

import com.example.WorldChatProject.frdChat.dto.FrdChatMessageDTO;
import com.example.WorldChatProject.frdChat.dto.ResponseDTO;
import com.example.WorldChatProject.frdChat.entity.FrdChatMessage;
import com.example.WorldChatProject.frdChat.entity.FrdChatRoomHistory;
import com.example.WorldChatProject.frdChat.entity.FrdChatUpdateMessage;
import com.example.WorldChatProject.frdChat.repository.FrdChatMessageRepository;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FrdChatMessageService {

    private final FrdChatMessageRepository frdChatMessageRepository;
    private final FrdChatRoomService frdChatRoomService;
    private final UserService userService;
    private final FrdChatRoomHistoryService frdChatRoomHistoryService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void save(FrdChatMessage frdChatMessage) {
        frdChatMessageRepository.save(frdChatMessage);
    }

    public List<FrdChatMessage> getChatMessages(Long roomId) {
        return frdChatMessageRepository.findByRoomId(roomId);
    }

    //상대가 들어왔을때 내 메시지 상태 안읽음 -> 읽음 만들기
    public List<FrdChatMessage> updateCheckRead(long roomId, String userNickName, boolean statement) {
       List<FrdChatMessage> unReadList = findMessages(roomId, userNickName, statement);

       for(FrdChatMessage message : unReadList) {
           message.setCheckRead(true);
       }

       frdChatMessageRepository.saveAll(unReadList);

       return unReadList;
    }

    //내 안읽힌 메시지 찾기
    private List<FrdChatMessage> findMessages(long roomId, String userNickName, boolean statement) {
        return frdChatMessageRepository.findByRoomIdAndSenderAndCheckRead(roomId, userNickName, statement);
    }

    //읽음처리 api. 원래 controller에서 작성하던거 service로 빼봤다.
    public ResponseEntity<?> updateRead(Long roomId, Long userId) {
        ResponseDTO<FrdChatMessage> responseDTO = new ResponseDTO<>();

        try {
            User user = userService.findById(userId);
            String userNickName = user.getUserNickName();
            //false인것만 true로 바꾸기 위해서 임의로 줬다
            boolean statement = false;
            List<FrdChatMessage> updatedList = updateCheckRead(roomId, userNickName, statement);
            responseDTO.setItems(updatedList);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e){
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
