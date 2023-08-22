package com.example.WorldChatProject.cateChat.controller;

import com.example.WorldChatProject.cateChat.dto.TranslationRequest;
import com.example.WorldChatProject.cateChat.service.CateRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class TranslationController {
    private final CateRoomService cateRoomService;

    private final String PAPAGO_API_URL = "https://openapi.naver.com/v1/papago/n2mt";
    private final String CLIENT_ID = "ABIKpYQzMAvg_6hOqfOE";
    private final String CLIENT_SECRET = "GKwRKNLj_9";

    @PostMapping("/translate")
    public ResponseEntity<String> translateText(@RequestBody TranslationRequest request) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
        headers.add("X-NCP-APIGW-API-KEY", CLIENT_SECRET);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("source", request.getSource());
        map.add("target", request.getTarget());
        map.add("text", request.getText());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(PAPAGO_API_URL, entity, String.class);

        return response;
    }
}
