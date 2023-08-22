package com.example.WorldChatProject.cateChat.service;

import com.example.WorldChatProject.cateChat.dto.CateRoomDTO;
import com.example.WorldChatProject.cateChat.entity.CateChat;
import com.example.WorldChatProject.cateChat.entity.CateRoom;
import com.example.WorldChatProject.cateChat.entity.Interest;
import com.example.WorldChatProject.cateChat.repository.CateRoomRepository;
import com.example.WorldChatProject.user.dto.ResponseDTO;
import com.example.WorldChatProject.user.dto.UserDTO;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CateRoomService {

    private final CateRoomRepository cateRoomRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> getAllCateChatRoom(String category) {
        ResponseDTO<CateRoomDTO> responseDTO = new ResponseDTO<>();
        try {
            List<CateRoom> cateRoomList = new ArrayList<>();
            if (category.equals("ALL")) {
                cateRoomList = cateRoomRepository.findAll();
            } else {
                cateRoomList = cateRoomRepository.findByInterest(Interest.valueOf(category));
            }

            List<CateRoomDTO> cateRoomDTO = new ArrayList<>();
            for (CateRoom cateRoom : cateRoomList) {
                cateRoomDTO.add(cateRoom.toCateRoomDTO());
            }

            responseDTO.setItems(cateRoomDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());


            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    public ResponseEntity createCateRoom(CateRoom cateRoom) {
        log.info("방생성시작 {}", cateRoom);
        CateRoom rs_cateRoom = cateRoomRepository.save(cateRoom);
        log.info("방생성완료");
        return ResponseEntity.status(HttpStatus.OK).body(rs_cateRoom);
    }

    public CateRoom getChatRoom(Long cateId) {
        CateRoom cateRoom = cateRoomRepository.findById(cateId).orElse(null);
        return cateRoom;
    }


    @Transactional
    //채팅방 인원 +1
    public void plusUserCnt(Long cateId) {
        System.out.println("유저 들어옴");
        CateRoom cateRoom = cateRoomRepository.findById(cateId).get();
        cateRoom.setCateUserCnt(cateRoom.getCateUserCnt() + 1);

        cateRoomRepository.save(cateRoom);
    }

    //채팅방 인원 -1
    public void minusUserCnt(Long cateId) {
        System.out.println("유저 나감");
        CateRoom cateRoom = cateRoomRepository.findById(cateId).get();
        cateRoom.setCateUserCnt(cateRoom.getCateUserCnt() - 1);
        cateRoomRepository.save(cateRoom);
    }



}
