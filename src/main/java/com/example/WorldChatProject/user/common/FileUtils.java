package com.example.WorldChatProject.user.common;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.WorldChatProject.configuration.NaverConfiguration;
import com.example.WorldChatProject.user.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class FileUtils {

	private final AmazonS3 s3;

	public FileUtils(NaverConfiguration naverConfiguration) {
		s3 = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
						naverConfiguration.getEndPoint(), naverConfiguration.getRegionName()
				))
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(
								naverConfiguration.getAccessKey(), naverConfiguration.getSecretKey()
						)
				))
				.build();
	}
	//MultipartFile 객체를 받아서 DTO형태로 변경 후 리턴
	public User parseFileInfo(MultipartFile file, String directoryPath) throws IOException {
		String bucketName = "bitcamp-bukkit-132";

		//리턴할 BoardDTO 객체 생성
		User user = new User();
		
		String profileFileOrigin = file.getOriginalFilename();
		
		//같은 파일명 파일이 올라오면 나중에 업로드 된 파일로 덮어써지기 때문에 
		//파일명을 날짜_랜덤_...
		SimpleDateFormat formmater = 
				new SimpleDateFormat("yyyyMMddHHmmss");
		Date nowDate = new Date();
		String nowDateStr = formmater.format(nowDate);
		
		UUID uuid = UUID.randomUUID();
		
		//실제 db에 저장될 파일명
		String userProfileName = nowDateStr + "_" + uuid.toString()
					+ "_" + profileFileOrigin;

		//리턴될 DTO 셋팅
		user.setUserProfileName(userProfileName);
		user.setUserProfileOrigin(profileFileOrigin);

		//try구문 안에서만 사용할 객체나 변수를 선언할 수 있음
		//주로 사용후에 close 해줘야되는 객체들을 선언
		try(InputStream fileInputStream = file.getInputStream()) {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType(file.getContentType());

			PutObjectRequest putObjectRequest = new PutObjectRequest(
					bucketName,
					directoryPath + userProfileName,
					fileInputStream,
					objectMetadata
			).withCannedAcl(CannedAccessControlList.PublicRead);

			s3.putObject(putObjectRequest);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return user;
	}
}
