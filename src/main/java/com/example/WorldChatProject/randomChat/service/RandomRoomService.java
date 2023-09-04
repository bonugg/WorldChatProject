package com.example.WorldChatProject.randomChat.service;


import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.user.entity.User;

import java.util.List;
import java.util.Optional;


public interface RandomRoomService {

    RandomRoom matchStart(String username);

    boolean deleteRoom(long roomId);

    RandomRoom createRoom(User user);

    RandomRoom matchAwithB(User user1, User user2);

    RandomRoom findRoomById(long roomId);

    List<RandomRoom> findAllRoomByUserId(long userId);

    RandomRoom removeUserFromRoom(long userId, long roomId);

    Optional<User> findUserFromRoom(RandomRoom room);

    boolean canMatch(User user1, User user2);


}
