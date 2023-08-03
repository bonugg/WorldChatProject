package com.example.WorldChatProject.frdChat.dto.chat;

import com.example.WorldChatProject.frdChat.entity.chat.FrdChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrdChatRoomDTO {
    private long id;
    private String roomName;

    public FrdChatRoom DTOToEntity() {
        return FrdChatRoom.builder()
                          .id(this.id)
                          .roomName(this.roomName)
                          .build();
    }
}
