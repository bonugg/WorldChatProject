package com.example.WorldChatProject.frdChat.entity;

import com.example.WorldChatProject.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FrdChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User friends1;

    @ManyToOne
    private User friends2;

    private LocalDateTime createdAt;


}
