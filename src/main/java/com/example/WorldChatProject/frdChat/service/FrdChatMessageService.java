package com.example.WorldChatProject.frdChat.service;
import com.example.WorldChatProject.frdChat.dto.FrdChatMessageDTO;
import com.example.WorldChatProject.configuration.ApplicationContextProvider;
import com.example.WorldChatProject.frdChat.dto.FrdChatUpdateMessage;
import com.example.WorldChatProject.frdChat.dto.ResponseDTO;
import com.example.WorldChatProject.frdChat.entity.FrdChatMessage;
import com.example.WorldChatProject.frdChat.repository.FrdChatMessageRepository;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FrdChatMessageService {

    private final FrdChatMessageRepository frdChatMessageRepository;
    private final FrdChatRoomService frdChatRoomService;
    private final UserService userService;
    private final UserRepository userRepository;
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

        for (FrdChatMessage message : unReadList) {
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

//            long unreadListNum = frdChatMessageRepository.countByRoomIdAndSenderAndCheckRead(roomId, userNickName, statement);
//            System.out.println("안읽은 메시지 개수 jpa" + unreadListNum);

            List<FrdChatMessage> updatedList = updateCheckRead(roomId, userNickName, statement);
//            long unreadNum = updatedList.size();
//            System.out.println("안읽은 메시지 개수 size()" + unreadNum);

            responseDTO.setItems(updatedList);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    public Long getUnreadCount(String nickName, String receiver, boolean statement) {

        return frdChatMessageRepository.countBySenderAndReceiverAndCheckRead(nickName, receiver, statement);
    }

    public List<FrdChatMessage> getUnreadMsgList(Long roomId, String otherUserNickName, boolean statement) {
        return frdChatMessageRepository.findByRoomIdAndSenderAndCheckRead(roomId, otherUserNickName, statement);
    }

    public void changeUnread(Long userId, Long roomId) {
        System.out.println("changeUnread 실행됌");
        //1. 해당 채팅방의 상대 알아내기
        Long otherUserId = frdChatRoomService.getOtherUser(roomId, userId);
        User otherUser = userService.findById(otherUserId);
        String otherUserNickName = otherUser.getUserNickName();

        //2. 해당 채팅방에서 상대가 보낸 메시지중 안읽은거를 읽음으로 처리
        //일단 안읽은 메시지 리스트를 찾는다.
        boolean statement = false;
        List<FrdChatMessage> unreadMsgList = getUnreadMsgList(roomId, otherUserNickName, statement);
        //바꿔준다
        for(FrdChatMessage msg : unreadMsgList) {
            msg.setCheckRead(true);
        }
        //저장한다
        List<FrdChatMessage> updatedMsgList = frdChatMessageRepository.saveAll(unreadMsgList);
        //이걸 이제 이벤트리스너에 보낸담에 리스너에서 프런트로 보내면 될듯?
        applicationEventPublisher.publishEvent(new FrdChatUpdateMessage("updated", roomId, updatedMsgList));
    }

    public FrdChatMessageDTO changeLikeStatus(FrdChatMessageDTO chatDTO) {
        long chatId = chatDTO.getId();
        boolean status;
        if (chatDTO.getLike().equals("on")) {
            status = true;
            chatDTO.setLiked(true);
        } else {
            status = false;
            chatDTO.setLiked(false);
        }
        log.info(String.valueOf(status));
        FrdChatMessage frdChatMessage = frdChatMessageRepository.findById(chatId).get();
        frdChatMessage.setLiked(status);
        frdChatMessageRepository.save(frdChatMessage);
        return chatDTO;
    }

    public void delete(List<FrdChatMessage> frdChatMessageList) {
        frdChatMessageRepository.deleteAll(frdChatMessageList);
    }
}

