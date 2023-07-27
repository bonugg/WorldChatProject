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
@Table(name = "t_randomChat")
@Builder
public class RandomChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "randomChatId")
    private long randomChatId;
    @Column(name = "randomChatContent")
    private String randomChatContent;
    @Column(name = "randomChatTime")
    private String randomChatTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "randomId")
    private RandomRoom randomRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id")
    private User sender; //발신자아이디

    //또는?
    //private String sender;

}
