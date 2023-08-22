package com.example.WorldChatProject.cateChat.service;

import com.example.WorldChatProject.cateChat.dto.TranslationRequest;
import com.example.WorldChatProject.cateChat.dto.TranslationResponseDTO;
import org.springframework.http.ResponseEntity;

public interface TranslationService {
    ResponseEntity<TranslationResponseDTO> translate(TranslationRequest translationRequest);
}
