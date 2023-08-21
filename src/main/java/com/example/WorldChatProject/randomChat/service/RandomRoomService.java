package com.example.WorldChatProject.randomChat.service;


import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.impl.RandomFileServiceImpl;
import com.example.WorldChatProject.user.entity.User;

import java.util.Optional;


public interface RandomRoomService {

    RandomRoom match(String username);

    boolean deleteRoom(long roomId);

    RandomRoom createRoom(User user);

    RandomRoom addUserToRoom(User user);

    RandomRoom findRoomById(long roomId);

    RandomRoom findRoomByUserId(long userId);

    RandomRoom removeUserFromRoom(long userId, long roomId);


    Optional<User> findUserFromRoom(RandomRoom room);
}
