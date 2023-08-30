package com.example.WorldChatProject.randomChat.service;

import com.example.WorldChatProject.randomChat.dto.RandomFileDTO;
import com.example.WorldChatProject.randomChat.entity.RandomFile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RandomFileService {

    void uploadFile(RandomFile file, String roomId);

    RandomFile parseFileInfo(MultipartFile file, String directoryPath, String roomId) throws IOException;

    ResponseEntity<byte[]> getObject(String fileDir, String fileName) throws IOException;

    void deleteFilesFromBucket(long path);

    void deleteFilesByRoomIdFromDB(long randomRoomId);
}
