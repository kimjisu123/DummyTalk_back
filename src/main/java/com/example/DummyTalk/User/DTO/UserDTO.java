package com.example.DummyTalk.User.DTO;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDTO implements UserDetails{

    private Long userId;

    private String name;

    private String nickname;

    private String userEmail;

    private String password;

    private String userPhone;

    private String credential;

    private String userImgPath;

    private  String userSecretKey;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private String nationalLanguage;

    private List<UserServerCodeDto> userServerCodeList = new ArrayList<>();

    private Collection<GrantedAuthority> authorities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
