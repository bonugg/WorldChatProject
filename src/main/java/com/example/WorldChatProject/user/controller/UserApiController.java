package com.example.WorldChatProject.user.controller;

import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserApiController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("hello")
    public List<String> hello(){
        return Arrays.asList("나의앱", "MYAPP");
    }

    @GetMapping("/user")
    public PrincipalDetails user(Authentication authentication) {
        //principal 안에는 유저의 정보가 담겨있음
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return principal;
    }

    @PostMapping("/user/logout")
    private ResponseEntity logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        userService.UserLogout(session, request, response);
        return ResponseEntity.status(HttpStatus.OK).body("logout");
    }

    @PostMapping("/idCheck")
    public ResponseEntity<String> idCheck(@RequestBody String userName) {
        return userService.UserIdCheck(userName);
    }
    @PostMapping("/join")
    public String join(@RequestBody User user) {
        log.info(String.valueOf(user));
        userService.UserJoin(user);
        return "회원가입완료";
    }

    @PostMapping("/accessToken")
    public ResponseEntity<String> accessToken() {
        return new ResponseEntity<>("엑세스", HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(HttpServletResponse response, @RequestBody String userName) {
        log.info(userName);
        return userService.UserRefreshToken(response, userName);
    }

    @GetMapping("/userlist")
    public ResponseEntity<List<User>> userList(){
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }
}











