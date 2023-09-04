package com.example.WorldChatProject.randomChat.service;

import com.example.WorldChatProject.randomChat.dto.RandomTranslateDTO;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Random;

public interface RandomTranslateService {

    //텍스트 언어 판별 & 결과 출력(ex ko, en)
    String detect(RandomTranslateDTO translateDTO) throws UnsupportedEncodingException;

    RandomTranslateDTO translateDirect(RandomTranslateDTO randomTranslateDTO);

    // Sourcecode언어 -> 한국어로 번역
    RandomTranslateDTO translateKorean(RandomTranslateDTO randomTranslateDTO);

    // 한국어 -> TargetCode로 번역
    RandomTranslateDTO translateFinal(RandomTranslateDTO randomTranslateDTO);


}
