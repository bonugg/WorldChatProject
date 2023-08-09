package com.example.WorldChatProject.cateChat.repository;

import com.example.WorldChatProject.cateChat.entity.CateRoom;
import com.example.WorldChatProject.cateChat.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CateRoomRepository extends JpaRepository<CateRoom, Long> {


        List<CateRoom> findByInterest(Interest interest);

}

