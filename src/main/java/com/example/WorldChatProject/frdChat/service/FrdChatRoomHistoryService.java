package com.example.WorldChatProject.frdChat.service;

import com.example.WorldChatProject.frdChat.dto.FrdChatRoomHistoryDTO;
import com.example.WorldChatProject.frdChat.entity.FrdChatRoomHistory;
import com.example.WorldChatProject.frdChat.dto.FrdChatUpdateMessage;
import com.example.WorldChatProject.frdChat.repository.FrdChatRoomHistoryRepository;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FrdChatRoomHistoryService {

    private final FrdChatRoomHistoryRepository frdChatRoomHistoryRepository;
    private final FrdChatRoomService frdChatRoomService;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    //채팅방에 접속한 사람 넣기
    public void enteredRoom(String user, long roomId, String sessionId) {
        FrdChatRoomHistoryDTO frdChatRoomHistoryDTO = new FrdChatRoomHistoryDTO();
        frdChatRoomHistoryDTO.setUserNickName(user);
        frdChatRoomHistoryDTO.setRoomId(roomId);
        frdChatRoomHistoryDTO.setSessionId(sessionId);
        frdChatRoomHistoryRepository.save(frdChatRoomHistoryDTO.dTOToEntity());
    }

    //접속정보 삭제
    public void leaveRoom(String userNickName, String sessionId) {
        FrdChatRoomHistory enterInfo = frdChatRoomHistoryRepository.findByUserNickNameAndSessionId(userNickName, sessionId);

        if(enterInfo != null) {
            frdChatRoomHistoryRepository.delete(enterInfo);
        }
    }

    //채팅방에 접속 했을때 해당하는 채팅방의 상대가 누군지 알아내기
    public FrdChatRoomHistory checkOtherUser(long roomId, String userNickName) {
        Optional<FrdChatRoomHistory> checkOtherUser = frdChatRoomHistoryRepository.findByRoomIdAndUserNickName(roomId, userNickName);
        System.out.println("FrdChatRoomHistoryService.checkOtherUser 실행됌");
        if(checkOtherUser.isEmpty()) {
            return null;
        } else {
            return checkOtherUser.get();
        }
    }

    //내가 접속했을때 상대에게 접속했다고 알려주는 메소드 -> 이벤트 리스너와 연결돼서 실시간으로 알려준다.
    public void detectOtherUser(Long roomId, Long userId) {
        //1. 접속한 채팅방의 상대가 누군지 확인
        Long otherUserId = frdChatRoomService.getOtherUser(roomId, userId);
        User otherUser = userService.findById(otherUserId);
        String otherUserNickName = otherUser.getUserNickName();
        //2. 상대의 접속 기록 체크하기
        FrdChatRoomHistory history = checkOtherUser(roomId, otherUserNickName);
        if(history == null) {
            //3. 없으면 offline
            applicationEventPublisher.publishEvent(new FrdChatUpdateMessage("offline", roomId));
        } else {
            //4. 있으면 알지?
            applicationEventPublisher.publishEvent(new FrdChatUpdateMessage("online", roomId));
        }
    }

}

