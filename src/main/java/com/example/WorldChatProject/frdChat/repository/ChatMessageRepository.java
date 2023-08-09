package com.example.WorldChatProject.frdChat.repository;

import com.example.WorldChatProject.frdChat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
