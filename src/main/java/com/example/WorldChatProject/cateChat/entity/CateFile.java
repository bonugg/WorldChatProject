package com.example.WorldChatProject.cateChat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class CateFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long cateFileId;

    @Column
    private String cateFilePath;

    @Column
    private String cateFileName;

    @Column
    private String cateFileOrigin; //파일 원본 이름

    @ManyToOne
    private CateRoom cateRoom;



}
