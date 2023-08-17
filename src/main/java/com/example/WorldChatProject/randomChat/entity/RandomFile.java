package com.example.WorldChatProject.randomChat.entity;

import com.example.WorldChatProject.randomChat.dto.RandomFileDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "random_file")
public class RandomFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long randomFileId;
    @Column
    private String randomFilePath;
    @Column
    private String randomFileName;
    @Column
    private String randomFileOrigin;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "random_room_id")
    @JsonIgnore
    private RandomRoom randomRoom;


    public RandomFileDTO toDTO() {
        RandomRoom initializedRoom = this.randomRoom;
        if (this.randomRoom != null) {
            Hibernate.initialize(initializedRoom);
        }

        return RandomFileDTO.builder()
                .randomFileId(this.randomFileId)
                .randomFilePath(this.randomFilePath)
                .randomFileName(this.randomFileName)
                .randomFileOrigin(this.randomFileOrigin)
                .randomRoom(initializedRoom)
                .build();
    }



}
