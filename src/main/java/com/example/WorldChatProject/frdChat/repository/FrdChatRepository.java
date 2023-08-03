package com.example.WorldChatProject.frdChat.repository;

import com.example.WorldChatProject.frdChat.entity.chat.FrdChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrdChatRepository extends JpaRepository<FrdChatRoom, Long> {
}
