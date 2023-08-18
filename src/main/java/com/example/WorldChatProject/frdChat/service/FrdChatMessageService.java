package com.example.WorldChatProject.frdChat.service;

import com.example.WorldChatProject.frdChat.dto.FrdChatMessageDTO;
import com.example.WorldChatProject.frdChat.entity.FrdChatMessage;
import com.example.WorldChatProject.frdChat.repository.FrdChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FrdChatMessageService {

    private final FrdChatMessageRepository frdChatMessageRepository;

    public void save(FrdChatMessage frdChatMessage) {
        frdChatMessageRepository.save(frdChatMessage);
    }

    public List<FrdChatMessage> getChatMessages(Long roomId) {
        return frdChatMessageRepository.findByRoomId(roomId);
    }
}
