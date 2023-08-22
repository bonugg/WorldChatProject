package com.example.WorldChatProject.frdChat.entity;

import com.example.WorldChatProject.frdChat.dto.FrdChatRoomDTO;
import com.example.WorldChatProject.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FrdChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User friends1;
    @ManyToOne
    private User friends2;
    private LocalDateTime createdAt;

    public FrdChatRoomDTO entityToDTO() {
        return FrdChatRoomDTO.builder()
                .id(this.id)
                .friends1(this.friends1)
                .friends2(this.friends2)
                .createdAt(this.createdAt)
                .build();
    }


}
