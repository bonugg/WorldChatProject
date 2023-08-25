package com.example.WorldChatProject.randomChat.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RandomTranslateConfiguration {
    private final String PAPAGO_API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";

    private final String CLIENT_ID = "v24rnexxhf";

    private final String CLIENT_SECRET = "ENyX6aXwQlSoI4JHdIFbiir5aE3CeUfoIbczzHvf";
}
