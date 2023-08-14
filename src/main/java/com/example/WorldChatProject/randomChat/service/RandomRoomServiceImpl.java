package com.example.WorldChatProject.randomChat.service;

import com.example.WorldChatProject.randomChat.dto.RandomRoomDTO;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.repository.RandomRoomRepository;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RandomRoomServiceImpl implements RandomRoomService{

    private final UserRepository userRepository;
    private final RandomRoomRepository randomRoomRepository;
    private final Queue<User> waitQueue = new LinkedList<>();

    @Override
    public RandomRoom match(String username) {
        User user = userRepository.findByUserName(username).get();
        if(user == null || user.equals("")){
            log.info("not found user");
            return null;
        }
        // 대기큐에 사용자 있는지 검사
        if(waitQueue.isEmpty() && !waitQueue.contains(user)){
            return create(user);
        }else {
            return enter(user);
        }
    }

    //채팅방 생성 , 대기
    @Override
    public RandomRoom create(User user) {
        RandomRoom room;
        String userNickName = user.getUserNickName();
        log.info("waitQueue is Empty. {} is entering waitQueue", userNickName);
        waitQueue.offer(user);
        room = RandomRoom.create(user);
        log.info("{} is create {} room", userNickName, room.getRandomRoomId());
        randomRoomRepository.save(room);
        return room;
    }

    //채팅방 입장, 대기자 삭제
    @Override
    public RandomRoom enter(User user) {
        RandomRoom room;
        String userNickName = user.getUserNickName();
        User otherUser = waitQueue.poll();
        String otherNickName = otherUser.getUserNickName();
        log.info("waitQueue is not Empty. {} is matched with {}", userNickName, otherNickName);
        room = randomRoomRepository.findByUserId(otherUser.getUserId());
        room = RandomRoom.rename(room, user, otherUser);
        log.info("{} is enter in {}", userNickName, room.getRandomRoomId());
        randomRoomRepository.save(room);
        return room;
    }

    @Override
    public boolean delete(long roomId) {
        RandomRoom room = find(roomId);
        if(room == null) {
            log.info("{} room not found", roomId);
            return false;
        }else {
            randomRoomRepository.deleteByRoomId(roomId);
            log.info("{} room is deleted", roomId);
            return true;
        }
    }

    @Override
    public RandomRoom find(long roomId) {
        return randomRoomRepository.findByRoomId(roomId);
    }


}
