package com.example.WorldChatProject.cateChat.repository;

import com.example.WorldChatProject.cateChat.entity.CateChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CateChatRepository extends JpaRepository<CateChat, Long>  {
    List<CateChat> findByCateRoomCateId(long roomId);

    @Query("SELECT c FROM CateChat c WHERE c.cateRoom.cateId = :chatId AND c.cateChatContent LIKE %:keyword%")
    List<CateChat> searchByContent(@Param("chatId") Long chatId, @Param("keyword") String keyword);
}
