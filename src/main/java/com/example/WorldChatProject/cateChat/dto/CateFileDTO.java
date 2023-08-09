package com.example.WorldChatProject.cateChat.dto;

import com.example.WorldChatProject.cateChat.entity.CateRoom;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CateFileDTO {

    private Long cateFileId;
    private String cateFilePath;
    private String cateFileName;
    private String cateFileOrigin;
    private CateRoom cateRoom;

}

