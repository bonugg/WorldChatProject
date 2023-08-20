package com.example.WorldChatProject.friends.controller;

import com.example.WorldChatProject.frdChat.dto.ResponseDTO;
import com.example.WorldChatProject.friends.dto.BlackListDTO;
import com.example.WorldChatProject.friends.entity.BlackList;
import com.example.WorldChatProject.friends.service.BlackListService;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BlackListController {

    private final UserService userService;
    private final BlackListService blackListService;

    @PostMapping("/add-blacklist")
    public ResponseEntity<?> addBlackList(Authentication authentication, @RequestBody User user) {
        ResponseDTO<Map<String, String>> response = new ResponseDTO<>();
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            User hater = principal.getUser().DTOToEntity();
            User hated = userService.findById(user.getUserId());
            BlackList checkBlackList = blackListService.checkBlackList(hater, hated);
            Map<String, String> returnMap = new HashMap<>();

            if (checkBlackList == null) {
                BlackList blackList = new BlackList();
                blackList.setHater(hater);
                blackList.setHated(hated);
                blackListService.save(blackList);
                returnMap.put("msg", "added to blacklist");
            } else {
                returnMap.put("msg", "already blacklist");
            }
            response.setStatusCode(HttpStatus.OK.value());
            response.setItem(returnMap);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
