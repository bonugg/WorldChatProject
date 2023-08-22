package com.example.WorldChatProject.cateChat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PapagoResultDTO {
    @JsonProperty("srcLangType")
    private String srcLangType;

    @JsonProperty("tarLangType")
    private String tarLangType;

    @JsonProperty("translatedText")
    private String translatedText;
}
