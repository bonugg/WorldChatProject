package com.example.WorldChatProject.frdChat.entity;

import com.example.WorldChatProject.frdChat.dto.Status;
import com.example.WorldChatProject.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long roomId;
    private String sender;
    private String receiver;
    private String message;

    private LocalDateTime createdAt;
    @Transient
    private Status status;
}
