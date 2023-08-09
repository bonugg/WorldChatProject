package com.example.WorldChatProject.cateChat.service;

import com.example.WorldChatProject.cateChat.repository.CateFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CateFileService {

    private final CateFileRepository cateFileRepository;
}
