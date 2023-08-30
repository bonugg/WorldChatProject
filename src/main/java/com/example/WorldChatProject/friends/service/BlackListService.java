package com.example.WorldChatProject.friends.service;

import com.example.WorldChatProject.friends.entity.BlackList;
import com.example.WorldChatProject.friends.repository.BlackListRepository;
import com.example.WorldChatProject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlackListService {

    private final BlackListRepository blackListRepository;

    public void save(BlackList blackList) {
        blackListRepository.save(blackList);
    }

    public BlackList checkBlackList(User hater, User hated) {
        Optional<BlackList> checkBlackList = blackListRepository.findByHaterAndHated(hater, hated);
        if(checkBlackList.isEmpty()) {
            return null;
        } else {
            return checkBlackList.get();
        }
    }


}