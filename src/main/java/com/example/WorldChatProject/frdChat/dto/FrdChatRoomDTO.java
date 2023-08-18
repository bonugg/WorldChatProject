package com.example.WorldChatProject.frdChat.dto;

import com.example.WorldChatProject.frdChat.entity.FrdChatRoom;
import com.example.WorldChatProject.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrdChatRoomDTO {
    private Long id;
    private User friends1;
    private User friends2;
    private LocalDateTime createdAt;

    public FrdChatRoom dtoToEntity() {
        return FrdChatRoom.builder()
                .id(this.id)
                .friends1(this.friends1)
                .friends2(this.friends2)
                .createdAt(this.createdAt)
                .build();
    }
}
