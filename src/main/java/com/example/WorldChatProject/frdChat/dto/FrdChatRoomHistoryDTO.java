package com.example.WorldChatProject.frdChat.dto;

import com.example.WorldChatProject.frdChat.entity.FrdChatRoomHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrdChatRoomHistoryDTO {
    private Long id;

    private Long roomId;

    private String userNickName;

    private String sessionId;

    public FrdChatRoomHistory dTOToEntity() {
        return FrdChatRoomHistory.builder()
                .id(this.id)
                .roomId(this.roomId)
                .userNickName(this.userNickName)
                .sessionId(this.sessionId)
                .build();
    }
}