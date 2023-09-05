package com.example.WorldChatProject.webChat.service.ChatService.NcpApiService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class Papago {
    public String PapagoTranslation(String test, String lang, String sendLang) {
        String clientId = "zj8m201pt1";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "uMNnrFNs2kpy3UvPwVP9b9OQRTH1Hwxq7UcE96wb";//애플리케이션 클라이언트 시크릿값";

        String apiURL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";
        String text;
        try {
            text = URLEncoder.encode(test, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("인코딩 실패", e);
        }

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-NCP-APIGW-API-KEY-ID", clientId);
        requestHeaders.put("X-NCP-APIGW-API-KEY", clientSecret);

        String responseBody = post(apiURL, requestHeaders, text, lang, sendLang);

        System.out.println(responseBody);
        return responseBody;
    }

    private static String post(String apiUrl, Map<String, String> requestHeaders, String text, String lang, String sendLang) {
        HttpURLConnection con = connect(apiUrl);
        log.info("번역 도착 언어 " + lang + "번역 출발 언어: " + sendLang);
        switch (lang) {
            case "Kor" -> lang = "ko";
            case "Eng" -> lang = "en";
            case "Jpn" -> lang = "ja";
            case "Chn" -> lang = "zh-CN";
        }
        switch (sendLang) {
            case "Kor" -> sendLang = "ko";
            case "Eng" -> sendLang = "en";
            case "Jpn" -> sendLang = "ja";
            case "Chn" -> sendLang = "zh-CN";
        }
        log.info("번역 도착 언어2: " + lang + "번역 출발 언어2: " + sendLang);

        String postParams = "source=" + sendLang + "&target=" + lang + "&text=" + text; //원본언어: 한국어 (ko) -> 목적언어: 영어 (en)
//        if(lang == text){
//            return
//        }
        try {
            con.setRequestMethod("POST");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postParams.getBytes());
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                JsonNode translatedText = null;
                try {
                    // JSON 문자열
                    String jsonStr = readBody(con.getInputStream());

                    // Jackson의 ObjectMapper를 사용하여 JSON 문자열을 JsonNode 객체로 변환
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(jsonStr);

                    // "Hello" 문자열 추출
                    translatedText = rootNode.path("message").path("result").path("translatedText");
                    if (!translatedText.isMissingNode()) { // 노드가 존재하는지 확인
                        System.out.println("Translated Text: " + translatedText.asText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                return readBody(con.getInputStream());
                return translatedText.asText();
            } else {  // 에러 응답
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
