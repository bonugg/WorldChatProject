package com.example.WorldChatProject.cateChat.controller;

import com.example.WorldChatProject.cateChat.dto.TranslationRequest;
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
public class TranslationController {

        String apiUrl = "https://openapi.naver.com/v1/papago/n2mt";
        String clientId = "ABIKpYQzMAvg_6hOqfOE";
        String clientSecret = "GKwRKNLj_9";

        @PostMapping("/translate")
                                            //@RequestBody = HTTP 요청의 본문을 TranslationRequest 객체로 변환
        public ResponseEntity<?> translateText(@RequestBody TranslationRequest translationRequest) {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("source", translationRequest.getSource());
            requestBody.add("target", translationRequest.getTarget());
            requestBody.add("text", translationRequest.getText());

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<?> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            return response;
        }

}
