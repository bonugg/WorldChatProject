package com.example.WorldChatProject.randomChat.repository;

//import com.example.WorldChatProject.randomChat.entity.RandomChat;
import com.example.WorldChatProject.randomChat.entity.RandomFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RandomFileRepository extends JpaRepository<RandomFile, Long> {


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM random_file rf WHERE rf.random_room_id=:roomId", nativeQuery = true)
    void deleteByRoomId(long roomId);
}
