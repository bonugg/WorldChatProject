package com.example.WorldChatProject.randomChat.repository;

import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RandomRoomRepository extends JpaRepository<RandomRoom, Long> {
    RandomRoom findByRandomRoomId(long randomRoomId);

    @Query(value = "SELECT * FROM RandomRoom r WHERE r.user1 =: otherUser", nativeQuery = true)
    RandomRoom findByUsername(User otherUser);
}
