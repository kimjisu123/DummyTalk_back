package com.example.DummyTalk.Auth.Service;

import com.example.DummyTalk.Jwt.TokenProvider;
import com.example.DummyTalk.User.DTO.TokenDTO;
import com.example.DummyTalk.User.DTO.UserDTO;
import com.example.DummyTalk.User.Entity.User;
import com.example.DummyTalk.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    private final ModelMapper modelMapper;

    public TokenDTO login(UserDTO userDTO) throws Exception {

        // 기존 유저 확인 (email)
        User user =userRepository.findByUserEmail(userDTO.getUserEmail());

        // 이메일(아이디) 체크
        if(user == null){
            throw new RuntimeException("존재하지 않는 이메일 입니다.");
        }

        // 비밀번호 체크
        if(!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())){
            throw new RuntimeException("잘못된 비밀번호를 입력하셨습니다.");
        }

        return tokenProvider.generateTokenDTO(user);
    }


}
