package com.asdf.minilog.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinilogUserDetails implements UserDetails { // 사용자 정보를 캡슐화하는 인터페이스
    private Long id;
    private String username; // 사용자명
    private String password; // 비밀번호
    private Collection<? extends GrantedAuthority> authorities; // 권한
}
