package com.example.WorldChatProject.cateChat.repository;

import com.example.WorldChatProject.cateChat.entity.CateUserList;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CateUserListRepostiory extends JpaRepository<CateUserList, Long> {
    CateUserList findByCateIdAndUserName(Long cateId, String userName);
    List<CateUserList> findByCateId(Long cateId);

    @Transactional
    void deleteByUserName(@Param("userName") String userName);
}

