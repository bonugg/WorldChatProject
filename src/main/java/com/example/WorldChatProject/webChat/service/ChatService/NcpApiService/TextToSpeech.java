package com.example.WorldChatProject.webChat.service.ChatService.NcpApiService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
@Slf4j
public class TextToSpeech {
    public static byte[] tts(String text, String lang) {
        String clientId = "d9dmogkfhs";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "zH13EOFbwKLRuyxsHo9RuVQ711BZrJRwQRmjy4kI";//애플리케이션 클라이언트 시크릿값";
        try {
//            text = URLEncoder.encode("내가 그린 기린 그림은 잘 그린 기린 그림이고 네가 그린 기린 그림은 못 그린 기린 그림이다.", "UTF-8"); // 13자
            String apiURL = "https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            String name="";
            switch (lang) {
                case "Kor" -> name = "nara";
                case "Eng" -> name = "clara";
                case "Jpn" -> name = "ntomoko";
                case "Chn" -> name = "meimei";
            }
            log.info("tts 음성 이름 확인: " + name);
            String encodedText = URLEncoder.encode(text, "UTF-8");
            // post request
            String postParams = "speaker="+name+"&volume=0&speed=0&pitch=0&format=mp3&text=" + encodedText;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
//            if (responseCode == 200) { // 정상 호출
//                InputStream is = con.getInputStream();
//                int read = 0;
//                byte[] bytes = new byte[1024];
//                // 랜덤한 이름으로 mp3 파일 생성
////                String tempname = Long.valueOf(new Date().getTime()).toString();
//                String tempname = "testFile";
//                File f = new File(tempname + ".mp3");
//                f.createNewFile();
//                OutputStream outputStream = new FileOutputStream(f);
//                while ((read = is.read(bytes)) != -1) {
//                    outputStream.write(bytes, 0, read);
//                }
//                is.close();
//            } else {  // 오류 발생
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (responseCode == 200) { // 정상 호출
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];
                while ((read = is.read(bytes)) != -1) {
                    byteArrayOutputStream.write(bytes, 0, read);
                }
                is.close();
                return byteArrayOutputStream.toByteArray();
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
            }
        } catch (
                Exception e) {
            System.out.println(e);
        }
//        return "";
        return null;
    }
}