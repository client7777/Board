package com.example.Board.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
// User 클래스는 UserDetails 인터페이스를 구현한 클래스로 최대한 간단하게 UserDetails 타입을 생성할 수 있는 방법을 제공
public class MemberSecurityDTO extends User implements OAuth2User // 소셜 로그인에서도 사용할 수 있도록 OAuth2User 인터페이스 구현 추가
{
    private String mid;

    private String mpw;

    private String email;

    private boolean del;

    private boolean social;

    private Map<String, Object> props; // 소셜 로그인 정보

    public MemberSecurityDTO(String userName, String password, String email,
                             boolean del, boolean social,
                             Collection<? extends GrantedAuthority> authorities)
    {
        super(userName, password, authorities);

        this.mid = userName;

        this.mpw = password;

        this.email = email;

        this.del = del;

        this.social = social;
    }

    @Override
    public Map<String, Object> getAttributes()
    {
        return this.getProps();
    }

    @Override
    public String getName()
    {
        return this.mid;
    }
}
