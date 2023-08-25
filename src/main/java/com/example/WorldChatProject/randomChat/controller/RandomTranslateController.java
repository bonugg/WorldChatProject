package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.randomChat.dto.RandomChatDTO;
import com.example.WorldChatProject.randomChat.service.RandomTranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/randomTranslate")
@Slf4j
@RequiredArgsConstructor
public class RandomTranslateController {
    private final RandomTranslateService translateService;

    @PostMapping("")
    public void translate(@RequestBody RandomChatDTO randomChatDTO) {
        String text = randomChatDTO.getContent();
        translateService.translate(text);




    }

}
