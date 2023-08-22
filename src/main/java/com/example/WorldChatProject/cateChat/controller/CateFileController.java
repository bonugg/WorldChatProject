package com.example.WorldChatProject.cateChat.controller;

import com.example.WorldChatProject.cateChat.dto.CateFileDTO;
import com.example.WorldChatProject.cateChat.service.CateFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CateFileController {

    private final CateFileService cateFileService;

    // 프론트에서 ajax 를 통해 /upload 로 MultipartFile 형태로 파일과 roomId 를 전달받는다.
    // 전달받은 file 를 uploadFile 메서드를 통해 업로드한다.
    @PostMapping("/cateChat/upload/")
    public CateFileDTO uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("roomId") String cateId){


        CateFileDTO fileReq = cateFileService.uploadFile(file, UUID.randomUUID().toString(), cateId);
        log.info("최종 upload Data {}", fileReq);

        System.out.println("최종 upload Data {}: " + fileReq);

        // fileReq 객체 리턴
        return fileReq;
    }

    @GetMapping("/download/{s3DataUrl}")
    public ResponseEntity<byte[]> download(@PathVariable String s3DataUrl) {
        System.out.println("dfkajdflajdflasdjfiadsjf3194093142384729384");
        try {
            System.out.println("s3DataUrl: " + s3DataUrl);
            // 변환된 byte, httpHeader와 HttpStatus가 포함된 ResponseEntity 객체를 반환합니다.
            return cateFileService.getObject(s3DataUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
