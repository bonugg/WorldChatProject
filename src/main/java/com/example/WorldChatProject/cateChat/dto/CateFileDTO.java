package com.example.WorldChatProject.cateChat.dto;

import com.example.WorldChatProject.cateChat.entity.CateFile;
import com.example.WorldChatProject.cateChat.entity.CateRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CateFileDTO {
    private MultipartFile file;
    private String originalFileName;
    private String transaction;
    private String chatRoom;
    private String s3DataUrl;
    private String fileDir;


    public CateFile DTOTOEntity() {
        CateFile cateFile = CateFile.builder()
                .originalFileName(this.originalFileName)
                .s3DataUrl(this.s3DataUrl)
                .fileDir(this.fileDir)
                .build();
        return cateFile;
    }


}