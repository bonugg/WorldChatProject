package com.example.WorldChatProject.frdChat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadDTO {
    private MultipartFile file;
    private String originalFileName;
    private String transaction;
    private String chatRoom;
    private String s3DataUrl;
    private String fileDir;
}
