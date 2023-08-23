package com.example.WorldChatProject.frdChat.controller;

import com.example.WorldChatProject.frdChat.dto.TranslationRequest;
import com.example.WorldChatProject.frdChat.entity.FrdChatRoom;
import com.example.WorldChatProject.frdChat.service.FrdChatRoomService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.RequestEntity.post;

@RestController
@RequiredArgsConstructor
@RequestMapping("/language")
@Slf4j
public class TranslationController {

    private final FrdChatRoomService frdChatRoomService;
    private final UserService userService;

    private final String PAPAGO_API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";
    private final String CLIENT_ID = "61mug87lp3";
    private final String CLIENT_SECRET = "QgUg4DXyHGSbLhKWM8of8lrdtm9DPK7ZSnkNlpVA";
    private final String DETECT_URL = "https://naveropenapi.apigw.ntruss.com/langs/v1/dect";

    @PostMapping("/translate")
    public ResponseEntity<String> translateText(@RequestBody TranslationRequest request, Authentication authentication) throws JsonProcessingException {
//        System.out.println("------------이거아예안찍히냐?");
//        System.out.println(request.getFrdChatRoomId() + "이거는 번역에서 받아올 채팅방 아이디!"); //난 이미 roomId를 알고 있기에 넣어서 찾으면 되지않을까?
//        FrdChatRoom frdChatRoom = frdChatRoomService.findById(request.getFrdChatRoomId());
//
//        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//        System.out.println(principal.getUser().getUserNationality() + "로그인유저 국적좀 보자!! 성공이요~ axios 쓸때 config로 토큰을 보내줘야 authentication을 쓸수 있어!!!");
//
//        Long loginUserId = principal.getUser().getUserId();
//        User user;
//        if(loginUserId == frdChatRoom.getFriends1().getUserId()) {
//            user = userService.findById(frdChatRoom.getFriends2().getUserId());
//        } else {
//            user = userService.findById(frdChatRoom.getFriends1().getUserId());
//        }
//        System.out.println(user + "이건 좀 너무한거 아니냐고 싯팔");
//        System.out.println(loginUserId + "설마 너도 안나오냐?ㅋㅋㅋㅋ");

        System.out.println("이거 아예 안찍히냐?");
        System.out.println("내가 친 내용" + request.getText());
        System.out.println("감지감자언어" + request.getSourceLanguage());
        System.out.println("타겟언어" + request.getTargetLanguage());

        //api쓰기 위한 틀(?) 생성
        RestTemplate restTemplate = new RestTemplate();

        //헤더에 api에 관한 내 정보 담기
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
        headers.add("X-NCP-APIGW-API-KEY", CLIENT_SECRET);


//        map.add("source", request.getSourceLanguage());
//        map.add("target", "ko");
//        map.add("text", request.getText());

        //번역기능 제공하는 언어가 별로 없어서 상대방 말 -> 한국어, 한국어 -> 원하는 말 이런식으로 번역하기로 함
        //근데 api에서 같은 나라 언어 번역은 에러로 잡음. 그래서 경우의 수를 나눔
        //1. 상대방이 친 언어가 한국어일때
        if(request.getSourceLanguage().equals("ko")) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("source", request.getSourceLanguage());
            map.add("target", request.getTargetLanguage());
            map.add("text", request.getText());

            //이것 또한 틀
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            //아까만든 제일 큰 틀에 정보들 담아서 요청!
            ResponseEntity<String> response = restTemplate.postForEntity(PAPAGO_API_URL, entity, String.class);

            return response;
            //2. 지정한 언어가 한국어 일때
        } else if(request.getTargetLanguage().equals("ko")) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("source", request.getSourceLanguage());
            map.add("target", "ko");
            map.add("text", request.getText());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(PAPAGO_API_URL, entity, String.class);
            return response;
            //3. 나머지~
        } else {
            MultiValueMap<String, String> map1 = new LinkedMultiValueMap<>();
            map1.add("source", request.getSourceLanguage());
            map1.add("target", "ko");
            map1.add("text", request.getText());

            HttpEntity<MultiValueMap<String, String>> firstentity = new HttpEntity<>(map1, headers);
            ResponseEntity<String> firstTranslation = restTemplate.postForEntity(PAPAGO_API_URL, firstentity, String.class);
            System.out.println("=====================");
            System.out.println(firstTranslation);
            System.out.println(firstTranslation.getBody());

            //json형태 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(firstTranslation.getBody());
            String translatedKoreanText = jsonNode.path("message").path("result").path("translatedText").asText();

            System.out.println("파싱파싱파싱싱카");
            System.out.println(translatedKoreanText);

            //String translatedKoreanText = firstTranslation.getBody();
            MultiValueMap<String, String> map2 = new LinkedMultiValueMap<>();
            map2.add("source", "ko");
            map2.add("target", request.getTargetLanguage());
            map2.add("text", translatedKoreanText);

            System.out.println(map2);
            HttpEntity<MultiValueMap<String, String>> secondEntity = new HttpEntity<>(map2, headers);

            System.out.println("두번째엔ㅌ티티티티ㅣ티");
            System.out.println(secondEntity);

            ResponseEntity<String> secondTranslation = restTemplate.postForEntity(PAPAGO_API_URL, secondEntity, String.class);

            return secondTranslation;
        }
    }

    @PostMapping("/detect")
    public ResponseEntity<?> detectLanguage(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        System.out.println("쿼리나와라 쿼리쿼리쿼리 : " + query);
        log.info("쿼리커리쿼리커리 : " + query);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
        System.out.println(CLIENT_ID);
        headers.add("X-NCP-APIGW-API-KEY", CLIENT_SECRET);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("query", query);
        System.out.println("바디바디바디:"+body);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        System.out.println("엔티티엔티티 : " + entity);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(DETECT_URL, entity, String.class);
            System.out.println("이건뜨냐??");
            System.out.println(response);
            System.out.println(response.getBody());
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}