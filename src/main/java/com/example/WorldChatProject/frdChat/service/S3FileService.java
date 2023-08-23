package com.example.WorldChatProject.frdChat.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.IOUtils;
import com.example.WorldChatProject.frdChat.dto.FileUploadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileService implements FileService {
    // AmazonS3 주입받기
    private final AmazonS3 amazonS3;

    // S3 bucket 이름
    @Value("${ncp.bucket}")
    private String bucket;

    // S3 base URL
    @Value("${ncp.endPoint}")
    private String endPoint;

    @Override
    public FileUploadDTO uploadFile(MultipartFile file, String transaction, String roomId) {
        try{
            String filename = file.getOriginalFilename(); // 파일원본 이름
            String key = "oneonone" + "/" + roomId + "/" + transaction + "_" + filename; // S3 파일 경로

            // 매개변수로 넘어온 multipartFile 을 File 객체로 변환 시켜서 저장하기 위한 메서드
            File convertedFile = convertMultipartFileToFile(file, transaction + filename);

            // 파일의 메타데이터와 ACL을 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, convertedFile)
                    .withCannedAcl(CannedAccessControlList.PublicRead) // 공개 읽기 권한 설정
                    .withMetadata(metadata);

            // 아마존 S3 에 파일 업로드를 위해 사용하는 TransferManagerBuilder
            TransferManager transferManager = TransferManagerBuilder
                    .standard()
                    .withS3Client(amazonS3)
                    .build();

            // bucket 에 key 와 convertedFile 을 이용해서 파일 업로드
            Upload upload = transferManager.upload(putObjectRequest);
            upload.waitForUploadResult();
            // 변환된 File 객체 삭제
            removeFile(convertedFile);

            // uploadDTO 객체 빌드
            FileUploadDTO uploadReq = FileUploadDTO.builder()
                    .transaction(transaction)
                    .chatRoom(roomId)
                    .originalFileName(filename)
                    .fileDir(key)
                    .s3DataUrl(endPoint + "/" + bucket + "/" + key)
                    .build();

            // uploadDTO 객체 리턴
            return uploadReq;

        } catch (Exception e) {
            log.error("fileUploadException {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteFileDir(String path) {
        for (S3ObjectSummary summary : amazonS3.listObjects(bucket, path).getObjectSummaries()) {
            amazonS3.deleteObject(bucket, summary.getKey());
        }
    }

    @Override
    public ResponseEntity<byte[]> getObject(String fileDir, String fileName) throws IOException {
        // bucket 와 fileDir 을 사용해서 S3 에 있는 객체 - object - 를 가져온다.
        S3Object object = amazonS3.getObject(new GetObjectRequest(bucket, fileDir));

        // object 를 S3ObjectInputStream 형태로 변환한다.
        S3ObjectInputStream objectInputStream = object.getObjectContent();

        // 이후 다시 byte 배열 형태로 변환한다.
        // 아마도 파일 다운로드를 위해서는 byte 형태로 변환할 필요가 있어서 그런듯하다
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        // 여기는 httpHeader 에 파일 다운로드 요청을 하기 위한내용
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        httpHeaders.setContentDispositionFormData("attachment", encodedFileName);

        // 지정된 fileName 으로 파일이 다운로드 된다.


        log.info("HttpHeader : [{}]", httpHeaders);

        // 최종적으로 ResponseEntity 객체를 리턴하는데
        // --> ResponseEntity 란?
        // ResponseEntity 는 사용자의 httpRequest 에 대한 응답 테이터를 포함하는 클래스이다.
        // 단순히 body 에 데이터를 포함하는 것이 아니라, header 와 httpStatus 까지 넣어 줄 수 있다.
        // 이를 통해서 header 에 따라서 다른 동작을 가능하게 할 수 있다 => 파일 다운로드!!

        // 나는 object가 변환된 byte 데이터, httpHeader 와 HttpStatus 가 포함된다.
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }
}
