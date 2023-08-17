package com.example.WorldChatProject.randomChat.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.example.WorldChatProject.configuration.NaverConfiguration;
import com.example.WorldChatProject.randomChat.dto.RandomFileDTO;
import com.example.WorldChatProject.randomChat.entity.RandomFile;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.repository.RandomFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RandomFileServiceImpl implements RandomFileService {
    private final RandomFileRepository fileRepository;
    private final RandomRoomService roomService;
    private AmazonS3 s3;
    private String bucket;

    @Autowired
    public RandomFileServiceImpl(
            RandomFileRepository fileRepository,
            RandomRoomService roomService,
            NaverConfiguration naverConfiguration) {
        this.fileRepository = fileRepository;
        this.roomService = roomService;

        this.s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        naverConfiguration.getEndPoint(), naverConfiguration.getRegionName()
                ))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(
                                naverConfiguration.getAccessKey(), naverConfiguration.getSecretKey()
                        )
                ))
                .build();

        this.bucket = naverConfiguration.getBucket();
    }


    //파일 이름, 경로 설정 & object storage 파일 업로드
    public RandomFile parseFileInfo(MultipartFile file, String directoryPath, String roomId) throws IOException {
        RandomFile randomFile = new RandomFile();

        String fileOriginName = file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String key = roomId + "/" + uuid.toString() + "_" + fileOriginName;
        //파일명: randomRoomId/uuid번호/원본파일이름

        //리턴될 DTO 셋팅
        randomFile.setRandomFileOrigin(fileOriginName);
        randomFile.setRandomFileName(key);
        randomFile.setRandomFilePath(directoryPath);

        try {
            TransferManager transferManager = TransferManagerBuilder.standard()
                    .withS3Client(s3)
                    .build();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    this.bucket,
                    key,
                    file.getInputStream(),
                    objectMetadata
            ).withCannedAcl(CannedAccessControlList.PublicRead);

            Upload upload = transferManager.upload(putObjectRequest);
            //upload.waitForCompletion();
            upload.waitForUploadResult();
        } catch (Exception e) {
            log.info("object storage error : {}", e.getMessage());
        }
        return randomFile;
    }


    //db에 파일 업로드
    @Override
    public void uploadFile(RandomFile file, String roomId) {
            RandomRoom room = roomService.find(Long.parseLong(roomId));
            file.setRandomRoom(room);
            fileRepository.save(file);
    }


}
