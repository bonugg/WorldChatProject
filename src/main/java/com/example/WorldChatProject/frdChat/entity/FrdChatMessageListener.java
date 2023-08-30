package com.example.WorldChatProject.frdChat.entity;

import com.example.WorldChatProject.configuration.ApplicationContextProvider;
import com.example.WorldChatProject.webChat.rtc.LoginWebsocketHandler;
import jakarta.persistence.PostPersist;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Transactional
@Component
public class FrdChatMessageListener {

    @PostPersist
    public void alertUnreadMessage(FrdChatMessage frdChatMessage) {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();

        System.out.println("포스트 업데이트 실행되냐 실행되냐 실행되냐? : " + frdChatMessage);
        if(!frdChatMessage.isCheckRead()) {
            LoginWebsocketHandler loginWebsocketHandler = context.getBean(LoginWebsocketHandler.class);
            loginWebsocketHandler.sendMessageToUser(frdChatMessage.getReceiver(), "1");
        }
    }
}
