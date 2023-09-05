package com.example.WorldChatProject.frdChat.entity;

import com.example.WorldChatProject.configuration.ApplicationContextProvider;
import com.example.WorldChatProject.webChat.rtc.LoginWebsocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.PostPersist;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Transactional
@Component
public class FrdChatMessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostPersist
    public void alertUnreadMessage(FrdChatMessage frdChatMessage) {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();

        System.out.println("포스트 업데이트 실행되냐 실행되냐 실행되냐? : " + frdChatMessage);
        if(!frdChatMessage.isCheckRead()) {
            LoginWebsocketHandler loginWebsocketHandler = context.getBean(LoginWebsocketHandler.class);
            String sender = frdChatMessage.getSender();

            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("sender", sender);

            String jsonMsg = "";
            try {
                jsonMsg = "채팅"+objectMapper.writeValueAsString(msgMap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            loginWebsocketHandler.postUnread(frdChatMessage.getReceiver(), jsonMsg);
        }
    }
}
