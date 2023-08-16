package com.example.WorldChatProject.randomChat.service;

import com.example.WorldChatProject.randomChat.dto.RandomRoomDTO;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.user.entity.User;

public interface RandomRoomService {
    RandomRoom match(String username);

    boolean delete(long roomId);

    RandomRoom create(User user);

    RandomRoom enter(User user);

    RandomRoom find(long roomId);



}
