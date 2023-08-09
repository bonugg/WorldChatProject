package com.example.WorldChatProject.friends.dto;

import com.example.WorldChatProject.friends.entity.Friends;
import com.example.WorldChatProject.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendsDTO {
    private long id;
    private User user;
    private User friends;
    private FriendsStatement statement;

    public Friends DTOToEntity() {
        return Friends.builder()
                .id(this.id)
                .user(this.user)
                .friends(this.friends)
                .statement(this.statement)
                .build();
    }
}
