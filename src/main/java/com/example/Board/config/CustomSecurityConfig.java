package com.example.Board.config;

import com.example.Board.security.CustomUserDetailService;
import com.example.Board.security.handler.Custom403Handler;
import com.example.Board.security.handler.CustomSocialLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
@Log4j2
@Configuration
//prePostEnabled 속성은 원하는 곳에 @PreAuthorize 혹은 @PostAuthorize 어노테이션을 이용해서 사전 혹은 사후의 권한을 체크할 수 있다.
//메서드 단위의 보안을 설정할 수 있다.
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class CustomSecurityConfig
{

    //주입 필요
    private final DataSource dataSource;
    private final CustomUserDetailService userDetailService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception 
    {
        log.info("------------configure-------------------");

        //HttpSecurity의 formLogin() 관련해서 loginPage()를 지정하면 로그인이 필요한 경우에 /member/login 경로로 자동 리다이렉트
        http.formLogin( httpSecurityFormLoginConfigurer -> {
            httpSecurityFormLoginConfigurer.loginPage("/member/login");
        });

        http.logout(LogoutConfigurer::deleteCookies);
        
        //csrf 토큰 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        //자동 로그인, 쿠키 발행
        http.rememberMe(httpSecurityRememberMeConfigurer ->
        {
            httpSecurityRememberMeConfigurer.key("12345678")
                    .tokenRepository(persistentTokenRepository())
                    .userDetailsService(userDetailService)
                    .tokenValiditySeconds(60*60*24*30);
        });

        //접근 거부 처리 설정
        http.exceptionHandling( httpSecurityExceptionHandlingConfigurer -> {
            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler());
        });

        //OAuth2 로그인 설정
        http.oauth2Login( httpSecurityOauth2LoginConfigurer -> {
            httpSecurityOauth2LoginConfigurer.successHandler(authenticationSuccessHandler());
        });

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler()
    {
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer()
    {

        log.info("------------web configure-------------------");

        //완전히 정적으로 동작하는 파일들에는 굳이 시큐리티를 적용할 필요가 없으므로
        //CustomSecurityConfig에 webSecurityCustomizer() 메서드 설정을 추가
        return (web) -> web.ignoring().requestMatchers(PathRequest.
                toStaticResources().atCommonLocations());

    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository()
    {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }
}
