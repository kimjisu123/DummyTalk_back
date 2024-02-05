package com.example.DummyTalk.Jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization"; // 사용자가 request header에 Authorization 속성으로 token을 던진다.

    public static final String BEARER_PREFIX = "Bearer";               // 사용자가 던지는 토큰 값만 파싱하기 위한 접두사 저장용 변수(접두사는 Bearer라는 표준으로 정의됨)

    private final TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = resolveToken(request);  // accessToken


        /* 추출한 토큰의 유효성 검사 후 인증을 위해 Authentication 객체를 SecurityContextHolder에 담는다.*/
        if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
                Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response); // 다음 filterchain 진행
    }

    /* Request Header에서 토큰 정보 꺼내기(여기서 위에서 선언한 두개의 static변수를 사용할꺼)*/
    public static String resolveToken(HttpServletRequest request){

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        String pathInfo = request.getRequestURI();

        log.info("pathInfo============>{}", pathInfo);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(6); // 사용자가 보낸 토큰 값추출
            // 토큰 생성 패턴이 Bearer ==byoj...
        }
        return null;
    }
}