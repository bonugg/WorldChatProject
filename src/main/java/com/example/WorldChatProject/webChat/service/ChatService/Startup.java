package com.example.WorldChatProject.webChat.service.ChatService;

import com.example.WorldChatProject.webChat.dto.ChatRoomDto;
import com.example.WorldChatProject.webChat.dto.ChatRoomMap;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class Startup implements ApplicationRunner {
    private final ChatServiceMain chatServiceMain;
    public Startup(ChatServiceMain chatServiceMain) {
        this.chatServiceMain = chatServiceMain;
    }

    // 서버 시작 시 최초에 한 번만 동작하는 초기화 인터페이스
    @Override
    public void run(ApplicationArguments args) {
        
        // 실시간 알림을 구현해야 하기 때문에 로그인 시 즉시 웹소켓에 연결하기 위한 방 생성
        
        ChatRoomDto room = ChatRoomDto.builder()
                .roomId("loginUserRoom")
                .roomName("publicWebSocket")
//                .roomPwd(roomPwd) // 채팅방 패스워드
                .secretChk(false) // 채팅방 잠금 여부
                .userCount(0) // 채팅방 참여 인원수
                .maxUserCnt(100) // 최대 인원수 제한
                .build();

        //방을 만든 후 자체 맵에 저장 -> db 저장으로 변환 예정
        room.setUserList(new HashMap<String, String>());
        room.setChatType(ChatRoomDto.ChatType.MSG);
        ChatRoomMap.getInstance().getChatRooms().put(room.getRoomId(), room);
    }
}
