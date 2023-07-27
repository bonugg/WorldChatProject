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
@Table(name = "random_room")
@Builder
public class RandomRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "random_room_id")
    private long randomRoomId;
    @NotNull
    @Column(name="random_room_name")
    private String randomRoomName;

    @OneToMany(mappedBy = "randomRoom", cascade = CascadeType.ALL)
    private List<RandomChat> randomChatContent = new ArrayList<>();

    //랜덤채팅방 생성
    public static RandomRoom create(String roomName){
        RandomRoom room = new RandomRoom();
        room.randomRoomId = room.getRandomRoomId();
        room.randomRoomName = roomName;
        return room;
    }



}
