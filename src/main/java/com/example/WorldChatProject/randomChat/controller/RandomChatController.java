package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.randomChat.dto.RandomChatDTO;
import com.example.WorldChatProject.randomChat.repository.RandomChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Slf4j
public class RandomChatController {
    //채팅을 수신(sub), 송신(pub) 하기 위한 Controller
    private final SimpMessagingTemplate template;
    @Autowired
    RandomChatRepository repository;

    @MessageMapping("/{randomRoomId}")
    public void sendMessage(@Payload RandomChatDTO chatDTO, @DestinationVariable("randomRoomId") String randomRoomId) {
        log.info("CHAT : {}", chatDTO);
        chatDTO.setContent(chatDTO.getContent());
        // /randomSub/randomRoomId 에 있는 구독 클라이언트에게 메시지 전송
        template.convertAndSend("/randomSub/" + randomRoomId, chatDTO);
    }

    @MessageMapping("/{randomRoomId}/enter")
    public void enter(@Payload RandomChatDTO chatDTO, @DestinationVariable("randomRoomId") String randomRoomId) {
        log.info("ENTER: {}님이 입장하셨습니다.", chatDTO.getSender());
        log.info("chatDTO: {}", chatDTO);
        template.convertAndSend("/randomSub/" + randomRoomId, chatDTO);
    }

    @MessageMapping("/{randomRoomId}/leave")
    public void leave(@Payload RandomChatDTO chatDTO, @DestinationVariable("randomRoomId") String randomRoomId){
        log.info("LEAVE: {}님이 퇴장하셨습니다.", chatDTO.getSender());
        template.convertAndSend("randomSub/" + randomRoomId, chatDTO);
    }

//    @MessageMapping("/chat/message")
//    public void message(ChatMessage message){
//        String name = message.getSender();
//        message.setSender(name);
//        if(ChatMessage.MessageType.ENTER.equals(message.getType())) {
//            message.setSender("[알림]");
//            message.setMessage(name + "님이 입장하셨습니다.");
//        }
//
//        log.info("Sender Name : " + name);
//        log.info("Meesage Content : " + message.getMessage());
//        log.info("RoomId : " + message.getRoomId());
//        log.info("Channel Topic : " + channelTopic.getTopic());
//        chatService.sendChatMessage(message);
//    }

//    @MessageMapping("/chat/enter")
//    public void enter(@Payload RandomChatDTO chatDTO, SimpMessageHeaderAccessor headerAccessor) {
//        repository.plusUserCnt(chatDTO.getRandomRoomId());
//        String userUUID = repository.addUser(chatDTO.getRandomRoomId(), chatDTO.getSender());
//
//        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
//        headerAccessor.getSessionAttributes().put("roomId", chatDTO.getRandomRoomId());
//
//        chatDTO.setContent(chatDTO.getSender() + "님이 입장하셨습니다.");
//        template.convertAndSend("/randomSub/chat/room" + chatDTO.getRandomRoomId(), chatDTO);
//    }


//
//    @EventListener
//    public void webSocketDisconnectListener(SessionDisconnectEvent event){
//        log.info("DisConnectEvent {}", event);
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
//        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
//        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
//
//        log.info("headAccessor {}", headerAccessor);
//
//        // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제
//        String username = repository.getUserName(roomId, userUUID);
//        repository.delUser(roomId, userUUID);
//
//        if (username != null) {
//            log.info("User Disconnected : " + username);
//
//            // builder 어노테이션 활용
//            RandomChatDTO chat = RandomChatDTO.builder()
//                    .type(MessageType.LEAVE)
//                    .sender(username)
//                    .message(username + " 님 퇴장이 퇴장하셨습니다.")
//                    .build();
//
//            template.convertAndSend("/sub/chat/room/" + roomId, chat);
//        }
//    }
//
//    //채팅에 참여한 유저 리스트 반환
//    @GetMapping("/randomChat/userList")
//    @ResponseBody
//    public ArrayList<String> userlist(String roomId) {
//        return repository.getUserList(roomId);
//    }
//
//    // 채팅에 참여한 유저 닉네임 중복 확인
//    @GetMapping("/chat/duplicateName")
//    @ResponseBody
//    public String isDuplicateName(@RequestParam("roomId") String roomId, @RequestParam("username") String username) {
//
//        // 유저 이름 확인
//        String userName = repository.isDuplicateName(roomId, username);
//        log.info("동작확인 {}", userName);
//
//        return userName;
//    }
}