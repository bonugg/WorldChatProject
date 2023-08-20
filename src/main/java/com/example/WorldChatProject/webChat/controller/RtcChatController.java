
package com.example.WorldChatProject.webChat.controller;//package com.example.WorldChatProject.webChat.controller;

//
//import com.example.WorldChatProject.webChat.dto.ChatDTO;
//import com.example.WorldChatProject.webChat.dto.ChatRoomMap;
//import com.example.WorldChatProject.webChat.service.ChatService.ChatServiceMain;
//import com.example.WorldChatProject.webChat.service.ChatService.MsgChatService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//import java.util.ArrayList;
//
//
//@Slf4j
//@RequiredArgsConstructor
//@RestController
//public class RtcChatController {
//
//    // 아래에서 사용되는 convertAndSend 를 사용하기 위해서 서언
//    // convertAndSend 는 객체를 인자로 넘겨주면 자동으로 Message 객체로 변환 후 도착지로 전송한다.
//    private final SimpMessageSendingOperations template;
//
//    private final MsgChatService msgChatService;
//    private final ChatServiceMain chatServiceMain;
//
//    //로그린 후 즉시 웹소켓 연결 후 유저 목록 저장 -> db 저장으로 변경 예정
//    @PostMapping("/chat/addUser")
//    public void addUser(String userId){
//        msgChatService.addUser(ChatRoomMap.getInstance().getChatRooms(), "1", userId);
//    }
//
//    //기존에 있던 일반 채팅 코드
//    @MessageMapping("/chat/enterUser")
//    public void enterUser(@Payload ChatDTO chat, SimpMessageHeaderAccessor headerAccessor) {
//
//        // 채팅방 유저+1
//        chatServiceMain.plusUserCnt(chat.getRoomId());
//
//        // 채팅방에 유저 추가 및 UserUUID 반환
//        String userUUID = msgChatService.addUser(ChatRoomMap.getInstance().getChatRooms(), chat.getRoomId(), chat.getSender());
//
//        // 반환 결과를 socket session 에 userUUID 로 저장
//        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
//        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());
//
//        chat.setMessage(chat.getSender() + " 님 입장!!");
//        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
//    }
//
//    // 해당 유저
//    @MessageMapping("/chat/sendMessage")
//    public void sendMessage(@Payload ChatDTO chat) {
//        log.info("CHAT {}", chat);
//        chat.setMessage(chat.getMessage());
//        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
//
//    }
//
//    // 유저 퇴장 시에는 EventListener 을 통해서 유저 퇴장을 확인
//    @EventListener
//    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
//        log.info("DisConnEvent {}", event);
//
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
//        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
//        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
//
//        log.info("headAccessor {}", headerAccessor);
//
//        // 채팅방 유저 -1
//        chatServiceMain.minusUserCnt(roomId);
//
//        // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제
//        String username = msgChatService.findUserNameByRoomIdAndUserUUID(ChatRoomMap.getInstance().getChatRooms(), roomId, userUUID);
//        msgChatService.delUser(ChatRoomMap.getInstance().getChatRooms(), roomId, userUUID);
//
//        if (username != null) {
//            log.info("User Disconnected : " + username);
//
//            // builder 어노테이션 활용
//            ChatDTO chat = ChatDTO.builder()
//                    .type(ChatDTO.MessageType.LEAVE)
//                    .sender(username)
//                    .message(username + " 님 퇴장!!")
//                    .build();
//
//            template.convertAndSend("/sub/chat/room/" + roomId, chat);
//        }
//    }
//
//    // 채팅에 참여한 유저 리스트 반환
//    @GetMapping("/chat/userlist")
//    @ResponseBody
//    public ArrayList<String> userList(String roomId) {
//
//        return msgChatService.getUserList(ChatRoomMap.getInstance().getChatRooms(), roomId);
//    }
//
//    // 채팅에 참여한 유저 닉네임 중복 확인
//    @GetMapping("/chat/duplicateName")
//    @ResponseBody
//    public String isDuplicateName(@RequestParam("roomId") String roomId, @RequestParam("username") String username) {
//
//        // 유저 이름 확인
//        String userName = msgChatService.isDuplicateName(ChatRoomMap.getInstance().getChatRooms(), roomId, username);
//        log.info("동작확인 {}", userName);
//
//        return userName;
//    }
//}
