package com.example.WorldChatProject.randomChat.repository;

import com.example.WorldChatProject.randomChat.entity.RandomChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomChatRepository extends JpaRepository<RandomChat, Long> {
    RandomChat findByRandomRoomId(long randomRoomId);
}
