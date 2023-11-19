package com.ethan.security1.config.auth;

//  시큐리티타 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
//  로그인 진행이 완료되면 시큐리티 session을 만들어준다. (Security ContextHoler)
//  오브젝트 => Authentication 타입 객체
//  Authentication 안에 User 정보가 있어야 함.
//  User 오브젝트타입 => UserDetails 타입 객체

import com.ethan.security1.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// Security Session => Authentication => UserDetails(PrincipalDetails)

@Data
@AllArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user; //   콤포지션

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    //  해당 User의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    //  만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {

        //  우리 사이트 1년 동안 회원이 로그인을 안하면 휴면 처리하기로 함.
        //  현재 시간 - 로그인 시간 => 1년을 초과하면 return false

        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}
