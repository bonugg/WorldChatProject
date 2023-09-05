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
    private List<FrdChatMessage> updatedMsgList;

    public FrdChatUpdateMessage(List<FrdChatMessage> updatedMsgList) {
        this.updatedMsgList = updatedMsgList;
    }

    public FrdChatUpdateMessage(Long roomId, List<FrdChatMessage> updatedMsgList) {
        this.roomId = roomId;
        this.updatedMsgList = updatedMsgList;
    }


}

