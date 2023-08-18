package com.example.WorldChatProject.frdChat.controller;

import com.example.WorldChatProject.frdChat.dto.TranslationRequest;
import com.example.WorldChatProject.frdChat.entity.FrdChatRoom;
import com.example.WorldChatProject.frdChat.service.FrdChatRoomService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class TranslationController {

    private final FrdChatRoomService frdChatRoomService;
    private final UserService userService;

    private final String PAPAGO_API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";
    private final String CLIENT_ID = "61mug87lp3";
    private final String CLIENT_SECRET = "QgUg4DXyHGSbLhKWM8of8lrdtm9DPK7ZSnkNlpVA";

    @PostMapping("/translate")
    public ResponseEntity<String> translateText(@RequestBody TranslationRequest request, Authentication authentication) {
        System.out.println("------------이거아예안찍히냐?");
        System.out.println(request.getFrdChatRoomId() + "이거는 번역에서 받아올 채팅방 아이디!"); //난 이미 roomId를 알고 있기에 넣어서 찾으면 되지않을까?
        FrdChatRoom frdChatRoom = frdChatRoomService.findById(request.getFrdChatRoomId());

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println(principal.getUser().getUserNationality() + "로그인유저 국적좀 보자!! 성공이요~ axios 쓸때 config로 토큰을 보내줘야 authentication을 쓸수 있어!!!");

        Long loginUserId = principal.getUser().getUserId();
        User user;
        if(loginUserId == frdChatRoom.getFriends1().getUserId()) {
            user = userService.findById(frdChatRoom.getFriends2().getUserId());
        } else {
            user = userService.findById(frdChatRoom.getFriends1().getUserId());
        }
        System.out.println(user + "이건 좀 너무한거 아니냐고 싯팔");
        System.out.println(loginUserId + "설마 너도 안나오냐?ㅋㅋㅋㅋ");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
        headers.add("X-NCP-APIGW-API-KEY", CLIENT_SECRET);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("source", principal.getUser().getUserNationality());
        map.add("target", user.getUserNationality());
        map.add("text", request.getText());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(PAPAGO_API_URL, entity, String.class);

        return response;
    }
}
