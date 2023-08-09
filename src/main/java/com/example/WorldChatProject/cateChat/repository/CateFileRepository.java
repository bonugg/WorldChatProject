package com.example.WorldChatProject.cateChat.repository;

import com.example.WorldChatProject.cateChat.entity.CateFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CateFileRepository extends JpaRepository<CateFile, Long>  {
}
