package com.example.DummyTalk.Auth.Controller;

import com.example.DummyTalk.Auth.Service.AuthService;
import com.example.DummyTalk.Common.DTO.ResponseDTO;
import com.example.DummyTalk.User.DTO.TokenDTO;
import com.example.DummyTalk.User.DTO.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authSerivce;

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> Login(@RequestBody UserDTO userDTO) throws Exception {

        TokenDTO result = authSerivce.login(userDTO);

        return ResponseEntity.
                status(HttpStatus.OK)
                .body(new ResponseDTO( HttpStatus.OK, "로그인에 성공하셨습니다.", result));
    }
}