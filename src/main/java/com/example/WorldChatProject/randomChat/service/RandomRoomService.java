package com.example.WorldChatProject.randomChat.service;

import com.example.WorldChatProject.randomChat.dto.RandomFileDTO;
import com.example.WorldChatProject.randomChat.dto.RandomRoomDTO;
import com.example.WorldChatProject.randomChat.entity.RandomFile;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface RandomRoomService {
    RandomRoom match(String username);

    boolean delete(long roomId);

    RandomRoom create(User user);

    RandomRoom enter(User user);

    RandomRoom find(long roomId);

}
