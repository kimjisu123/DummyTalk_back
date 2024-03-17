package com.example.DummyTalk.Auth.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 4) // Redis Lettuce를 사용하기 위해 작성, redis 저장소의
                                                            // key로는 { value } : {@Id 어노테이션이 붙은 값}이 저장됨

public class RefreshToken {

    @Id
    private String refreshToken;

    private Long userId;

}
