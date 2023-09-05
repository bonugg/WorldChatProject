package com.example.WorldChatProject.webChat.dto;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;


// Stomp 를 통해 pub/sub 를 사용하면 구독자 관리가 알아서 된다!!
// 따라서 따로 세션 관리를 하는 코드를 작성할 필도 없고,
// 메시지를 다른 세션의 클라이언트에게 발송하는 것도 구현 필요가 없다!
@Data
@Builder
@EqualsAndHashCode
@Getter
@Setter
public class ChatRoomDto {

    private String roomId; // 채팅방 아이디
    private String roomName; // 채팅방 이름 
    private int userCount; // 채팅방 인원수
    private int maxUserCnt; // 채팅방 최대 인원 제한

    private String roomPwd; // 채팅방 삭제시 필요한 pwd
    private boolean secretChk; // 채팅방 잠금 여부
    public enum ChatType{  // 화상 채팅, 문자 채팅
        MSG, RTC, VOI
    }
    private ChatType chatType; //  채팅 타입 여부

    private Map<String, ?> userList;

}
