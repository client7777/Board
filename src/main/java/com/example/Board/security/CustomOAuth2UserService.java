package com.example.Board.security;

import com.example.Board.domain.Member;
import com.example.Board.domain.MemberRole;
import com.example.Board.repository.MemberRepository;
import com.example.Board.security.dto.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService
{
    
    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException
    {
        log.info("userRequest");
        log.info(userRequest);

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();

        log.info("Name:" + clientName);

        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> paramMap = oAuth2User.getAttributes();

        String email = null;

        switch (clientName)
        {
            case "kakao":
                email = getKaKaoEmail(paramMap);
                break;
        }
        log.info("==========================================");
        log.info(email);
        log.info("==========================================");

        return generateDTO(email, paramMap);
    }

    private MemberSecurityDTO generateDTO(String email, Map<String, Object> params)
    {
        //이미 회원 가입이 된 회원에 대해서는 기존 정보를 반환하고 새롭게 소셜 로그인된 사용자는 자동으로 회원 가입 처리
        //어떤 상황에서든 MemberSecurityDTO를 반환하기 때문에 사용자는 추가적인 작업이 필요 없음
        Optional<Member> result = memberRepository.findByEmail(email);

        if(result.isEmpty())
        {
            Member member = Member.builder()
                    .mid(email)
                    .mpw(passwordEncoder.encode("1111"))
                    .email(email)
                    .social(true)
                    .build();
            member.addRole(MemberRole.USER);
            memberRepository.save(member);

            MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(email, "1111", email, false, true,
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            memberSecurityDTO.setProps(params);

            return memberSecurityDTO;
        }
        else
        {
            Member member = result.get();
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(
                            member.getMid(),
                            member.getMpw(),
                            member.getEmail(),
                            member.isDel(),
                            member.isSocial(),
                            member.getRoleSet()
                                    .stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.name()))
                                    .collect(Collectors.toList())
                    );
            return memberSecurityDTO;
        }
    }
    private String getKaKaoEmail(Map<String, Object> paramMap)
    {
        log.info("KAKAO----------------------------------------------");

        Object value = paramMap.get("kakao_account");

        log.info(value);

        LinkedHashMap accountMap = (LinkedHashMap) value;

        String email = (String) accountMap.get("email");

        log.info("email" + email);

        return email;
    }
}
/*
* CustomOAuth2UserService는 카카오 서비스에서 얻어온 이메일을 이용해 같은 이메일을 가진 사용자를 찾아보고 없는 경우에 자동으로 회원가입을 하고
* MemberSecurityDTO를 반환
*/