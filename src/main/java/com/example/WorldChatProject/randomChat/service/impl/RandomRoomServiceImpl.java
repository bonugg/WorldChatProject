package com.example.WorldChatProject.randomChat.service.impl;


import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.repository.RandomRoomRepository;
import com.example.WorldChatProject.randomChat.service.RandomFileService;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RandomRoomServiceImpl implements RandomRoomService {

    private final UserRepository userRepository;
    private final RandomRoomRepository randomRoomRepository;
    private final UserService userService;
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
            return createRoom(user);
        }else {
            return addUserToRoom(user);
        }
    }

    //채팅방 생성 , 대기
    @Override
    public RandomRoom createRoom(User user) {
        RandomRoom room = null;
        String userNickName = user.getUserNickName();
        log.info("waitQueue is Empty. {} is entering waitQueue", userNickName);
        waitQueue.offer(user);
        room = room.create(user);
        randomRoomRepository.save(room);
        return room;
    }

    //채팅방 입장, 대기자 삭제
    @Override
    public RandomRoom addUserToRoom(User user) {

        String userNickName = user.getUserNickName();
        User otherUser = waitQueue.poll();
        String otherNickName = otherUser.getUserNickName();
        log.info("waitQueue is not Empty. {} is matched with {}", userNickName, otherNickName);
        RandomRoom room = randomRoomRepository.findByUserId(otherUser.getUserId());
        room = room.rename(room, user, otherUser);
        log.info("{} is enter in {}", userNickName, room.getRandomRoomId());
        randomRoomRepository.save(room);
        return room;
    }

    @Override
    public boolean deleteRoom(long roomId) {
        RandomRoom room = findRoomById(roomId);
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
    public RandomRoom findRoomById(long roomId) {
        return randomRoomRepository.findByRoomId(roomId);
    }

    @Override
    public RandomRoom findRoomByUserId(long userId) {
        return randomRoomRepository.findByUser1IdOrUser2Id(userId);
    }

    @Override
    public RandomRoom removeUserFromRoom(long userId, long roomId) {
        //사용자가 대기큐에 존재할 수 있으므로 대기큐에서도 제거.
        User user = userService.findById(userId);
        if(waitQueue.contains(user)){
            //waitQueue에서 user제거
            log.info("{} is removed from waitQueue by disconnection", user.getUserNickName());
            waitQueue.remove(user);
        }
        randomRoomRepository.removeUser(userId, roomId);
        return randomRoomRepository.findByRoomId(roomId);
    }

    @Override
    public Optional<User> findUserFromRoom(RandomRoom room) {
        Optional<RandomRoom> foundRoom = randomRoomRepository.findRoomWithUser(room.getRandomRoomId());
        return foundRoom.map(r -> r.getUser1() != null ? r.getUser1() : r.getUser2());
    }



}
