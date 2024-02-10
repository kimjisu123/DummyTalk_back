package com.example.DummyTalk.Jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
        Long userNo = getUserNo(request);

        /* 추출한 토큰의 유효성 검사 후 인증을 위해 Authentication 객체를 SecurityContextHolder에 담는다.*/
        if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt, userNo)){
            Authentication authentication = tokenProvider.getAuthentication(jwt, userNo);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 추후 다른 필터, 서블릿, 컨트롤러 등등에서 인증 객체를 사용하기 위해 SecurityContext에 값을 담음
            // SecurityContextHolder : Spring Security에서는 현재 사용자와 관련된 SecurityContext를 제공, 현재 스레드의 보안 컨텍스트를 저장.
            // getContext() : SecurityContextHolder의 정적 메소드로 현재 실행중인 *스레드의 SecurityContext를 반환
            // setAuthentication(authentication) : 'SecurityContext'에 현재 사용자의 인증 객체를 설정합니다.
        }
        filterChain.doFilter(request, response); // 다음 filterchain 진행
    }


    /* Request Header에서 토큰 정보 꺼내기(여기서 위에서 선언한 두개의 static변수를 사용할꺼)*/
    public static String resolveToken(HttpServletRequest request){

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(8); // 사용자가 보낸 토큰 값추출
            // 토큰 생성 패턴이 Bearer ==byoj...
        }

        return null;
    }

    /* Request Header에 같이 전송한 userNo을 꺼내기 */
    public static Long getUserNo(HttpServletRequest request){

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return  Long.valueOf(bearerToken.substring(7, 8));
            // 토큰 앞에 userNo을 같이 넘김
        }
        return null;
    }
}