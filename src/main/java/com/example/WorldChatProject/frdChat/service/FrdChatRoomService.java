package com.example.WorldChatProject.frdChat.service;

import com.example.WorldChatProject.frdChat.entity.FrdChatRoom;
import com.example.WorldChatProject.frdChat.repository.FrdChatRoomRepository;
import com.example.WorldChatProject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FrdChatRoomService {

    private final FrdChatRoomRepository frdChatRoomRepository;

    public void save(FrdChatRoom frdChatRoom) {
        frdChatRoomRepository.save(frdChatRoom);
    }

    public FrdChatRoom findByFriends1AndFriends2(User user, User user2) {
        Optional<FrdChatRoom> checkChatRoom = frdChatRoomRepository.findByFriends1AndFriends2(user, user2);
        if(checkChatRoom.isEmpty()) {
            return null;
        } else {
            return checkChatRoom.get();
        }
    }

    public FrdChatRoom findById(Long id) {
        return frdChatRoomRepository.findById(id).get();
    }


    public FrdChatRoom findRoomByFriends1OrFriends2(User user, User user2) {
        Optional<FrdChatRoom> checkFrdChatRoom = frdChatRoomRepository.findRoomByFriends1OrFriends2(user.getUserId(), user2.getUserId());
        if(checkFrdChatRoom.isEmpty()) {
            return null;
        } else {
            return checkFrdChatRoom.get();
        }
    }

    public List<FrdChatRoom> findChatRoomByUser(User user) {
        return frdChatRoomRepository.findChatRoomByUser(user.getUserId());
    }

    public Long getOtherUser(long roomId, long userId) {
        return frdChatRoomRepository.findFriendIdByRoomIdAndUser(roomId, userId);
    }

    public void deleteRoom(FrdChatRoom checkRoom) {
        frdChatRoomRepository.delete(checkRoom);
    }

}

