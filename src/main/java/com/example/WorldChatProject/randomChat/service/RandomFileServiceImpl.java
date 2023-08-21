package com.example.WorldChatProject.randomChat.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.IOUtils;
import com.example.WorldChatProject.configuration.NaverConfiguration;
import com.example.WorldChatProject.randomChat.entity.RandomFile;
import com.example.WorldChatProject.randomChat.entity.RandomRoom;
import com.example.WorldChatProject.randomChat.repository.RandomFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public RandomFileServiceImpl(RandomFileRepository fileRepository,
                                 RandomRoomService roomService,
                                 NaverConfiguration naverConfiguration) {
        this.fileRepository = fileRepository;
        this.roomService = roomService;

        this.s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                naverConfiguration.getEndPoint(),
                                naverConfiguration.getRegionName()))
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                        naverConfiguration.getAccessKey(),
                                        naverConfiguration.getSecretKey())))
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
            TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(s3).build();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucket, key, file.getInputStream(), objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead);

            Upload upload = transferManager.upload(putObjectRequest);
            upload.waitForUploadResult();
        } catch (Exception e) {
            log.info("object storage error : {}", e.getMessage());
        }
        return randomFile;
    }

    //db에 파일 업로드
    @Override
    public void uploadFile(RandomFile file, String roomId) {
        RandomRoom room = roomService.findRoomById(Long.parseLong(roomId));
        file.setRandomRoom(room);
        fileRepository.save(file);
    }

    @Override
    public ResponseEntity<byte[]> getObject(String fileDir, String fileName) throws IOException {
        // bucket 와 fileDir 을 사용해서 S3 에 있는 객체 - object - 를 가져온다.
        S3Object object = s3.getObject(new GetObjectRequest(bucket, fileDir));

        // object 를 S3ObjectInputStream 형태로 변환한다.
        S3ObjectInputStream objectInputStream = object.getObjectContent();

        // 이후 다시 byte 배열 형태로 변환한다.
        // 아마도 파일 전송을 위해서는 다시 byte[] 즉, binary 로 변환해서 전달해야햐기 때문
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        // 여기는 httpHeader 에 파일 다운로드 요청을 하기 위한내용
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // 지정된 fileName 으로 파일이 다운로드 된다.
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        log.info("HttpHeader : [{}]", httpHeaders);

        // object가 변환된 byte 데이터, httpHeader 와 HttpStatus 가 포함된다.
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }


}
