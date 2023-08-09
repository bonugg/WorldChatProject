package com.example.WorldChatProject.friends.service;

import com.example.WorldChatProject.friends.dto.FriendsStatement;
import com.example.WorldChatProject.friends.entity.Friends;
import com.example.WorldChatProject.friends.repository.FriendsRepository;
import com.example.WorldChatProject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final FriendsRepository friendsRepository;

    public void save(Friends friends) {
        friendsRepository.save(friends);
    }

//    public List<Friends> findByUserId(long userId) {
//        Optional
//        return friendsRepository.findById(userId);
//    }

    public Friends findById(Long friendsId) {
        Optional<Friends> friends = friendsRepository.findById(friendsId);
        if(friends.isEmpty()) {
            return null;
        } else {
            return friends.get();
        }
    }

    public Friends findByUserAndFriends(User requester, User receiver) {
        Optional<Friends> checkFriends = friendsRepository.findByUserAndFriends(requester, receiver);
        if(checkFriends.isEmpty()) {
            return null;
        } else {
            return checkFriends.get();
        }
    }

    public List<Long> findFriendsIdByUserId(long userId) {
        return friendsRepository.findReceiverIdByUserId(userId);
    }

//    public List<Friends> findByFriends(User user) {
//        return friendsRepository.findByFriends(user);
//    }

    public List<Friends> findByFriendsAndStatement(User user, FriendsStatement friendsStatement) {
        return friendsRepository.findByFriendsAndStatement(user, friendsStatement);
    }

    public List<Friends> findByUserAndStatement(User user, FriendsStatement friendsStatement) {
        return friendsRepository.findByUserAndStatement(user, friendsStatement);
    }

    public void delete(Friends friends1) {
        friendsRepository.delete(friends1);
    }


//    public void addFriends(Friends friends1) {
//        friendsRepository.save(friends1);
//    }
//
//    public List<Friends> getFriends(User requester) {
//        return friendsRepository.findByRequester(requester);    }
//
//
////    public Friends getRequester(User fromWho) {
////        return friendsRepository.
////    }
//
//    public Friends findById(long id) {
//        Optional<Friends> friends = friendsRepository.findById(id);
//        if(friends.isEmpty()) {
//            return null;
//        } else {
//            return friends.get();
//        }
//    }
//
//    public void updateFriends(Friends request) {
//        friendsRepository.save(request);
//    }
//
//    public Friends findRequestByRequesterAndReceiver(User receiver, User requester) {
//        return friendsRepository.findRequestByRequesterAndReceiver(receiver, requester);
//    }
//
//    public List<Friends> getFriendsList(User requester) {
//        return friendsRepository.findByRequesterAndIsFromAndStatement(requester, true, FriendsStatement.APPROVED);
//    }
}
