package com.example.DummyTalk.User.Service;

import com.example.DummyTalk.AES.AESUtil;
import com.example.DummyTalk.Auth.Entity.RefreshToken;
import com.example.DummyTalk.Auth.Repository.RefreshTokenRepository;
import com.example.DummyTalk.Aws.AwsS3Service;
import com.example.DummyTalk.Chat.Channel.Dto.ImageDto;
import com.example.DummyTalk.Jwt.TokenProvider;
import com.example.DummyTalk.User.DTO.FriendDTO;
import com.example.DummyTalk.User.DTO.TokenDTO;
import com.example.DummyTalk.User.DTO.UserDTO;
import com.example.DummyTalk.User.Entity.Friend;
import com.example.DummyTalk.User.Entity.User;
import com.example.DummyTalk.User.Repository.FriendRepository;
import com.example.DummyTalk.User.Repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService  {

    @Value("${serverAbsolutePath.dir}")
    private String absolutePath;

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AwsS3Service awsS3UploadService;

    private final String BUCKET_DIR = "profile/";

    // 회원가입
    public UserDTO signUp(UserDTO userDTO) throws Exception {

        String email = userDTO.getUserEmail();

        boolean isExists =  userRepository.existsByUserEmail(email);

        if (!isExists){

            int keyLength = 64;

            // 안전한 랜덤 바이트 생성
            byte[] keyBytes = generateRandomBytes(keyLength);

            // Base64로 인코딩하여 JWT 시크릿 키 생성
            String jwtKey = Base64.getEncoder().encodeToString(keyBytes);

            // AWS KMS(AES 256)를 활용한 암호화
//            String encrtptJWT = aesUtil.encrypt(kmsClient, jwtKey);

            // 서울시간으로 가져오기 위해 + 9시간
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime plus9Hours = currentDateTime.plusHours(9);


            userDTO.setCreateAt(plus9Hours);                                    // 유저에게 현재 시간 추가
            userDTO.setUserSecretKey(jwtKey);
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword())); // 비밀번호 인코딩

            // 닉네임을 입력하지 않았을 경우 이름으로 등록
            if(userDTO.getNickname().isEmpty()){
                userDTO.setNickname(userDTO.getName());
            }

            User user = modelMapper.map(userDTO, User.class);

            User createUser= userRepository.save(user);

            return modelMapper.map(createUser, UserDTO.class);
        } else {

            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
    }

    // 구글 로그인
    public TokenDTO googleLogin(String credential) throws Exception {

        User result =  userRepository.findByCredential(credential.substring(0, 400));

        // 등록된 계정이 아닐 경우 등록
        if(result == null){

            // 구글에서 받아온 IdToken으로 GoolgeToken 생성
            GoogleIdToken idToken = GoogleIdToken.parse(new GsonFactory(), credential);
            GoogleIdToken.Payload payload = idToken.getPayload();
            // Token에서 email 추출
            String email = payload.getEmail();

            int keyLength = 64;

            // 안전한 랜덤 바이트 생성
            byte[] keyBytes = generateRandomBytes(keyLength);

            // Base64로 인코딩하여 JWT 시크릿 키 생성
            String jwtKey = Base64.getEncoder().encodeToString(keyBytes);
            
            // AWS KMS(AES 256)를 활용한 암호화
//            String encrtptJWT = aesUtil.encrypt(kmsClient, jwtKey);

            // 서울시간으로 가져오기 위해 + 9시간
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime plus9Hours = currentDateTime.plusHours(9);

            UserDTO userDTO = UserDTO.builder()
                                        .nickname(getEmailUsername(email))
                                        .credential(credential.substring(0, 400))
                                        .createAt(plus9Hours)
                                        .userSecretKey(jwtKey)
                                        .userEmail(email)
                                        .nationalLanguage("kor_Hang")
                                        .build();

            User user = modelMapper.map(userDTO, User.class);

            User resultUser = userRepository.save(user);

            return tokenProvider.generateTokenDTO(resultUser);

        } else {

            return tokenProvider.generateTokenDTO(result);
        }
    }

    public TokenDTO refreshToken(Map<String, String>  refreshToken) {

        // 리프레시토큰으로 redis 조회

        // if 있을 경우 token 발급

        // 없을 경우 null return

        return null;
    }

    public UserDTO  findByUser(String userId){

        User user = userRepository.findById(Long.parseLong(userId)).get();

        return modelMapper.map(user, UserDTO.class);
    }

    public FriendDTO saveFriend(String userId, Map<String,String> email){


        Long LuserId = Long.parseLong(userId);

        String resultEmail =  email.get("email"); // 입력한 이메일

        User friend = userRepository.findByUserEmail(resultEmail);
        User user = userRepository.findByUserId(LuserId);

        List<Friend> friendList = friendRepository.findByUser(LuserId);


        if(friend == null){
            throw new RuntimeException("존재하지 않은 회원입니다.");
        } else {
            if (friend.getUserId() == user.getUserId()) {

                throw new RuntimeException("본인은 친구로 추가할 수 없습니다.");
            } else {

                for(Friend friend1 : friendList){
                    if(friend1.getFriendUser().getUserEmail().equals(resultEmail)){
                        throw new RuntimeException("이미 친구요청을 보낸 이메일입니다.");
                    }
                }
                Friend addFriend = Friend.builder()
                                            .userId(LuserId)                    // 친구요청을 보낸 사람
                                            .friendUserId(friend.getUserId())   // 친구요청을 받은 사람
                                            .accept("N")                        // 수락 대기중
                                            .build();


                Friend resultFriend = friendRepository.save(addFriend);

                return modelMapper.map(resultFriend, FriendDTO.class);
            }
        }
    }

    @Transactional
    public UserDTO changePassword(Map<String, String> user) {

        String email = user.get("email");
        String password = user.get("password");


        User findUser =  userRepository.findByUserEmail(email);

        findUser.setPassword(passwordEncoder.encode(password));

        log.info("TestPassword");
        return modelMapper.map(findUser, UserDTO.class);

    }

    public List<UserDTO> findByFriend(int userId) {

        List<User> userList = userRepository.findByFriends(userId);

        List<UserDTO> result = userList.stream().map(User -> modelMapper.map(User, UserDTO.class)).collect(Collectors.toList());

        return result;
    }
    public List<UserDTO> findByFriendRequest(int userId) {

        List<User> userList = userRepository.findByFriendRequest(userId);

        List<UserDTO> result = userList.stream().map(User -> modelMapper.map(User, UserDTO.class)).collect(Collectors.toList());
        return result;

    }

    public UserDTO changeUser(int userId, MultipartFile file, Map<String, String> formData) throws IOException {

        User user = userRepository.findByUserId(Long.valueOf(userId));

        if(file != null){

            ImageDto imageDto = awsS3UploadService.upload(file, BUCKET_DIR);
            String imagePath =  imageDto.getFilePath();
            user.setUserImgPath(imagePath);

            String filePath = absolutePath;
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();

            File saveFile = new File(filePath, fileName);
            file.transferTo(saveFile);
        }
        if (!formData.get("nickname").isEmpty()) {
            user.setNickname(formData.get("nickname"));
        }
        if (!formData.get("language").isEmpty()) {
            user.setNationalLanguage(formData.get("language"));
        }
        if (!formData.get("password").isEmpty()) {
            if(user.getCredential() != null){
                throw new RemoteException("구글로그인은 비밀번호 변경이 불가능합니다.");
            }
            user.setPassword(passwordEncoder.encode(formData.get("password")));
        }
        return modelMapper.map(user, UserDTO.class);

    }


    public UserDTO approval(int userId, String friendId) {

        Friend friend =friendRepository.findByFriend(userId, friendId);

        friend.setAccept("Y");

        Friend user = Friend.builder()
                                .userId(Long.valueOf(userId))
                                .friendUserId(Long.parseLong(friendId))
                                .accept("Y")
                                .build();

        friendRepository.save(user);

        return null;
    }

    public UserDTO refusal(int userId, String friendId) {

        Friend friend =friendRepository.findByFriend(userId, friendId);

        friendRepository.deleteById(friend.getFriendId());

        return null;
    }

    // 랜덤한 JWT SecretKey 생성
    private static byte[] generateRandomBytes(int length) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }


    public static String getEmailUsername(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex != -1) {
            // "@" 앞의 문자열 추출
            String username = email.substring(0, atIndex);
            return username;
        } else {
            // "@"이 없는 경우 빈 문자열 반환 또는 오류 처리
            return "";
        }
    }

}
