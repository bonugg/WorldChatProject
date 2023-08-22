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

    @PostMapping("/cateChat/upload/")
    public CateFileDTO uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("roomId") String cateId){
        CateFileDTO fileReq = cateFileService.uploadFile(file, UUID.randomUUID().toString(), cateId);

        return fileReq;
    }

    @GetMapping("/download/{s3DataUrl}")
    public ResponseEntity<byte[]> download(@PathVariable String s3DataUrl) {
        try {
            return cateFileService.getObject(s3DataUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
