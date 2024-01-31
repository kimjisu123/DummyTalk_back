package com.example.DummyTalk.Mail.Service;


import com.example.DummyTalk.User.Entity.User;
import com.example.DummyTalk.User.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    private final UserRepository userRepository;
    private static final String senderEmail= "gohwangbong@gmail.com";
    private static int number;

    public static void createNumber(){
        number = (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    public MimeMessage CreateMail(String mail){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    public String sendMail(String userEmail){

        List<User> userList = userRepository.findAll();

        // 이메일 중복 체크
        for(User user : userList){
            if(user.getUserEmail().equals(userEmail.replace("\"", ""))){
                throw new RuntimeException("이미 중복된 이메일이 존재합니다.");
            }
        }
        try{
            MimeMessage message = CreateMail(userEmail);
            javaMailSender.send(message);

            return "해당 이메일로 인증번호가 전송되었습니다.";
        } catch (RuntimeException e){

            throw new RuntimeException("인증번호 전송에 실패하였습니다.");
        }

    }
    public String checkNum(int checkNum) {

        if(number == checkNum){

            return "인증에 성공하였습니다.";
        } else{

            throw new RuntimeException("인증번호 체크 오류");
        }
    }

    public String passwordMail(String email) {


        User findUser =  userRepository.findByUserEmail(email);

        if(findUser.getCredential() != null){
            throw new RuntimeException("구글로 회원가입된 이메일입니다.");
        }
        try{

            MimeMessage message = CreateMail(email);
            javaMailSender.send(message);

            return "해당 이메일로 인증번호가 전송되었습니다.";
        } catch (RuntimeException e){

            throw new RuntimeException("인증번호 전송에 실패하였습니다.");
        }
    }
}