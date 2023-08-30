package com.example.WorldChatProject.webChat.service.ChatService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.WorldChatProject.webChat.dto.ChatRoomDto;
import com.example.WorldChatProject.webChat.dto.ChatRoomMap;
import com.example.WorldChatProject.webChat.dto.RequestDto;
import com.example.WorldChatProject.webChat.dto.UserSessionManager;
import com.example.WorldChatProject.webChat.dto.WebSocketMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RtcChatService {
	UserSessionManager manager = UserSessionManager.getInstance();

    public void exitRtcRoom(String roomName) {
        Map<String, ChatRoomDto> chatRooms = ChatRoomMap.getInstance().getChatRooms();

        // 해당 목록에서 roomName을 키로 사용하여 채팅방 제거
        chatRooms.remove(roomName);
    }

	public String sendRequest(String sender, String receiver, String type) {
		WebSocketSession session = manager.getUserSession(receiver);
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + session);

		if (session == null) {
			log.info("@@@@@@@@@@@@@@@@@@@@@@" + session + "이 비어있어요@@@@@@@@@@@@@@@@@@");
		}

		log.info(session + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

		String requestMessage = "";
		if ("video".equals(type)) {
			requestMessage = sender + "님이 영상통화 요청을 보냈습니다.";
			log.info(requestMessage);
		} else if ("voice".equals(type)) {
			requestMessage = sender + "님이 음성통화 요청을 보냈습니다.";
			log.info(requestMessage);
		}

		TextMessage message = new TextMessage(requestMessage);
		try {
			session.sendMessage(message);
		} catch (IOException e) {
			log.error("Error sending message to user: {}", sender, e);
		}
		return message.getPayload();
	}
	
	public String declineRTC(String sender, String receiver){
		String declineMessage = "{\"type\": \"decline\", \"message\": \"" + sender + "님이 통화 요청을 거절하였습니다.\"}";
	    WebSocketSession session = manager.getUserSession(receiver);
	    log.info("거절받는유저 " + receiver);
	    log.info("거절받는유저 세션 " + session);
	    log.info("거절 메세지 보내는 유저" + sender);
	    log.info(declineMessage);
	    TextMessage message = new TextMessage(declineMessage);
	    try {
	        session.sendMessage(message);
	    } catch (IOException e) {
	        log.error("Error sending decline message to user: {}", receiver, e);
	    }
	    return message.getPayload();
	}



	// 로그아웃시 웹소켓 해제 및 map에서 삭제
	public void RTCLogout(String userName) throws IOException {
		log.info("로그아웃" + userName);
		if (userName == null || userName.trim().isEmpty()) {
			throw new IllegalArgumentException("userName cannot be null or empty");
		}
		WebSocketSession userSession = manager.getUserSession(userName);
		if (userSession != null && userSession.isOpen()) {
			userSession.close(); // 연결 끊기
			manager.removeUserSession(userName);
			System.out.println("로그인중인 유저: " + manager.getAllKeys());
		}
	}

	// repository substitution since this is a very simple realization
	public ChatRoomDto createChatRoom(String roomName, String roomPwd, boolean secretChk, int maxUserCnt) {
		// roomName 와 roomPwd 로 chatRoom 빌드 후 return
		ChatRoomDto room = ChatRoomDto.builder()
//                .roomId(UUID.randomUUID().toString())
				.roomId(roomName).roomName(roomName).roomPwd(roomPwd) // 채팅방 패스워드
				.secretChk(secretChk) // 채팅방 잠금 여부
				.userCount(0) // 채팅방 참여 인원수
				.maxUserCnt(maxUserCnt) // 최대 인원수 제한
				.build();

		room.setUserList(new HashMap<String, WebSocketSession>());

		// msg 타입이면 ChatType.MSG
		room.setChatType(ChatRoomDto.ChatType.RTC);

		// map 에 채팅룸 아이디와 만들어진 채팅룸을 저장
		ChatRoomMap.getInstance().getChatRooms().put(room.getRoomId(), room);

		return room;
	}

    public ChatRoomDto createVoiceChatRoom(String roomName, String roomPwd, boolean secretChk, int maxUserCnt) {
        // roomName 와 roomPwd 로 chatRoom 빌드 후 return
        ChatRoomDto room = ChatRoomDto.builder()
//                .roomId(UUID.randomUUID().toString())
                .roomId(roomName)
                .roomName(roomName)
                .roomPwd(roomPwd) // 채팅방 패스워드
                .secretChk(secretChk) // 채팅방 잠금 여부
                .userCount(0) // 채팅방 참여 인원수
                .maxUserCnt(maxUserCnt) // 최대 인원수 제한
                .build();

        room.setUserList(new HashMap<String, WebSocketSession>());

        // msg 타입이면 ChatType.MSG
        room.setChatType(ChatRoomDto.ChatType.VOI);

        // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장
        ChatRoomMap.getInstance().getChatRooms().put(room.getRoomId(), room);
//        StringBuffer a1 = new StringBuffer("test");
//        System.out.println(a1);
//        StringBuilder a2 = new StringBuilder("test");
//        System.out.println(a2);
//        String a3 = "test";
//        System.out.println(a3);
        return room;
    }

    public Map<String, WebSocketSession> getClients(ChatRoomDto room) {
        // 공부하기 좋은 기존 코드
        // unmodifiableMap : read-only 객체를 만들고 싶을 때 사용
        // Collections emptyMap() : 결과를 반환할 시 반환할 데이터가 없거나 내부조직에 의해 빈 데이터가 반환되어야 하는 경우
        // NullPointException 을 방지하기 위하여 반환 형태에 따라 List 나 Map 의 인스턴스를 생성하여 반환하여 처리해야하는 경우
        // size 메서드 등을 체크하고 추가적인 값을 변경하지 않는 경우 Collections.emptyMap() 를 사용하면 매번 동일한 정적 인스턴스가
        // 변환되므라 각 호출에 대한 불필요한 인스턴스 생성하지 않게 되어 메모리 사용량을 줄일 수 있다

//        return (Map<String, WebSocketSession>) Optional.ofNullable(room)
//                .map(r -> Collections.unmodifiableMap(r.getUserList()))
//                .orElse(Collections.emptyMap());

		Optional<ChatRoomDto> roomDto = Optional.ofNullable(room);

		return (Map<String, WebSocketSession>) roomDto.get().getUserList();
	}

	// userList 에서 클라이언트 삭제
	public void removeClientByName(ChatRoomDto room, String userUUID) {
		room.getUserList().remove(userUUID);
	}
	

    public Map<String, WebSocketSession> addClient(ChatRoomDto room, String name, WebSocketSession session) {
        Map<String, WebSocketSession> userList = (Map<String, WebSocketSession>) room.getUserList();
        log.info(userList.toString() + " aa " + name + " bb ");
        userList.put(name, session);
        return userList;
    }

    // 유저 카운터 return
    public boolean findUserCount(WebSocketMessage webSocketMessage) {
        Set<String> keys = ChatRoomMap.getInstance().getChatRooms().keySet();
        log.info("현재 RTC채팅방 목록: " + keys.toString());
        log.info(String.valueOf(ChatRoomMap.getInstance().getChatRooms().get(webSocketMessage.getData())));
        log.info(ChatRoomMap.getInstance().toString());
        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(webSocketMessage.getData());
        log.info("클라에서 넘어오는 메세지: " + webSocketMessage.getData());

        if (webSocketMessage.getChatType().equals("video")) {

            if (room == null) {
                room = createChatRoom(webSocketMessage.getData(), "", false, 2);
                log.info("방 생성했움2");
            }
            log.info("ROOM COUNT : [{} ::: {}]", room.toString(), room.getUserList().size());
            return room.getUserList().size() >= 1;

        } else if (webSocketMessage.getChatType().equals("voice")) {
            if (room == null) {
                room = createVoiceChatRoom(webSocketMessage.getData(), "", false, 2); // 음성채팅방 생성
                log.info("음성채팅방 생성했움" + webSocketMessage.getData());
            }
            log.info("음성채팅방 ROOM COUNT : [{} ::: {}]", room.toString(), room.getUserList().size());
            return room.getUserList().size() >= 1;
        }

        return false;
    }

}