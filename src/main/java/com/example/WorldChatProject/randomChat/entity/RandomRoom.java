package com.example.WorldChatProject.randomChat.entity;

import com.example.WorldChatProject.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_randomRoom")
@Builder
public class RandomRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "randomId")
    private long randomId;
    @NotNull
    @Column(name="randomName")
    private String randomName;
    @NotNull
    @Column(name="randomNum")
    private String randomNum;

    @OneToMany(mappedBy = "randomRoom", cascade = CascadeType.ALL)
    private List<RandomChat> randomChatContent = new ArrayList<>();

    //랜덤채팅방 생성
    public static RandomRoom create(String roomName){
        RandomRoom room = new RandomRoom();
        room.randomNum = UUID.randomUUID().toString();
        room.randomName = roomName;
        return room;
    }



}
