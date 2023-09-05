package com.example.WorldChatProject.frdChat.repository;

import com.example.WorldChatProject.frdChat.entity.FrdChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FrdChatMessageRepository extends JpaRepository<FrdChatMessage, Long> {
    List<FrdChatMessage> findByRoomId(Long roomId);

    List<FrdChatMessage> findByRoomIdAndSender(long roomId, String userNickName);

    List<FrdChatMessage> findByRoomIdAndSenderAndCheckRead(long roomId, String userNickName, boolean statement);

    long countByRoomIdAndSenderAndCheckRead(Long roomId, String userNickName, boolean statement);

    Long countBySenderAndReceiverAndCheckRead(String senderNickName, String receiver, boolean statement);

    @Transactional
    @Modifying
    @Query(value = "UPDATE frd_chat_message " +
            "SET is_liked = :status, " +
            "WHERE id = :chatId", nativeQuery = true)
    void updateLike(@Param("chatId") long chatId,@Param("status") boolean status);
}
