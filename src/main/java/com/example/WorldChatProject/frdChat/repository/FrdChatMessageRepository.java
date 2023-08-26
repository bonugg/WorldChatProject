package com.example.WorldChatProject.frdChat.repository;

import com.example.WorldChatProject.frdChat.entity.FrdChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrdChatMessageRepository extends JpaRepository<FrdChatMessage, Long> {
    List<FrdChatMessage> findByRoomId(Long roomId);

    List<FrdChatMessage> findByRoomIdAndSender(long roomId, String userNickName);

    List<FrdChatMessage> findByRoomIdAndSenderAndCheckRead(long roomId, String userNickName, boolean statement);

    long countByRoomIdAndSenderAndCheckRead(Long roomId, String userNickName, boolean statement);
}
