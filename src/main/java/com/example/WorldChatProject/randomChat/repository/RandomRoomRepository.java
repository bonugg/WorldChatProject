package com.example.WorldChatProject.randomChat.repository;

import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RandomRoomRepository extends JpaRepository<RandomRoom, Long> {
    @Query(value = "SELECT * FROM random_room r WHERE r.user1_id=:userId", nativeQuery = true)
    RandomRoom findByUserId(long userId);

    @Query(value = "SELECT * FROM random_room r WHERE r.random_room_id=:roomId", nativeQuery = true)
    RandomRoom findByRoomId(long roomId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM random_room r WHERE r.random_room_id=:roomId", nativeQuery = true)
    void deleteByRoomId(long roomId);

    @Query(value = "SELECT * FROM random_room r WHERE r.user1_id=:userId OR r.user2_id=:userId", nativeQuery = true)
    RandomRoom findByUser1IdOrUser2Id(long userId);
}
