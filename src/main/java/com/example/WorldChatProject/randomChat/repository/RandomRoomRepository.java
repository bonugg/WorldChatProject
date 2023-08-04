package com.example.WorldChatProject.randomChat.repository;

import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RandomRoomRepository extends JpaRepository<RandomRoom, Long> {
    @Query(value = "SELECT * FROM random_room r WHERE r.user1_id=:userId", nativeQuery = true)
    RandomRoom findByUserId(long userId);
}
