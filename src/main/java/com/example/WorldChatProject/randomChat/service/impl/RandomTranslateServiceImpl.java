package com.example.WorldChatProject.randomChat.service.impl;

import com.example.WorldChatProject.randomChat.configuration.RandomTranslateConfiguration;
import com.example.WorldChatProject.randomChat.service.RandomTranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.RequestEntity.post;

@Service
@RequiredArgsConstructor
@Slf4j
public class RandomTranslateServiceImpl implements RandomTranslateService {
    private final RandomTranslateConfiguration randomTranslateConfiguration;

    public void translate(String text) {
        String translatedText;
        try{
            translatedText = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("encoding failure: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", randomTranslateConfiguration.getCLIENT_ID());
        requestHeaders.put("X-Naver-Client_Secret", randomTranslateConfiguration.getCLIENT_SECRET());

//        String responseBody = post(randomTranslateConfiguration.getPAPAGO_API_URL(), requestHeaders, translatedText);

    }

//    public String post(String apiUrl, Map<String, String> requestHeaders, String text) {
//        HttpURLConnection con = connect(apiUrl);
//        String postParams = "source=ko"
//    }

}
