package com.example.WorldChatProject.cateChat.service;

import com.example.WorldChatProject.cateChat.dto.CateChatDTO;
import com.example.WorldChatProject.cateChat.entity.CateChat;
import com.example.WorldChatProject.cateChat.entity.CateRoom;
import com.example.WorldChatProject.cateChat.repository.CateChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CateChatService {

    private final CateChatRepository cateChatRepository;

    private final CateRoomService cateRoomService;

    public List<CateChat> getMessagesByCateRoom(long roomId) {
        return cateChatRepository.findByCateRoomCateId(roomId);
    }

    //메시지를 디비에 저장
    public CateChatDTO saveMessage(CateChatDTO cateChatDTO) {

        if(cateChatDTO.getCateChatContent().equals("typing...") || cateChatDTO.getCateChatContent().equals("removeTyping")){
            return cateChatDTO;
        }else {
            CateChat cateChat = new CateChat();

            cateChat.setCateChatId(cateChatDTO.getCateChatId());
            cateChat.setCateChatContent(cateChatDTO.getCateChatContent());
            cateChat.setCateChatRegdate(cateChatDTO.getCateChatRegdate());
            cateChat.setType(cateChatDTO.getType());
            cateChat.setSender(cateChatDTO.getSender());
            cateChat.setCateRoom(cateRoomService.getChatRoom((cateChatDTO.getCateId())));
            CateChat savedCateChat = cateChatRepository.save(cateChat);
            return savedCateChat.toCateChatDTO();
        }
    }


    public void deleteCateChatByCateId(String cateId) {
        List<CateChat> cateChatList = cateChatRepository.findByCateRoomCateId(Long.valueOf(cateId));
        cateChatRepository.deleteAll(cateChatList);
    }
}
