package com.example.WorldChatProject.cateChat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PapagoMessageDTO {
    @JsonProperty("result")
    private PapagoResultDTO result;
}
