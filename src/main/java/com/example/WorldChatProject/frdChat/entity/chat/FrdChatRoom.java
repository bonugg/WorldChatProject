package com.example.WorldChatProject.frdChat.entity.chat;

import com.example.WorldChatProject.frdChat.dto.chat.FrdChatRoomDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrdChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String roomName;

    public FrdChatRoomDTO EntityToDTO() {
        return FrdChatRoomDTO.builder()
                .id(this.id)
                .roomName(this.roomName)
                .build();
    }
}
