package com.example.WorldChatProject.randomChat.entity;

import com.example.WorldChatProject.randomChat.dto.RandomRoomDTO;
import com.example.WorldChatProject.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;


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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user2_id")
    private User user2;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "randomRoom", cascade = CascadeType.ALL)
    private List<RandomFile> randomFiles;


    //랜덤채팅방 이름 구성하고 랜덤채팅방 반환
    public static RandomRoom create(User user){
        RandomRoom room = new RandomRoom();
        room.setRandomRoomName("WAITING");
        room.setUser1(user);
        return room;
    }

    //채팅방에 상대방 참여 시 채팅방 이름 변경
    public static RandomRoom rename(RandomRoom room, User user1, User user2){
        room.setRandomRoomName("MATCHED");
        room.setUser1(user1);
        room.setUser2(user2);
        return room;
    }

    public RandomRoomDTO toDTO(){
        return RandomRoomDTO.builder()
                .randomRoomId(this.randomRoomId)
                .randomRoomName(this.randomRoomName)
                .user1(this.user1)
                .user2(this.user2)
                .build();
    }
}
