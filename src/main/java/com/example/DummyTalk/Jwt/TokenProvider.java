package com.example.DummyTalk.Jwt;


import com.example.DummyTalk.Exception.TokenException;
import com.example.DummyTalk.User.DTO.TokenDTO;
import com.example.DummyTalk.User.Entity.User;
import com.example.DummyTalk.User.Repository.UserRepository;
import com.example.DummyTalk.User.Service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {
    private static final String BEARER_TYPE = "Bearer";   // Bearer 토큰 사용시 앞에 붙이는 prefix문자열
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 8; // 8시간으로 설정
    private static final String AUTHORITIES_KEY = "auth";
    private final CustomUserDetailsService customUserDetailsService;  // 사용자의 인증 및 권한 정보를 가져올수 있음
    private final UserRepository userRepository;
    private Key key;


    /* 1. 토큰(xxxxx.yyyyy.zzzzz) 생성 메소드 */
    public TokenDTO generateTokenDTO(User user) throws Exception {

        // secret key 복호화
//        String decryptJWT = aesUtil.decrypt(kmsClient, user.getUserSecretKey());

        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(user.getUserSecretKey()));

        /* 1. 회원 아이디를 "sub"이라는 클레임으로 토큰으로 추가 */
        Claims claims = Jwts.claims().setSubject(String.valueOf(user.getUserId()));    // ex) { sub : memberId }

        claims.put(AUTHORITIES_KEY, "role");
        claims.put("nickname", user.getNickname());
        claims.put("userName", user.getName());
        claims.put("national_language", user.getNationalLanguage());

        long now = System.currentTimeMillis();  // 현재시간을 밀리세컨드단위로 가져옴

        Date accessTokenExpriesIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME); // java.util.Date로 import

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(accessTokenExpriesIn) // 토큰의 만료기간을 DATE형으로 토큰에 추가(exp라는 클레임으로 long형으로 토큰에 추가)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return new TokenDTO(BEARER_TYPE, user.getUserEmail()
                , accessToken, accessTokenExpriesIn.getTime());
    }

    /* 2. 토큰의 등록된 클레임의 subject에서 해당 회원의 아이디를 추출 */
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build() // 시크릿 키 필요
                .parseClaimsJws(token)
                .getBody()          // payload의 Clamis 추출
                .getSubject();      // Claim중에 등록 클레임에 해당하는 sub값 추출(회원 아이디)
    }

    /* 3. AccessToken으로 인증 객체 추출 */
    public Authentication getAuthentication(String token, Long userNo){

        // 토큰에서 claim들을 추출
        Claims claims = parseClaims(token, userNo);

        if(claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
//        Collection<? extends GrantedAuthority> authorities =
//                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))    // ex: "ROLE_ADMIN"이랑 "ROLE_MEMBER"같은 문자열이 들어있는 문자열 배열
//                        .map(role -> new SimpleGrantedAuthority(role))              // 문자열 배열에 들어있는 권한 문자열 마다 SimpleGrantedAuthority 객체로 만듦
//                        .collect(Collectors.toList());
//
//        log.info("[TokenProvider] authorities {} ", authorities);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject()); // loadUserByUsername 사용자의 이름을 기반으로 사용자의 정보를 가져옴

        log.info("[TokenProvider] ===================== {}",  userDetails.getAuthorities());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        // UsernamePasswordAuthenticationToken는 Authentication를 생성해주는 클래스
        // Authentication 객체로 반드시 userDetails를 상속한 객체를 넣지 않아도 된다.
        /* new UsernamePasswordAuthenticationToken (Authentication)
        * principal : 주로 사용자의 신원 정보를 나타내며, 여기에서는 'userDetails'가 해당 역할을 한다. 'principal'은 사용자의 주요 식별 정보를 나타냅니다.
        * credentials : 사용자의 비밀번호나 인증 토큰을 나타내는데 사용이 되며 보통 userDetails 내부에 포함이 되어있거나 사용하지 않는 경우가 있다.
        * authorities : 사용자의 권한 목록을 나타냅니다. 주로 userDetails.getAuthorities()를 사용하여 사용자의 권한 정보를 가져옵니다.
        *  */
        // UsernamePasswordAuthenticationToken : 사용자의 신원 정보와 권한을 저장하고 전달하는데 사용
        // userDetails :  사용자의 이름, 비밀번호, 권한을 포함한 사용자의 세부 정보를 나타내는 객체를 의미
        // "" : 두 번째 매개변수는 사용자의 비밀번호를 나타냅니다. 비밀번호가 이미 userDetails 객체에 포함이 되어 있거나 인증시에 사용을 하지 않기도 한다.
        // userDetails.getAuthorities() : 마지막 매개변수는 사용자의 권한을 나타냅니다. 여기서도 userDetails 객체에서 권한 정보를 가져와 설정합니다.
    }

    /* 4. 토큰 유효성 검사 */
    public boolean validateToken(String token, Long userNo){

        User user = userRepository.findByUserId(userNo);


        try{
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(user.getUserSecretKey()))).build().parseClaimsJws(token);
            return true;
        }catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("[TokenProvider] 잘못된 JWT 서명입니다.");
            throw new TokenException("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e){
            log.info("[TokenProvider] 만료된 JWT 토큰입니다.");
            throw new TokenException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e){
            log.info("[TokenProvider] 지원되지 않는 JWT 토큰입니다.");
            throw new TokenException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e){
            log.info("[TokenProvider] JWT 토큰이 잘못되었습니다.");
            throw new TokenException("JWT 토큰이 잘못되었습니다.");
        }
    }
    /* 5. AccessToken에서 클레임 추출하는 메소드 */
    private Claims parseClaims(String token, Long userNo){

        User user = userRepository.findByUserId(userNo);
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(user.getUserSecretKey()));

        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();            // 토큰이 만료되어 예외가 발생하더라도 클레임 값들을 뽑을 수 있다.
        }

    }
}