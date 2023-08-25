package com.example.WorldChatProject.friends.controller;

import com.example.WorldChatProject.frdChat.entity.FrdChatRoom;
import com.example.WorldChatProject.frdChat.service.FrdChatRoomService;
import com.example.WorldChatProject.friends.dto.FriendsDTO;
import com.example.WorldChatProject.frdChat.dto.ResponseDTO;
import com.example.WorldChatProject.friends.entity.BlackList;
import com.example.WorldChatProject.friends.entity.Friends;
import com.example.WorldChatProject.friends.service.BlackListService;
import com.example.WorldChatProject.friends.service.FriendsService;
import com.example.WorldChatProject.user.dto.UserDTO;
import com.example.WorldChatProject.user.entity.User;
import com.example.WorldChatProject.user.security.auth.PrincipalDetails;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import static com.example.WorldChatProject.friends.dto.FriendsStatement.APPROVED;
import static com.example.WorldChatProject.friends.dto.FriendsStatement.WAITING;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendsController {

    private final FriendsService friendsService;
    private final UserService userService;
    private final BlackListService blackListService;
    private final FrdChatRoomService frdChatRoomService;

    @PostMapping("/request")
    public ResponseEntity<?> requestFriends(@RequestBody UserDTO userDTO,
                                            Authentication authentication) {
        ResponseDTO<Map<String, String>> response = new ResponseDTO<>();
        System.out.println("testtest");
        System.out.println(userDTO);
        System.out.println(authentication);
        //드는 의문점.
        //1.리퀘스트DTO 따로 만든 이유?
        //2.requester는 로그인한 유저로 하면 되는거아님?
        try {
            //보낸 사람은 로그인한 유저
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            System.out.println(principal + "이건 프린시펄");
            UserDTO requesterDTO = principal.getUser();
            User requester = requesterDTO.DTOToEntity();
            //받은 사람은 클릭된 유저. 정보는 버튼에 담겨있다?
            User user = userDTO.DTOToEntity();
            User receiver = userService.findById(user.getUserId());
            Friends checkFriends = friendsService.findByUserAndFriends(requester, receiver);
            Friends checkFriends2 = friendsService.findByUserAndFriends(receiver, requester);
            Map<String, String> returnMap = new HashMap<>();
            if(checkFriends == null && checkFriends2 == null) {
                //일단 정방향 저장. approved 되면 역방향 저장해줄꺼야!
                Friends friends1 = new Friends();
                friends1.setUser(requester);
                friends1.setFriends(receiver);
                friends1.setStatement(WAITING);
                friendsService.save(friends1);

                returnMap.put("msg", "request ok");
            } else {
                returnMap.put("msg", "already frds");
            }
            response.setItem(returnMap);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/received-list")
    public ResponseEntity<?> receivedList(Authentication authentication) {
        ResponseDTO<FriendsDTO> response = new ResponseDTO<>();
        try{
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            User user = principal.getUser().DTOToEntity();
            //이건 로그인 했을때 내가 받은 친구요청 리스트!!!!!! 응답이 대기상태인것만!!!!
            List<Friends> friendsList = friendsService.findByFriendsAndStatement(user, WAITING);
            List<FriendsDTO> friendsDTOList = new ArrayList<>();
            for(Friends f : friendsList) {
                friendsDTOList.add(f.EntityToDTO());
            }
            response.setItems(friendsDTOList);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/requested-list")
    public ResponseEntity<?> requestedList(Authentication authentication) {
        ResponseDTO<FriendsDTO> response = new ResponseDTO<>();
        try{
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            User user = principal.getUser().DTOToEntity();
            //이건 로그인 했을때 내가 한 친구요청 리스트!!!!!! 응답이 대기상태인것만!!!!
            List<Friends> friendsList = friendsService.findByUserAndStatement(user, WAITING);
            List<FriendsDTO> friendsDTOList = new ArrayList<>();
            for(Friends f : friendsList) {
                friendsDTOList.add(f.EntityToDTO());
            }
            response.setItems(friendsDTOList);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveRequest(@RequestBody Friends friends, Authentication authentication) {
        ResponseDTO<Map<String, String>> response = new ResponseDTO<>();

        try {
            Map<String, String> returnMap = new HashMap<>();

            System.out.println(friends.getId());
            System.out.println(friends);
//            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//            User user = principal.getUser().DTOToEntity();

            Friends friends1 = friendsService.findById(friends.getId());
            System.out.println(friends1 + "이게 전체 정보!");
            friends1.setStatement(APPROVED);
            friendsService.save(friends1);

            Friends checkFriends = friendsService.findByUserAndFriends(friends1.getFriends(), friends1.getUser());

            if(checkFriends == null) {
                Friends friends2 = new Friends();
                friends2.setUser(friends1.getFriends());
                friends2.setFriends(friends1.getUser());
                friends2.setStatement(APPROVED);
                friendsService.save(friends2);
                returnMap.put("msg", "request approved");
            } else {
                returnMap.put("msg", "already friends");
            }
            response.setItem(returnMap);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/decline")
    public ResponseEntity<?> declineRequest(@RequestBody Friends friends) {
        ResponseDTO<Map<String, String>> response = new ResponseDTO<>();
        //친추를 한번만 하려면 save를 쓰는게 맞고,
        //여러번 하려면 delete로 만들어서 계속 db가 유동적으로 움직일수 있게? 하는게 맞는거같은데 흠..
        //근데 delete로 하면 decline으로 굳이 할 이유가 없겠지?
        //근데 왜 상태를 나눈걸까? 진행된 요청에 따른 상태를 보려고 나눔..
        //음.. 그럼 내 경우에 필요할까?
        System.out.println(friends);
        try {
            Friends friends1 = friendsService.findById(friends.getId());
            System.out.println(friends1);
            friendsService.delete(friends1);
            Map<String, String> returnMap = new HashMap<>();
            returnMap.put("msg", "request denied");
            response.setItem(returnMap);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/friends-list")
    public ResponseEntity<?> getFriendsList(Authentication authentication) {
        ResponseDTO<FriendsDTO> response = new ResponseDTO<>();
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            User user = principal.getUser().DTOToEntity();
            List<Friends> friendsList = friendsService.findByUserAndStatement(user, APPROVED);

            List<FriendsDTO> friendsDTOList = new ArrayList<>();
            for(Friends f : friendsList) {
                friendsDTOList.add(f.EntityToDTO());
            }
            response.setItems(friendsDTOList);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/delete-friends")
    public ResponseEntity<?> deleteFriends(@RequestBody User user, Authentication authentication) {

        ResponseDTO<Map<String, String>> response = new ResponseDTO<>();
        Map<String, String> returnMap = new HashMap<>();
        System.out.println("유저아이디 넘어오는지 확인 : " + user.getUserId());
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            User frds1 = principal.getUser().DTOToEntity();
            User frds2 = userService.findById(user.getUserId());
            Friends friends = friendsService.findByUserAndFriends(frds1, frds2);
            Friends friends2 = friendsService.findByUserAndFriends(frds2, frds1);
            friendsService.delete(friends);
            friendsService.delete(friends2);
            BlackList checkBlackList = blackListService.checkBlackList(frds1, frds2);
            if(checkBlackList == null) {
                BlackList blackList = new BlackList();
                blackList.setHater(frds1);
                blackList.setHated(frds2);
                blackListService.save(blackList);
            } else {
                returnMap.put("msg", "already bl");
            }
            FrdChatRoom checkRoom = frdChatRoomService.findRoomByFriends1OrFriends2(frds1, frds2);
            if(checkRoom != null) {
                frdChatRoomService.deleteRoom(checkRoom);
            }
            returnMap.put("msg", "delete ok");
            response.setItem(returnMap);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }
    }

//    @PostMapping("request")
//    public ResponseEntity<?> asd(@RequestBody User user,
//                                            Authentication authentication) {
//        //로그인한 유저 아이디 세팅
//        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//        Long loginUserId = principal.getUser().getUserId();
//        User receiver = userService.findById(user.getUserId());
//
//        return null;
//    }
    //내가 보낸? 친구요청
//    @PostMapping("/request")
//    public ResponseEntity<?> requestFriends(@RequestBody User receiver,
//                                        @AuthenticationPrincipal User requester) {
//        ResponseDTO<Map<String, String>> response = new ResponseDTO<>();
//        try{
//            Friends friends1 = new Friends();
//            //요청보낸 사람은 로그인한 유저겠지? 저 어노테이션으로 끌어올수 있다면 좋겠네...
//            friends1.setRequester(requester);
//            //요청 받는 사람은 해당버튼에 담겨있는 id로 찾자.(굳이 찾아야할까 의문이다. 어짜피 1:1 랜챗에서 친추할텐데.
//            //안찾아도 되면 리시버의 세팅을 바로 user로 ㄱㄱ)
//            receiver = userService.findById(receiver.getUserId());
//            friends1.setReceiver(receiver);
//            //리퀘스터가 보냈으니 true
//            friends1.setFrom(true);
//            //둘다 true가 되면 waiting을 approved로 바꿔서 상태도 표시하고싶다..
//            friends1.setStatement(FriendsStatement.WAITING);
//            //일단은 집어넣어
//            friendsService.addFriends(friends1);
//            //역방향으로 넣는다. 상대방 측에서도 친구가 되어야하니까.
//            Friends friends2 = new Friends();
//            friends2.setRequester(receiver);
//            friends2.setReceiver(requester);
//            //받는사람이 보낸건 아니니까 false
//            friends2.setFrom(false);
//            friends2.setStatement(FriendsStatement.WAITING);
//            friendsService.addFriends(friends2);
//            Map<String, String> returnMap = new HashMap<>();
//            returnMap.put("msg", "ok");
//            response.setItem(returnMap);
//            response.setStatusCode(HttpStatus.OK.value());
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            response.setErrorMessage(e.getMessage());
//            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @PostMapping("/accept")
//    public ResponseEntity<?> acceptRequest(@RequestBody Friends friends,
//                                           @AuthenticationPrincipal User receiver) {
//        ResponseDTO<Map<String, String>> response = new ResponseDTO<>();
//        try {
//            // 친구 요청을 찾습니다.
//            Friends request = friendsService.findById(friends.getId());
//
//            // 요청을 받는 사용자가 이 요청의 수신자인지 확인합니다.
//            if(!request.getReceiver().equals(receiver)) {
//                throw new Exception("Invalid request");
//            }
//
//            // 친구 요청의 상태를 '승인됨'으로 변경합니다.
//            request.setStatement(FriendsStatement.APPROVED);
//            request.setFrom(true);
//            friendsService.updateFriends(request);
//
//            // 역방향 요청을 찾고 상태를 '승인됨'으로 변경합니다.
//            Friends reverseRequest = friendsService.findRequestByRequesterAndReceiver(receiver, request.getRequester());
//            reverseRequest.setStatement(FriendsStatement.APPROVED);
//            reverseRequest.setFrom(true);
//            friendsService.updateFriends(reverseRequest);
//
//            Map<String, String> returnMap = new HashMap<>();
//            returnMap.put("msg", "ok");
//            response.setItem(returnMap);
//            response.setStatusCode(HttpStatus.OK.value());
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            response.setErrorMessage(e.getMessage());
//            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

//    @PostMapping("/decline")
//    public ResponseEntity<?> declineRequest(@RequestBody Friends friends,
//                                            @AuthenticationPrincipal User receiver) {
//        ResponseDTO<Map<String, String>> response = new ResponseDTO<>();
//        try {
//            // 친구 요청을 찾습니다.
//            Friends request = friendsService.findById(friends.getId());
//
//            // 요청을 받는 사용자가 이 요청의 수신자인지 확인합니다.
//            if(!request.getReceiver().equals(receiver)) {
//                throw new Exception("Invalid request");
//            }
//
//            // 친구 요청의 상태를 '거절됨'으로 변경합니다.
//            request.setStatement(FriendsStatement.DECLINED);
//            friendsService.updateFriends(request);
//
//            Map<String, String> returnMap = new HashMap<>();
//            returnMap.put("msg", "ok");
//            response.setItem(returnMap);
//            response.setStatusCode(HttpStatus.OK.value());
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            response.setErrorMessage(e.getMessage());
//            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

//    @GetMapping("/friends")
//    public ResponseEntity<?> getFriends(@AuthenticationPrincipal User requester) {
//        ResponseDTO<FriendsDTO> response = new ResponseDTO<>();
//        try {
//            List<Friends> friendsList = friendsService.getFriends(requester);
//            List<FriendsDTO> friendsDTOList = new ArrayList<>();
//            for (Friends friend : friendsList) {
//                friendsDTOList.add(friend.EntityToDTO());
//            }
//            response.setItems(friendsDTOList);
//            response.setStatusCode(HttpStatus.OK.value());
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            response.setErrorMessage(e.getMessage());
//            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

}
