package com.example.WorldChatProject.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class S3config {
    @Value("${ncp.accessKey}")
    private String accessKey;

    @Value("${ncp.secretKey}")
    private String secretKey;

    @Value("${ncp.regionName}")
    private String region;

    @Value("${ncp.endPoint}")
    private String endPoint;

<<<<<<< HEAD:src/main/java/com/example/WorldChatProject/frdChat/config/S3config.java
    @Value("${cloud.aws.s3.bucket2}")
=======
    @Value("${ncp.bucket}")
>>>>>>> e0b0c9a326f26755be7b534888a4d15229cf2bff:src/main/java/com/example/WorldChatProject/configuration/S3config.java
    private String bucket;

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey,secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region))
                .build();
    }
}
