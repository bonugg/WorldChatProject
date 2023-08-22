package com.example.WorldChatProject.cateChat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TranslationResponseDTO {
    @JsonProperty("message")
    private PapagoMessageDTO message;
}
