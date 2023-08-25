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
<<<<<<< HEAD
=======
    @Column
    private String fileDir;
>>>>>>> e0b0c9a326f26755be7b534888a4d15229cf2bff

    @ManyToOne
    private CateRoom cateRoom;


<<<<<<< HEAD
}
=======
}
>>>>>>> e0b0c9a326f26755be7b534888a4d15229cf2bff
