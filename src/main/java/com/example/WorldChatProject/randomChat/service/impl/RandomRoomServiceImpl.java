package com.example.WorldChatProject.randomChat.service.impl;


import com.example.WorldChatProject.friends.entity.BlackList;
import com.example.WorldChatProject.friends.repository.BlackListRepository;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.repository.RandomRoomRepository;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RandomRoomServiceImpl implements RandomRoomService {

    private final UserRepository userRepository;
    private final BlackListRepository blackListRepository;
    private final RandomRoomRepository randomRoomRepository;
    private final UserService userService;
    private final Queue<User> waitQueue = new LinkedList<>();
    private final Queue<User> blackListQueue = new LinkedList<>();

    @Override
    public RandomRoom matchStart(String username) {
        User user = userRepository.findByUserName(username).get();

        User otherUser = null;

        synchronized(this) {
            //대기큐 순회
            while (!waitQueue.isEmpty()) {
                otherUser = waitQueue.poll();
                if(canMatch(user, otherUser)) {
                    break;
                } else {
                    blackListQueue.add(otherUser);
                    otherUser = null;
                }
            }
        }

        if (otherUser != null) {
            return matchAwithB(user, otherUser);
        } else {
            synchronized(this) {
                for(User blockedUser : blackListQueue) {
                    waitQueue.add(blockedUser);
                }
            }
            return createRoom(user);
        }
//
//        //대기큐 순회
//        synchronized (this) {
//            while (!waitQueue.isEmpty()) {
//                System.out.println("waitQueue 비어져있음?: " + waitQueue.isEmpty());
//                User otherUser = waitQueue.poll();
//
//                //블랙리스트 필터링
//                if(canMatch(user, otherUser) == true) {
//                    log.info("blocked from blacklist");
//                    log.info("{} can Match : {} ", user.getUserNickName(), otherUser.getUserNickName());
//                    return matchAwithB(user, otherUser);
//                }else {
//                    log.info("{} and {} cannot Match by Blacklist");
//                    blackListQueue.add(otherUser);
//                }
//
//            }
//            // 대기큐에 사용자 없으면 방 만들기
//            for(User blockedUser : blackListQueue) {
//                waitQueue.add(blockedUser);
//            }
//        }
//        return createRoom(user);

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
    public RandomRoom matchAwithB(User user, User otherUser) {
        //otherUser: 방을 만들어놓고 대기큐에서 기다리고 있는 유저
        String userNickName = user.getUserNickName();
        String otherNickName = otherUser.getUserNickName();
        log.info("{} is matched with {}", userNickName, otherNickName);
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

    //블랙리스트 필터링
    @Override
    public boolean canMatch(User user1, User user2) {
        Optional<BlackList> blackList1 = blackListRepository.findByHaterAndHated(user1, user2);
        Optional<BlackList> blackList2 = blackListRepository.findByHaterAndHated(user2, user1);

        if(blackList1.isEmpty() == true && blackList2.isEmpty() == true) {
            //블랙리스트 조회 시 없음
            return true;
        }else {
            return false;
        }
    }


}

