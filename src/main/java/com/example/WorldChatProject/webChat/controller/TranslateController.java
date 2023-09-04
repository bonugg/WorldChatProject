package com.example.WorldChatProject.webChat.controller;

import com.example.WorldChatProject.webChat.service.ChatService.NcpApiService.Papago;
import com.example.WorldChatProject.webChat.service.ChatService.NcpApiService.SpeechToText;
import com.example.WorldChatProject.webChat.service.ChatService.NcpApiService.TextToSpeech;
import com.example.WorldChatProject.webChat.service.ChatService.RtcChatService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@RestController
public class TranslateController {
    private final SpeechToText speechToText;
    private final RtcChatService rtcChatService;
    private final Papago papago;
    public TranslateController(SpeechToText speechToText, RtcChatService rtcChatService, Papago papago){
        this.speechToText = speechToText;
        this.rtcChatService=rtcChatService;
        this.papago = papago;
    }
    @PostMapping("/rtc/upload")
    public void upload(@RequestParam("audio") MultipartFile file, @RequestParam("sender") String sender, @RequestParam("receiver") String receiver, @RequestParam("lang") String lang){
        String stt = speechToText.sttTest(file, lang);
        String text = "번역"+lang+stt;
        log.info("sender: " + sender + " / receiver: " + receiver + " / text: " + text + " / lang: " + lang);
        if(!StringUtils.isEmpty(stt)) {
            System.out.println(rtcChatService.sendRequest(sender, receiver, text));
        }
    }
    @PostMapping("/rtc/translate")
    public ResponseEntity<byte[]> upload(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        String lang = payload.get("lang");
        String sendLang = payload.get("sendLang");
        log.info("받는 사람의 언어: " + lang);
        log.info("보내는 사람의 언어: " + sendLang);
        String testText = papago.PapagoTranslation(text, lang, sendLang);
        log.info("번역 결과값: " + testText);
        byte[] mp3Data = TextToSpeech.tts(testText, lang);
        if (mp3Data != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .body(mp3Data);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/rtc/tts")
    public ResponseEntity<byte[]> getAudio() throws IOException {
        Path mp3Path = Paths.get("C:\\wwc\\WorldChatProject\\testFile.mp3"); // 실제 MP3 파일 경로
        byte[] mp3Data = Files.readAllBytes(mp3Path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(mp3Data);
    }
}
