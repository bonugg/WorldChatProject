package com.example.WorldChatProject.randomChat.entity;

import com.example.WorldChatProject.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "random_chat")
@Builder
public class RandomChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "random_chat_id")
    private long randomChatId;
    @Column(name = "random_chat_content")
    private String randomChatContent;
    @Column(name = "random_chat_time")
    private String randomChatTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "random_room_id")
    private RandomRoom randomRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User sender; //발신자아이디


}
