package com.example.WorldChatProject.cateChat.service;

import com.example.WorldChatProject.cateChat.dto.CateRoomDTO;
import com.example.WorldChatProject.cateChat.entity.CateChat;
import com.example.WorldChatProject.cateChat.entity.CateRoom;
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

    public  ResponseEntity<?> getAllCateChatRoom() {
        ResponseDTO<CateRoomDTO> responseDTO = new ResponseDTO<>();
        try {

            List<CateRoom> cateRoomList = cateRoomRepository.findAll();

            List<CateRoomDTO> cateRoomDTO = new ArrayList<>();
            for(CateRoom cateRoom : cateRoomList) {
                cateRoomDTO.add(cateRoom.toCateRoomDTO());
            }

            responseDTO.setItems(cateRoomDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());


            return ResponseEntity.ok().body(responseDTO);

        } catch(Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    public ResponseEntity createCateRoom(CateRoom cateRoom) {
        cateRoom.setCateUserCnt(0L);
        cateRoomRepository.save(cateRoom);
        return ResponseEntity.status(HttpStatus.OK).body("roomCreate");
    }

    public CateRoom getChatRoom(Long cateId) {
        CateRoom cateRoom = cateRoomRepository.findById(cateId).orElse(null);
        return cateRoom;
    }


    @Transactional
    //채팅방 인원 +1
    public void plusUserCnt(Long cateId, String userName) {
        System.out.println("유저 들어옴");
        CateRoom cateRoom = cateRoomRepository.findById(cateId).get();
        cateRoom.setCateUserCnt(cateRoom.getCateUserCnt()+1);

        cateRoomRepository.save(cateRoom);
    }

    //채팅방 인원 -1
    public void minusUserCnt(Long cateId) {
        System.out.println("유저 나감");
        CateRoom cateRoom = cateRoomRepository.findById(cateId).get();
        cateRoom.setCateUserCnt(cateRoom.getCateUserCnt()-1);
        cateRoomRepository.save(cateRoom);
    }

//        //최대인원수에 따른 채팅방 입장 여부
//    public boolean chkRoomUserCnt(Long cateId) {
//        CateRoom cateRoom =
//    }

//    //채팅방 유저 리스트에 유저 추가
//    public String addUserList(Long cateId, String userName) {
//        CateRoom cateRoom = cateRoomMap.get(cateId);
//        String userUUID = UUID.randomUUID().toString();
//
//        //아이디 중복 확인 후 userList에 추가
//        cateRoom.getCateUserList().put(userUUID, userName);
//
//        return userUUID;
//
//    }

//    // 채팅방 유저 이름 중복 확인
//    public String isDuplicateName(Long cateId, String username){
//        CateRoom cateroom = cateRoomMap.get(cateId);
//        String tmp = username;
//
//        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙임
//        // 이때 랜덤한 숫자를 붙였을 때 getUserlist 안에 있는 닉네임이라면 다시 랜덤한 숫자 붙이기!
//        while(cateroom.getCateUserList().containsValue(tmp)){
//            int ranNum = (int) (Math.random()*100)+1;
//
//            tmp = username+ranNum;
//        }
//
//        return tmp;
//    }
//
//    // 채팅방 유저 리스트 삭제
//    public void delUser(Long cateId, String userUUID){
//        CateRoom cateroom = cateRoomMap.get(cateId);
//        cateroom.getCateUserList().remove(userUUID);
//    }
//
//    // 채팅방 userName 조회
//    public String getUserName(Long cateId, String userUUID){
//        CateRoom cateroom = cateRoomMap.get(cateId);
//        return cateroom.getCateUserList().get(userUUID);
//    }
//
//    // 채팅방 전체 userlist 조회
//    public ArrayList<String> getUserList(Long cateId){
//        ArrayList<String> list = new ArrayList<>();
//
//        CateRoom cateroom = cateRoomMap.get(cateId);
//
//        // hashmap 을 for 문을 돌린 후
//        // value 값만 뽑아내서 list 에 저장 후 reutrn
//        cateroom.getCateUserList().forEach((key, value) -> list.add(value));
//        return list;
//    }





}
