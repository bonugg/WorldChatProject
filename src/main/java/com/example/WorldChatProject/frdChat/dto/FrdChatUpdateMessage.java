package com.example.WorldChatProject.frdChat.dto;

import com.example.WorldChatProject.frdChat.entity.FrdChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FrdChatUpdateMessage {

    private String msg;
    private Long roomId;

}

