package com.asdf.minilog.security;


import com.asdf.minilog.entity.Role;
import org.springframework.security.core.GrantedAuthority;

public class MinilogGrantedAuthority implements GrantedAuthority { // 권한을 관리하고 권한 이름을 반환
    private final Role role;

    public MinilogGrantedAuthority(Role role) {
        this.role = role;
    }

    // Spring Security에게 권한 이름 알려주기
    @Override
    public String getAuthority() {
        return role.name();
    }

    // 여기부터는 Java 객체를 제대로 비교/표현하기 위한 것
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj instanceof MinilogGrantedAuthority) {
            return role.equals(((MinilogGrantedAuthority) obj).role);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return role.hashCode();
    }

    @Override
    public String toString() {
        return this.role.name();
    }
}
