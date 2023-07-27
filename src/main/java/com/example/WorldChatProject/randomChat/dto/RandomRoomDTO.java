package com.example.WorldChatProject.randomChat.dto;

import com.example.WorldChatProject.user.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RandomRoomDTO {

    private long randomId;
    private String randomName;
    private String randomNum;

}
