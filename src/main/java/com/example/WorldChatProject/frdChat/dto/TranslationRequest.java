package com.example.WorldChatProject.frdChat.dto;

import com.example.WorldChatProject.frdChat.entity.FrdChatRoom;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.webChat.dto.ChatRoomDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationRequest {
    private String text;
    private long frdChatRoomId;
}
