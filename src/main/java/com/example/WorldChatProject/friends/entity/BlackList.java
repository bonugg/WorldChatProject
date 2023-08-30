package com.example.WorldChatProject.friends.entity;

import com.example.WorldChatProject.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private User hater;
    @ManyToOne
    private User hated;
}