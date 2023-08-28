package com.example.WorldChatProject.frdChat.repository;

import com.example.WorldChatProject.frdChat.entity.FrdChatRoom;
import com.example.WorldChatProject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FrdChatRoomRepository extends JpaRepository<FrdChatRoom, Long> {

    Optional<FrdChatRoom> findByFriends1AndFriends2(User user, User user2);

    List<FrdChatRoom> findByFriends1(User user);

    @Query(value = "SELECT * FROM frd_chat_room f WHERE (f.friends1_user_id = :userId AND f.friends2_user_id = :user2Id) OR (f.friends1_user_id = :user2Id AND f.friends2_user_id = :userId)", nativeQuery = true)
    Optional<FrdChatRoom> findRoomByFriends1OrFriends2(@Param("userId") long userId, @Param("user2Id") long userId1);

    @Query(value = "SELECT * FROM frd_chat_room f WHERE f.friends1_user_id = :userId OR f.friends2_user_id = :userId", nativeQuery = true)
    List<FrdChatRoom> findChatRoomByUser(@Param("userId") long userId);

    @Query(value = "SELECT (CASE WHEN f.friends1_user_id = :userId THEN f.friends2_user_id ELSE f.friends1_user_id END) FROM frd_chat_room f WHERE f.id = :id AND (f.friends1_user_id = :userId OR f.friends2_user_id = :userId)", nativeQuery = true)
    Long findFriendIdByRoomIdAndUser(@Param("id") long roomId, @Param("userId") long userId);

}
