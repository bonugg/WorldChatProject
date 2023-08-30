package com.example.WorldChatProject.randomChat.dto;


import com.example.WorldChatProject.randomChat.entity.RandomFile;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RandomFileDTO {
    private Long randomFileId;
    private String randomFileOrigin; //파일 원본 이름
    private String randomFilePath; // 파일 링크(random/key)
    private String randomFileName; //버킷 파일 경로 (key)
    private RandomRoom randomRoom;

    public RandomFile toEntity() {
        return RandomFile.builder()
                .randomFileId(this.randomFileId)
                .randomFilePath(this.randomFilePath)
                .randomFileName(this.randomFileName)
                .randomFileOrigin(this.randomFileOrigin)
                .randomRoom(this.randomRoom)
                .build();
    }
}
