package com.example.WorldChatProject.frdChat.repository;

import com.example.WorldChatProject.frdChat.dto.FrdChatRoomHistoryDTO;
import com.example.WorldChatProject.frdChat.entity.FrdChatRoomHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FrdChatRoomHistoryRepository extends JpaRepository<FrdChatRoomHistory, Long> {

    FrdChatRoomHistory findByUserNickNameAndSessionId(String userNickName, String sessionId);

    FrdChatRoomHistory findByRoomId(long roomId);

    Optional<FrdChatRoomHistory> findByRoomIdAndUserNickName(long roomId, String userNickName);
}