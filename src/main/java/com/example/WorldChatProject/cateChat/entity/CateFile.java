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
    private String originalFileName;

    @Column
    private String s3DataUrl;
    @Column
    private String fileDir;

    @ManyToOne
    private CateRoom cateRoom;


}