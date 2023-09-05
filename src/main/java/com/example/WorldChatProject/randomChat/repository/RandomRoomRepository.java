package com.example.WorldChatProject.randomChat.repository;

import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    List<RandomRoom> findAllByUser1IdOrUser2Id(@Param("userId") long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE random_room " +
            "SET user1_id = CASE WHEN user1_id = :userId THEN NULL ELSE user1_id END, " +
            "user2_id = CASE WHEN user2_id = :userId THEN NULL ELSE user2_id END " +
            "WHERE random_room_id = :roomId", nativeQuery = true)
    void removeUser(@Param("userId") long userId, @Param("roomId") long roomId);



    @Query("SELECT r FROM RandomRoom r WHERE r.randomRoomId = :roomId AND (r.user1 IS NOT NULL OR r.user2 IS NOT NULL)")
    Optional<RandomRoom> findRoomWithUser(@Param("roomId") long roomId);


}
