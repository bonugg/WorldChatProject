package com.example.WorldChatProject.cateChat.entity;

import com.example.WorldChatProject.cateChat.dto.CateRoomDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CateRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long cateId; //카테고리채팅 식별자

    @Column
    private String cateName; //방 이름

    @Column
    private Long cateUserCnt; //현재 인원수

    @Column
    private Long maxUserCnt; //최대인원수

    @Transient
    private Long categoryId; //HTML 폼으로부터 받은 선택된 카테고리 ID(식별자)를 임시로 저장하는 속성

    @Enumerated(EnumType.STRING)
    private Interest interest; //카테고리



    //엔티티를 DTO로 변환하는 메소드
    public CateRoomDTO toCateRoomDTO() {
        CateRoomDTO cateRoomDTO = CateRoomDTO.builder()
                                //DTO필드명에(this.엔티티명) -> 둘은 형태가 같아야 한다.
                                             .cateId(this.cateId)
                                             .cateName(this.cateName)
                                             .cateUserCnt(this.cateUserCnt)
                                             .maxUserCnt(this.maxUserCnt)
                                             .interest(this.interest)
                                             .build();

        return cateRoomDTO;
    }

}
