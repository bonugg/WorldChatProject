package com.example.WorldChatProject.webChat.controller;

import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.webChat.dto.ChatRoomDto;
import com.example.WorldChatProject.webChat.dto.ChatRoomMap;
import com.example.WorldChatProject.webChat.service.ChatService.ChatServiceMain;
//import com.example.WorldChatProject.webChat.service.social.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RtcChatRoomController {

    private final ChatServiceMain chatServiceMain;

    //기존에 있던 코드 -> 사용은 안 하지만 혹시 몰라 남겨둠!
    // 채팅방 생성 후 다시 / 로 return
    @PostMapping("/chat/createroom")
    public String createRoom(@RequestParam("roomName") String name,
                             @RequestParam("roomPwd") String roomPwd,
                             @RequestParam("secretChk") String secretChk,
                             @RequestParam(value = "maxUserCnt", defaultValue = "2") String maxUserCnt,
                             @RequestParam("chatType") String chatType,
                             RedirectAttributes rttr) {

//         log.info("chk {}", secretChk);

        // 매개변수 : 방 이름, 패스워드, 방 잠금 여부, 방 인원수
        ChatRoomDto room;

        room = chatServiceMain.createChatRoom(name, roomPwd, Boolean.parseBoolean(secretChk), Integer.parseInt(maxUserCnt), chatType);


        log.info("CREATE Chat Room [{}]", room);

        rttr.addFlashAttribute("roomName", room);
        return "redirect:/";
    }

//     채팅방 입장 화면
//     파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
//     지금은 임시로 roomId가 고정으로 되어, 서버 실행 한 번에 2명 밖에 입장이 안됨! -> 퇴장 및 방 삭제 기능 추가 예정
    @GetMapping("/chat/room")
    @ResponseBody
    public String roomDetail(Model model, @RequestParam String roomId, @AuthenticationPrincipal PrincipalDetails principalDetails, RedirectAttributes rttr){
        //유저가 채팅방에 입장했을 때 생성되어 있는 방이 있다면 connect 반환, 방이 없다면 방 생성 후 입장
        if(!ChatRoomMap.getInstance().getChatRooms().isEmpty()){
            return "connect";
        }
//            /chat/room?roomId="+roomId
        log.info("roomId {}", roomId+"여기가 서버로 넘어오는 룸id");
        ChatRoomDto room2;
        room2 = chatServiceMain.createChatRoom("test", "", false, 2, "RTC");
        System.out.println("Room just after creation: " + ChatRoomMap.getInstance().getChatRooms().get(roomId));
        rttr.addFlashAttribute("roomName", room2);
        // principalDetails 가 null 이 아니라면 로그인 된 상태!!
        if (principalDetails != null) {
            // 세션에서 로그인 유저 정보를 가져옴
            model.addAttribute("user", principalDetails.getUser());
        }

        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);
        log.info(room.toString()+"룸 정보");

        model.addAttribute("room", room);


        if (ChatRoomDto.ChatType.MSG.equals(room.getChatType())) {
            log.info("방 생성");
            return "error";
        }else{
            model.addAttribute("uuid", UUID.randomUUID().toString());

//            return "rtcroom";
            return "create";
        }
    }

    // 채팅방 비밀번호 확인
    @PostMapping("/chat/confirmPwd/{roomId}")
    @ResponseBody
    public boolean confirmPwd(@PathVariable String roomId, @RequestParam String roomPwd){

        // 넘어온 roomId 와 roomPwd 를 이용해서 비밀번호 찾기
        // 찾아서 입력받은 roomPwd 와 room pwd 와 비교해서 맞으면 true, 아니면  false
        return chatServiceMain.confirmPwd(roomId, roomPwd);
    }

    // 채팅방 삭제 -> 이거 아직 연결 안함!
    @PostMapping("/chat/delRoom")
    public String delChatRoom(@RequestBody String roomId){
        // roomId 기준으로 chatRoomMap 에서 삭제, 해당 채팅룸 안에 있는 사진 삭제
        chatServiceMain.delChatRoom(roomId);
        log.info("삭제test");
        return "";
    }

    // 유저 카운트
    @GetMapping("/chat/chkUserCnt/{roomId}")
    @ResponseBody
    public boolean chUserCnt(@PathVariable String roomId){

        return chatServiceMain.chkRoomUserCnt(roomId);
    }
}
