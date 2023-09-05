package com.example.WorldChatProject.cateChat.service;

import com.example.WorldChatProject.cateChat.entity.CateUserList;
import com.example.WorldChatProject.cateChat.repository.CateUserListRepostiory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CateUserListService {
    private final CateUserListRepostiory cateUserListRepostiory;

    @Transactional
    public void save(Long cateId, String userName) {
        if (cateUserListRepostiory.findByCateIdAndUserName(cateId, userName) == null) {
            CateUserList entity = CateUserList
                    .builder()
                    .cateId(cateId)
                    .userName(userName)
                    .build();
            cateUserListRepostiory.save(entity);
        }
    }

    @Transactional
    public Long UserCnt(Long cateId) {
        return cateUserListRepostiory.countByCateId(cateId);
    }
    @Transactional
    public void delete(Long cateId, String userName) {
        CateUserList cateUserList = cateUserListRepostiory.findByCateIdAndUserName(cateId, userName);
        if (cateUserList != null) {
            cateUserListRepostiory.delete(cateUserList);
        }
    }

    @Transactional
    public List<String> findAllUserNamesByCateId(Long cateId) {
        List<String> userNameList = new ArrayList<>();
        for (CateUserList user : cateUserListRepostiory.findByCateId(cateId)){
            userNameList.add(String.valueOf(user.getUserName()));
        }
        return userNameList;
    }


    public void deleteCateUserList(String userName) {
        cateUserListRepostiory.deleteByUserName(userName);

    }
}