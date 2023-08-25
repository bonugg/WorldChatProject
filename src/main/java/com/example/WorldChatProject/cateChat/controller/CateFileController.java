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

<<<<<<< HEAD
    @PostMapping("/cateChat/upload/")
    public CateFileDTO uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("roomId") String cateId){
=======
    @PostMapping("/cateChat/upload")
    public CateFileDTO uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("roomId") String cateId){
        log.info(file.getOriginalFilename());
        log.info(file.getContentType());
        log.info(file.getName());
        log.info(cateId);
>>>>>>> e0b0c9a326f26755be7b534888a4d15229cf2bff
        CateFileDTO fileReq = cateFileService.uploadFile(file, UUID.randomUUID().toString(), cateId);

        return fileReq;
    }

<<<<<<< HEAD
    @GetMapping("/download/{s3DataUrl}")
    public ResponseEntity<byte[]> download(@PathVariable String s3DataUrl) {
        try {
            return cateFileService.getObject(s3DataUrl);
=======
    @GetMapping("/catechat/download/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable String fileName, @RequestParam("fileDir")String fileDir){
        try {
            return cateFileService.getObject(fileDir, fileName);
>>>>>>> e0b0c9a326f26755be7b534888a4d15229cf2bff
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> e0b0c9a326f26755be7b534888a4d15229cf2bff
