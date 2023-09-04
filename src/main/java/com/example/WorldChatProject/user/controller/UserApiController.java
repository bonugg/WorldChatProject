package com.example.WorldChatProject.user.controller;

import com.example.WorldChatProject.user.dto.UserOauthDTO;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserApiController {
    @Value("${file.path}")
    String attachPath;
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/user")
    public PrincipalDetails user(Authentication authentication) {
        //principal 안에는 유저의 정보가 담겨있음
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return principal;
    }

    @PostMapping("/oauth")
    public ResponseEntity<String> oauthLogin(@RequestBody UserOauthDTO userOauthDTO) {
        return userService.UserOauth(userOauthDTO);
    }

    @PostMapping("/oauthJoin")
    public ResponseEntity<String> oauthJoin(@RequestBody UserOauthDTO userOauthDTO) {
        return userService.UserOauthJoin(userOauthDTO);
    }

    @PostMapping("/user/logout")
    private ResponseEntity logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        userService.UserLogout(session, request, response);
        return ResponseEntity.status(HttpStatus.OK).body("logout");
    }

    @PutMapping("/user/messageChange")
    public ResponseEntity<String> messageChange(@RequestBody String userMessage, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return userService.UserMessageChange(userMessage, principal.getUsername());
    }

    @PostMapping("/user/pwdCheck")
    public ResponseEntity<String> pwdCheck(@RequestBody String userPwd, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return userService.UserPwdCheck(userPwd, principal.getUsername());
    }

    @PostMapping("/user/pwdSave")
    public ResponseEntity<String> pwdSave(@RequestBody String NewUserPwd, Authentication authentication) {
        log.info(NewUserPwd);
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return userService.UserPwdSave(NewUserPwd, principal.getUsername());
    }

    @PostMapping( "/user/uploadImage")
    public ResponseEntity<String> uploadImage(@RequestParam("imageFile") MultipartFile imageFile, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        log.info(String.valueOf(imageFile));

        return userService.UserUploadImage(imageFile, principal.getUsername());
    }

    @PostMapping("/idCheck")
    public ResponseEntity<String> idCheck(@RequestBody String userName) {
        return userService.UserIdCheck(userName);
    }

    @PostMapping("/nickNameCheck")
    public ResponseEntity<String> nickNameCheck(@RequestBody String userNickName) {
        return userService.UserNickNameCheck(userNickName);
    }

    @PostMapping("/join")
    public String join(@RequestBody User user) {
        log.info(String.valueOf(user));
        userService.UserJoin(user);
        return "회원가입완료";
    }

    @PostMapping("/accessToken")
    public ResponseEntity<String> accessToken() {
        log.info("액세스토큰 요청");
        return new ResponseEntity<>("엑세스", HttpStatus.OK);
    }

    @GetMapping("/userlist")
    public ResponseEntity<List<User>> userList(){
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }
}











