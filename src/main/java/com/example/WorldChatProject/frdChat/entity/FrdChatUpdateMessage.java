package com.example.WorldChatProject.frdChat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FrdChatUpdateMessage {

    private List<FrdChatMessage> updatedMessages;
    private String msg;
    private Long roomId;

    public FrdChatUpdateMessage(List<FrdChatMessage> updatedMessages) {
        this.updatedMessages = updatedMessages;
    }

    public FrdChatUpdateMessage(String msg) {
        this.msg = msg;
    }

    public FrdChatUpdateMessage(Long roomId) {
        this.roomId = roomId;
    }

    public FrdChatUpdateMessage(String msg, long roomId) {
        this.msg = msg;
        this.roomId = roomId;
    }

    public FrdChatUpdateMessage(List<FrdChatMessage> updatedList, String online) {
        this.updatedMessages = updatedList;
        this.msg = online;
    }

    public List<FrdChatMessage> getUpdatedMessages() {
        return updatedMessages;
    }
}