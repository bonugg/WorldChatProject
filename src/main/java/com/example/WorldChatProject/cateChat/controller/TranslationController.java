package com.example.WorldChatProject.cateChat.controller;

import com.example.WorldChatProject.cateChat.dto.ResponseDTO;
import com.example.WorldChatProject.cateChat.dto.TranslationRequest;
import com.example.WorldChatProject.cateChat.dto.TranslationResponseDTO;
import com.example.WorldChatProject.cateChat.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslationController {

    private final TranslationService translationService;

    @Autowired
    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping("/translate")
    public ResponseEntity<?> translateText(@RequestBody TranslationRequest translationRequest) {
        ResponseDTO responseDTO = new ResponseDTO<>();
        try {
            TranslationResponseDTO dto = translationService.translate(translationRequest).getBody();
            responseDTO.setItem(dto.getMessage().getResult().getTranslatedText());
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
