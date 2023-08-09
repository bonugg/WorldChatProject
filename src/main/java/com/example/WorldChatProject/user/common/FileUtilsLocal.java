package com.example.WorldChatProject.user.common;

import com.example.WorldChatProject.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class FileUtilsLocal {
	//MultipartFile 객체를 받아서 DTO형태로 변경 후 리턴
	public static User parseFileInfo(MultipartFile file,
									 String attachPath) throws IOException {
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
		
		String userProfilePath = attachPath;

		
		//파일 업로드 처리
		File uploadFile = new File(attachPath + userProfileName);
		
		//리턴될 DTO 셋팅
		user.setUserProfileName(userProfileName);
		user.setUserProfileOrigin(profileFileOrigin);
		user.setUserProfilePath(userProfilePath);
		
		try {
			file.transferTo(uploadFile);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return user;
	}
}
