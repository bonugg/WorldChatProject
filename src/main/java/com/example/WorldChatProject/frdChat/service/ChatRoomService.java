package com.example.WorldChatProject.frdChat.service;

import com.example.WorldChatProject.frdChat.entity.ChatRoom;
import com.example.WorldChatProject.frdChat.repository.ChatRoomRepository;
import com.example.WorldChatProject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public void save(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

    public ChatRoom findByFriends1AndFriends2(User user, User user2) {
        Optional<ChatRoom> checkChatRoom = chatRoomRepository.findByFriends1AndFriends2(user, user2);
        if(checkChatRoom.isEmpty()) {
            return null;
        } else {
            return checkChatRoom.get();
        }
    }

//    public List<ChatRoom> findByFriends1AndStatement(User user, ChatRoomStatement chatRoomStatement) {
//        return chatRoomRepository.findByFriends1AndStatement(user, chatRoomStatement);
//
//    }

    public ChatRoom findById(Long id) {
        return chatRoomRepository.findById(id).get();
    }

    public List<ChatRoom> findByFriends1(User user) {
        return chatRoomRepository.findByFriends1(user);
    }
}
