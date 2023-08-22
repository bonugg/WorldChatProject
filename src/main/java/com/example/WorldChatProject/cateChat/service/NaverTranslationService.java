package com.example.WorldChatProject.cateChat.service;

import com.example.WorldChatProject.cateChat.dto.TranslationRequest;
import com.example.WorldChatProject.cateChat.dto.TranslationResponseDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class NaverTranslationService implements TranslationService{

    String apiUrl = "https://openapi.naver.com/v1/papago/n2mt";

    // 클라이언트 아이디와 시크릿 키
    String clientId = "ABIKpYQzMAvg_6hOqfOE"; // 네이버 클라우드의 클라이언트 아이디
    String clientSecret = "GKwRKNLj_9"; // 네이버 클라우드의 클라이언트 시크릿 키

    @Override
    public ResponseEntity<TranslationResponseDTO> translate(TranslationRequest translationRequest) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("source", translationRequest.getSource());
            requestBody.add("target", translationRequest.getTarget());
            requestBody.add("text", translationRequest.getText());

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<TranslationResponseDTO> response = restTemplate.postForEntity(apiUrl, requestEntity, TranslationResponseDTO.class);

            return response;
        } catch (Exception e) {
            // 오류 처리
            throw new RuntimeException(e);
        }
    }
}

