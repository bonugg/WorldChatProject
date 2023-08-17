package com.example.WorldChatProject.randomChat.repository;

import com.example.WorldChatProject.randomChat.entity.RandomChat;
import com.example.WorldChatProject.randomChat.entity.RandomFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomFileRepository extends JpaRepository<RandomFile, Long> {

}
