
//package com.example.WorldChatProject.frdChat.config;
//
//import lombok.Getter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//
//@Configuration
//@PropertySource("classpath:/application.properties")
//@Getter
//public class FrdChatNaverConfig {
//
//    @Value("${cloud.aws.credentials.accessKey}")
//    private String accessKey;
//    @Value("${cloud.aws.credentials.secretKey}")
//    private String secretKey;
//    @Value("${cloud.aws.region.static}")
//    private String regionName;
//    @Value("${cloud.aws.s3.endpoint}")
//    private String endPoint;
//}

package com.example.WorldChatProject.frdChat.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/application.properties")
@Getter
public class FrdChatNaverConfig {

    @Value("${ncp.accessKey}")
    private String accessKey;
    @Value("${ncp.secretKey}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String regionName;
    @Value("${cloud.aws.s3.endpoint}")
    private String endPoint;
}

