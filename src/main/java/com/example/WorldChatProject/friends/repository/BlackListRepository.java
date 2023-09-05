package com.example.WorldChatProject.friends.repository;

import com.example.WorldChatProject.friends.entity.BlackList;
import com.example.WorldChatProject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {

    Optional<BlackList> findByHaterAndHated(User hater, User hated);
}
