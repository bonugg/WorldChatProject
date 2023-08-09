package com.example.WorldChatProject.cateChat.repository;

import com.example.WorldChatProject.cateChat.entity.CateRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CateRoomRepository extends JpaRepository<CateRoom, Long> {

}

