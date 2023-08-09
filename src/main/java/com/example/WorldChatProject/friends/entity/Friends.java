package com.example.WorldChatProject.friends.entity;

import com.example.WorldChatProject.friends.dto.FriendsDTO;
import com.example.WorldChatProject.friends.dto.FriendsStatement;
import com.example.WorldChatProject.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private User friends;
    private FriendsStatement statement;

    public FriendsDTO EntityToDTO() {
        return FriendsDTO.builder()
                .id(this.id)
                .user(this.user)
                .friends(this.friends)
                .statement(this.statement)
                .build();
    }

}
