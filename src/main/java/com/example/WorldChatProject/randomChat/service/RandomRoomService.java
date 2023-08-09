package com.example.WorldChatProject.randomChat.service;

import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import jakarta.websocket.Session;
import org.springframework.web.socket.WebSocketSession;

public interface RandomRoomService {
    RandomRoom match(String username);

//    RandomRoom getRandomRoom(long randomRoomId);
}
