package com.example.WorldChatProject.frdChat.entity;

import com.example.WorldChatProject.frdChat.dto.FrdChatRoomHistoryDTO;
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
public class FrdChatRoomHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;

    private String userNickName;

    private String sessionId;

    public FrdChatRoomHistoryDTO EntityToDTO() {
        return FrdChatRoomHistoryDTO.builder()
                .id(this.id)
                .roomId(this.roomId)
                .userNickName(this.userNickName)
                .sessionId(this.sessionId)
                .build();
    }

}