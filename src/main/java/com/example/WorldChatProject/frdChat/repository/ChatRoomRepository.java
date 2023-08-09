package com.example.WorldChatProject.frdChat.repository;

import com.example.WorldChatProject.frdChat.entity.ChatRoom;
import com.example.WorldChatProject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByFriends1AndFriends2(User user, User user2);

//    List<ChatRoom> findByFriends1AndStatement(User user, ChatRoomStatement chatRoomStatement);

    List<ChatRoom> findByFriends1(User user);
}
