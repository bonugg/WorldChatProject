package com.example.WorldChatProject.cateChat.dto;

import com.example.WorldChatProject.cateChat.entity.CateRoom;
import com.example.WorldChatProject.cateChat.entity.Interest;
import com.example.WorldChatProject.user.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
public class CateRoomDTO {

    private Long cateId;
    private String cateName;
    private int cateUserCnt;
    private Long maxUserCnt;
    private Interest interest;
    private HashMap<String, String> cateUserList;



    //DTO를 엔티티로 변환하는 메소드
    public CateRoom toCateRoom() {
        CateRoom cateRoom = CateRoom.builder()
                //엔티티 필드명(this.DTO명 -> 둘은 형태가 같아야 한다.
                .cateId(this.cateId)
                .cateName(this.cateName)
                .cateUserCnt(this.cateUserCnt)
                .maxUserCnt(this.maxUserCnt)
                .interest(this.interest)
                .build();

        return cateRoom;

    }
}
