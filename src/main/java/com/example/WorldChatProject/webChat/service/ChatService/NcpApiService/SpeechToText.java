package com.example.WorldChatProject.webChat.service.ChatService.NcpApiService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;

@Service
@Slf4j
public class SpeechToText {
//    @Autowired
//    Papago papago;
//    TextToSpeech tts;

//    public SpeechToText(Papago papago, TextToSpeech text) {
//        this.papago = papago;
//        this.tts = text;
//    }

    public String sttTest(MultipartFile voiceFile, String lang) {
        String clientId = "d9dmogkfhs";             // Application Client ID";
        String clientSecret = "zH13EOFbwKLRuyxsHo9RuVQ711BZrJRwQRmjy4kI";     // Application Client Secret";

        String textValue = null;
        try {
//            String imgFile = "C:\\Users\\seog\\Downloads\\testAudio.wav";
//            File voiceFile = new File(imgFile);
//            String language = lang;        // 언어 코드 ( Kor, Jpn, Eng, Chn )
            log.info("언어 : " + lang);
            String apiURL = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=" + lang;
            URL url = new URL(apiURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            File tempFile = File.createTempFile("temp-audio-", ".wav");
            voiceFile.transferTo(tempFile);
            OutputStream outputStream = conn.getOutputStream();
            FileInputStream inputStream = new FileInputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            BufferedReader br = null;
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {  // 오류 발생
                System.out.println("error!!!!!!! responseCode= " + responseCode);
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            String inputLine;

            if (br != null) {
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println("stt 결과: "+response.toString());
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(String.valueOf(response));
                    textValue = jsonNode.get("text").asText();

                    System.out.println(textValue);
//                    tts.tts(papago.PapagoTranslation(textValue));
                    //음성 재생?
//                    try {
//                        WatchService watchService = FileSystems.getDefault().newWatchService();
//                        Paths.get("C:\\dooha\\demo").register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
//
//                        while (true) {
//                            WatchKey key = watchService.take();
//                            for (WatchEvent<?> event : key.pollEvents()) {
//                                Path changed = (Path) event.context();
//                                if (changed.toString().endsWith(".wav")) {
//                                    // .wav 파일 처리 로직
//                                    // 파일 삭제
////                                    Files.delete(changed);
//                                }
//                            }
//                            key.reset();
//                        }
//                    } catch (IOException | InterruptedException e) {
//                        // 에러 처리
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("error !!!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return textValue;
    }
}
//
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class SpeechToText {
//    public String PapagoTranslation(){
//        String clientId = "d9dmogkfhs";//애플리케이션 클라이언트 아이디값";
//        String clientSecret = "zH13EOFbwKLRuyxsHo9RuVQ711BZrJRwQRmjy4kI";//애플리케이션 클라이언트 시크릿값";
//
//        String apiURL = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt";
//        String contentType ="application/octet-stream";
//        String image;
//        try {
//            image = URLEncoder.encode("점심 뭐 먹지?", "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException("인코딩 실패", e);
//        }
//
//        Map<String, String> requestHeaders = new HashMap<>();
//        requestHeaders.put("X-NCP-APIGW-API-KEY-ID", clientId);
//        requestHeaders.put("X-NCP-APIGW-API-KEY", clientSecret);
//        requestHeaders.put("Content-Type", contentType);
//
//        String responseBody = post(apiURL, requestHeaders);
//
//        System.out.println(responseBody);
//        return responseBody;
//    }
//
//    private static String post(String apiUrl, Map<String, String> requestHeaders){
//        HttpURLConnection con = connect(apiUrl);
//        String postParams = "source=ko&target=ja&text=" + text; //원본언어: 한국어 (ko) -> 목적언어: 영어 (en)
//        try {
//            con.setRequestMethod("POST");
//            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
//                con.setRequestProperty(header.getKey(), header.getValue());
//            }
//
//            con.setDoOutput(true);
//            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
////                wr.write(postParams.getBytes());
//                wr.flush();
//            }
//
//            int responseCode = con.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
//                return readBody(con.getInputStream());
//            } else {  // 에러 응답
//                return readBody(con.getErrorStream());
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("API 요청과 응답 실패", e);
//        } finally {
//            con.disconnect();
//        }
//    }
//
//    private static HttpURLConnection connect(String apiUrl){
//        try {
//            URL url = new URL(apiUrl);
//            return (HttpURLConnection)url.openConnection();
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
//        } catch (IOException e) {
//            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
//        }
//    }
//
//    private static String readBody(InputStream body) {
//        InputStreamReader streamReader = new InputStreamReader(body);
//
//        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
//            StringBuilder responseBody = new StringBuilder();
//
//            String line;
//            while ((line = lineReader.readLine()) != null) {
//                responseBody.append(line);
//            }
//
//            return responseBody.toString();
//        } catch (IOException e) {
//            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
//        }
//    }
//}
