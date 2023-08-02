package com.example.WorldChatProject.randomChat.service;

import com.example.WorldChatProject.randomChat.entity.RandomChat;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.repository.RandomChatRepository;
import com.example.WorldChatProject.randomChat.repository.RandomRoomRepository;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RandomRoomServiceImpl implements RandomRoomService{

    private final UserRepository userRepository;
    private final RandomRoomRepository randomRoomRepository;
    private final RandomChatRepository randomChatRepository;
    private final Queue<User> waitQueue = new LinkedList<>();

    @Override
    public RandomRoom match(String username) {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new RuntimeException("User not found"));
        RandomRoom room;
        // 대기큐에 사용자 있는지 검사
        if(waitQueue.isEmpty()){
            // 대기큐에 아무도 없으면 랜덤 채팅을 신청한 사용자가 대기큐에 추가
            waitQueue.add(user);
            //채팅방을 생성하고 채팅방에 A사용자가 들어감
            room = RandomRoom.create(user);
            randomRoomRepository.save(room);
        }else {
            //대기큐에 사용자가 있으면 대기큐에 상대방을 제거하고 사용자 정보 가져옴
            User otherUser = waitQueue.poll();
            //상대방이 들어간 채팅방에 자신이 들어감
            room = randomRoomRepository.findByUsername(otherUser);
            room = RandomRoom.rename(room, user, otherUser);
            randomRoomRepository.save(room);
        }
        return room;


    }

    @Override
    public RandomRoom getRandomRoom(long randomRoomId) {
        return randomRoomRepository.findByRandomRoomId(randomRoomId);
    }
}
