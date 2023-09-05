package com.example.WorldChatProject.randomChat.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class RandomTranslateDTO {
    private String sourceCode;
    private String targetCode;
    private String sourceText;
    private String translatedText;

}
