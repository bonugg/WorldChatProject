package com.example.WorldChatProject.randomChat.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RandomTranslateConfiguration {
    @Value("${papago.api.url}")
    private String PAPAGO_API_URL;

    @Value("${papago.detect.api.url}")
    private String DETECT_API_URL;

    @Value("${papago.client.id}")
    private String CLIENT_ID;

    @Value("${papago.client.secret}")
    private String CLIENT_SECRET;

}
