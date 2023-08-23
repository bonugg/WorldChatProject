package com.example.WorldChatProject.randomChat.service.impl;

import com.example.WorldChatProject.randomChat.configuration.RandomTranslateConfiguration;
import com.example.WorldChatProject.randomChat.dto.RandomTranslateDTO;
import com.example.WorldChatProject.randomChat.service.RandomTranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;


@Service
@RequiredArgsConstructor
@Slf4j
public class RandomTranslateServiceImpl implements RandomTranslateService {
    private final RandomTranslateConfiguration randomTranslateConfiguration;

    @Override
    public String detect(RandomTranslateDTO translateDTO) {
        log.info("====================Processing language code detection==================");
        String client_id = randomTranslateConfiguration.getCLIENT_ID();
        String client_secret = randomTranslateConfiguration.getCLIENT_SECRET();
        String detect_api_url = randomTranslateConfiguration.getDETECT_API_URL();
        String query = translateDTO.getSourceText();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-NCP-APIGW-API-KEY-ID", client_id);
        headers.add("X-NCP-APIGW-API-KEY", client_secret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("query", query);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            JSONObject response = restTemplate.postForObject(detect_api_url, entity, JSONObject.class);
            String responseCode = (String) response.get("langCode");
            log.info("source Code: {}", responseCode);

            return responseCode;

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public RandomTranslateDTO translateDirect(RandomTranslateDTO randomTranslateDTO) {
        log.info("====================Processing direct translation==================");

        String client_id = randomTranslateConfiguration.getCLIENT_ID();
        String client_secret = randomTranslateConfiguration.getCLIENT_SECRET();
        String papago_api_url = randomTranslateConfiguration.getPAPAGO_API_URL();
        String sourceCode = randomTranslateDTO.getSourceCode();
        String targetCode = randomTranslateDTO.getTargetCode();
        String text = randomTranslateDTO.getSourceText();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-NCP-APIGW-API-KEY-ID", client_id);
        headers.add("X-NCP-APIGW-API-KEY", client_secret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("source", sourceCode);
        body.add("target", targetCode);
        body.add("text", text);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try{
            JSONObject message = restTemplate.postForObject(papago_api_url, entity, JSONObject.class);
            LinkedHashMap result = (LinkedHashMap) message.get("message");
            LinkedHashMap response = (LinkedHashMap) result.get("result");
            String translatedText = (String) response.get("translatedText");
            randomTranslateDTO.setTranslatedText(translatedText);
            log.info("translation result: {}", randomTranslateDTO);
            return randomTranslateDTO;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public RandomTranslateDTO translateKorean(RandomTranslateDTO randomTranslateDTO) {
        log.info("====================Processing Korean translation==================");

        String client_id = randomTranslateConfiguration.getCLIENT_ID();
        String client_secret = randomTranslateConfiguration.getCLIENT_SECRET();
        String papago_api_url = randomTranslateConfiguration.getPAPAGO_API_URL();
        String sourceCode = randomTranslateDTO.getSourceCode();
        String text = randomTranslateDTO.getSourceText();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-NCP-APIGW-API-KEY-ID", client_id);
        headers.add("X-NCP-APIGW-API-KEY", client_secret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("source", sourceCode);
        body.add("target", "ko");
        body.add("text", text);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try{
            JSONObject message = restTemplate.postForObject(papago_api_url, entity, JSONObject.class);
            LinkedHashMap result = (LinkedHashMap) message.get("message");
            LinkedHashMap response = (LinkedHashMap) result.get("result");
            String translatedText = (String) response.get("translatedText");
            randomTranslateDTO.setTranslatedText(translatedText);
            log.info("translation result: {}", randomTranslateDTO);
            return randomTranslateDTO;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    //텍스트를 번역하고 post 메소드 호출하여 번역결과가 responseBody에 담김
    @Override
    public RandomTranslateDTO translateFinal(RandomTranslateDTO randomTranslateDTO) {
        log.info("====================Processing Final translation==================");

        String client_id = randomTranslateConfiguration.getCLIENT_ID();
        String client_secret = randomTranslateConfiguration.getCLIENT_SECRET();
        String papago_api_url = randomTranslateConfiguration.getPAPAGO_API_URL();
        String text = randomTranslateDTO.getTranslatedText();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-NCP-APIGW-API-KEY-ID", client_id);
        headers.add("X-NCP-APIGW-API-KEY", client_secret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("source", "ko");
        body.add("target", randomTranslateDTO.getTargetCode());
        body.add("text", text);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try{
            JSONObject message = restTemplate.postForObject(papago_api_url, entity, JSONObject.class);
            LinkedHashMap result = (LinkedHashMap) message.get("message");
            LinkedHashMap response = (LinkedHashMap) result.get("result");
            String translatedText = (String) response.get("translatedText");
            randomTranslateDTO.setTranslatedText(translatedText);
            log.info("translation result: {}", randomTranslateDTO);
            return randomTranslateDTO;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}

