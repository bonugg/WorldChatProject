package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.randomChat.dto.RandomTranslateDTO;
import com.example.WorldChatProject.randomChat.service.RandomTranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@RestController
@RequestMapping("/randomTranslate")
@Slf4j
@RequiredArgsConstructor
public class RandomTranslateController {
    private final RandomTranslateService translateService;

    @PostMapping("/translate")
    public RandomTranslateDTO translate(@RequestBody RandomTranslateDTO randomTranslateDTO) {
        log.info("====================Translation Request==================");
        String srcCode = randomTranslateDTO.getSourceCode();
        String tarCode = randomTranslateDTO.getTargetCode();
        log.info("sourceCode: {}", srcCode);
        log.info("targetCode: {}", tarCode);

        RandomTranslateDTO response;

        if(srcCode.equals("ko") || tarCode.equals("ko")) {
            log.info("translate direct");
            response = translateService.translateDirect(randomTranslateDTO);
        }else {
            log.info("translate korean");
            randomTranslateDTO = translateService.translateKorean(randomTranslateDTO);
            response = translateService.translateFinal(randomTranslateDTO);
        }

        return response;
    }

    @PostMapping("detect")
    public RandomTranslateDTO detect(@RequestBody RandomTranslateDTO randomTranslateDTO) throws UnsupportedEncodingException {
        String langCode = translateService.detect(randomTranslateDTO);
        randomTranslateDTO.setSourceCode(langCode);
        return randomTranslateDTO;

    }


}
