package com.example.WorldChatProject.randomChat.controller;

import com.example.WorldChatProject.randomChat.dto.RandomFileDTO;
import com.example.WorldChatProject.randomChat.entity.RandomFile;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.service.RandomFileService;
import com.example.WorldChatProject.randomChat.service.RandomRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/randomFile")
@Slf4j
@RequiredArgsConstructor
public class RandomFileController {
    private final RandomFileService fileService;

    // fileService에 파일 여러개를 한번에 저장
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<RandomFileDTO> uploadFile(@RequestParam("file") List<MultipartFile> files, @RequestParam("roomId") String roomId) {
        List<RandomFileDTO> uploadFileList = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    RandomFile randomFile = new RandomFile();
                    randomFile = fileService.parseFileInfo(file, "random/", roomId);
                    fileService.uploadFile(randomFile, roomId);
                    uploadFileList.add(randomFile.toDTO());
                }
            }
            return uploadFileList;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable String fileName, @RequestParam("fileDir") String fileDir) {
        log.info("fileDir : fileName [{} : {}]", fileDir, fileName);
        try {
            // 변환된 byte, httpHeader 와 HttpStatus 가 포함된 ResponseEntity 객체를 return 한다.
            return fileService.getObject(fileDir, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
