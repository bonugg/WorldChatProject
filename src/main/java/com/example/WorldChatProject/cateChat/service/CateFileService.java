package com.example.WorldChatProject.cateChat.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.IOUtils;
import com.example.WorldChatProject.cateChat.dto.CateFileDTO;
import com.example.WorldChatProject.cateChat.repository.CateFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class CateFileService implements FileService {
    // AmazonS3 주입받기
    private final AmazonS3 s3;

    @Value("${ncp.bucket}")
    private String bucket;

    @Value("${ncp.endPoint}")
    private String endPoint;

    private final CateFileRepository cateFileRepository;

    @Override
    public CateFileDTO uploadFile(MultipartFile file, String transaction, String roomId) {
        try{
            String filename = file.getOriginalFilename(); // 파일원본 이름
            String key = "cate" + "/" + roomId + "/" + transaction + "_" + filename; // S3 파일 경로
            log.info(key + "---");
            // 매개변수로 넘어온 multipartFile 을 File 객체로 변환 시켜서 저장하기 위한 메서드
            File convertedFile = convertMultipartFileToFile(file, transaction + filename);

            // 파일의 메타데이터와 ACL을 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            log.info("-1-");
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, convertedFile)
                    .withCannedAcl(CannedAccessControlList.PublicRead) // 공개 읽기 권한 설정
                    .withMetadata(metadata);
            // 아마존 S3 에 파일 업로드를 위해 사용하는 TransferManagerBuilder
            TransferManager transferManager = TransferManagerBuilder
                    .standard()
                    .withS3Client(s3)
                    .build();
            // bucket 에 key 와 convertedFile 을 이용해서 파일 업로드
            log.info("-2-");
            Upload upload = transferManager.upload(putObjectRequest);

            upload.waitForUploadResult();
            log.info("-3-");
            // 변환된 File 객체 삭제
            removeFile(convertedFile);

            // uploadDTO 객체 빌드
            CateFileDTO uploadReq = CateFileDTO.builder()
                    .transaction(transaction)
                    .chatRoom(roomId)
                    .originalFileName(filename)
                    .fileDir(key)
                    .s3DataUrl(endPoint + "/" + bucket + "/" + key)
                    .build();
            // uploadDTO 객체 리턴
            log.info("123123 "+uploadReq.toString());
            return uploadReq;

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ResponseEntity<byte[]> getObject(String fileDir, String fileName) throws IOException {
        // bucket 와 fileDir 을 사용해서 S3 에 있는 객체 - object - 를 가져온다.
        S3Object object = s3.getObject(new GetObjectRequest(bucket, fileDir));

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

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }
}