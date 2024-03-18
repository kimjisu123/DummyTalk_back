package com.example.DummyTalk.User.Controller;

import com.example.DummyTalk.Common.DTO.ResponseDTO;
import com.example.DummyTalk.User.DTO.FriendDTO;
import com.example.DummyTalk.User.DTO.TokenDTO;
import com.example.DummyTalk.User.DTO.UserDTO;
import com.example.DummyTalk.User.Entity.User;
import com.example.DummyTalk.User.Service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    @Value("${serverAbsolutePath.dir}")
    private String absolutePath;

    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /* 회원가입 */
    @PostMapping("/signUp")
    public ResponseEntity<ResponseDTO> signUp(@RequestBody UserDTO user){

        log.info("user=====>{}", user);
        try{
            UserDTO result = userService.signUp(user);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseDTO(HttpStatus.CREATED, "회원가입 성공", result));

        } catch (Exception e){

            UserDTO empty = new UserDTO();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), empty));
        }
    }

    /* 구글 로그인 */
    @PostMapping("/googleLogin")
    public ResponseEntity<ResponseDTO> googleLogin(@RequestBody Map<String, String> credential) throws Exception {

        String idTokenString = credential.get("credential");

        TokenDTO result = userService.googleLogin(idTokenString);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(HttpStatus.OK, "구글 로그인에 성공하셨습니다.", result));
    }

    /* RTK로 인한 ATK 재발급 */
    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseDTO> RefreshToken(@RequestBody Map<String, String> userRTK) throws Exception {

        TokenDTO result = userService.refreshToken(userRTK);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO( HttpStatus.OK, "리프레시 토큰 발급에 성공하였습니다.", result));
    }

    /* 유저 조회 코드 */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO> findByUser(@PathVariable String userId){

        UserDTO result = userService.findByUser(userId);

        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(HttpStatus.OK, "유저 조회에 성공하였습니다.", result));
    }

    /* 친구 조회 */
    @GetMapping("friend/{userId}")
    public ResponseEntity<ResponseDTO> findFriend(@PathVariable int userId){


        List<UserDTO> result = userService.findByFriend(userId);

        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(HttpStatus.OK, "친구 조회에 성공하셨습니다.", result));
    }

//    /* 친구 요청 */
//    @PostMapping("/friend/{userId}")
//    public ResponseEntity<ResponseDTO> saveFriend(@PathVariable String userId,
//                                                  @RequestBody Map<String, String> email){
//
//        try{
//            FriendDTO result = userService.saveFriend(userId, email);
//
//            return  ResponseEntity
//                    .status(HttpStatus.OK)
//                    .body(new ResponseDTO(HttpStatus.OK, "친구 신청을 보냈습니다.", result));
//
//        } catch (RuntimeException e){
//            return  ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null));
//        }
//    }
    /* 친구 요청 */
    @MessageMapping("/friend/{userId}")
    public void saveFriend(@DestinationVariable String userId,
                            Map<String, String> message){


        try{
            FriendDTO result = userService.saveFriend(userId, message);

            simpMessagingTemplate.convertAndSend("/topic/friend","성공");
            simpMessagingTemplate.convertAndSend("/topic/friend/" + userId, "친구 신청을 보냈습니다");

        } catch (RuntimeException e){

            simpMessagingTemplate.convertAndSend("/topic/friend","실패");
            simpMessagingTemplate.convertAndSend("/topic/friend/"+userId, e.getMessage());
        }


    }

    /* 친구 요청 조회 */
    @GetMapping("friendRequest/{userId}")
    public ResponseEntity<ResponseDTO> findFriendRequest(@PathVariable int userId){

        SecurityContext context = SecurityContextHolder.getContext();

        // 현재 Authentication 객체를 가져옴
        UserDTO userDTO = (UserDTO) context.getAuthentication().getPrincipal();

        log.info("JWT 토큰의 인증 성공 유무를 확인겸 테스트 입니다~==============>{}",  userDTO.getUserId());


        List<UserDTO> result = userService.findByFriendRequest(userId);

        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(HttpStatus.OK, "친구 요청 조회에 성공하셨습니다.", result));
    }


    /* 친구 요청 수락 */
    @PostMapping("approval/{userId}")
    public ResponseEntity<ResponseDTO> approval(@PathVariable int userId, @RequestBody Map<String, String> friendId){


        friendId.get("friendId");
        UserDTO result = userService.approval(userId, friendId.get("friendId"));

        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(HttpStatus.OK, "친구 요청을 수락하셨습니다.", result));
    }

    /* 친구 요청 거절 */
    @PostMapping("refusal/{userId}")
    public ResponseEntity<ResponseDTO> refusal(@PathVariable int userId, @RequestBody Map<String, String> friendId){


        friendId.get("friendId");
        UserDTO result = userService.refusal(userId, friendId.get("friendId"));

        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(HttpStatus.OK, "친구 요청을 거절하셨습니다.", result));

    }

    /* 비밀번호 변경 */
    @PostMapping("/changePassword")
    public ResponseEntity<ResponseDTO> changePassword(@RequestBody Map<String, String> user){

        try{
            UserDTO result = userService.changePassword(user);

            return  ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDTO(HttpStatus.OK, "비밀번호 변경에 성공하셨습니다.", result));

        } catch (RuntimeException e){
            return  ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null));
        }
    }

    /* 프로필 수정 */
    @PostMapping("change/{userId}")
    public ResponseEntity<ResponseDTO> changeUser (@PathVariable int userId,
                                                   @RequestParam(required = false) MultipartFile file,
                                                   @RequestParam String nickname,
                                                   @RequestParam String password,
                                                   @RequestParam String language) throws IOException {

        Map<String, String> formData = new HashMap<>();
        formData.put("nickname", nickname);
        formData.put("password", password);
        formData.put("language", language);


        try {

            UserDTO result = userService.changeUser(userId, file, formData);

            return  ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDTO(HttpStatus.OK, "프로필 변경에 성공하였습니다.", result));

        } catch (RuntimeException e){
            return  ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR,  e.getMessage(), null));
        }
    }
}
