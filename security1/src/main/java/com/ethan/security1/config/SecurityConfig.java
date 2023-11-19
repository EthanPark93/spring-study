package com.ethan.security1.config;

// 1. 코드받기(인증)
// 2. 엑세스 토큰(권한)
// 3. 사용자 정보 가져온다.
// 4-1. 자동 회원 가입 가능
// 4-2 (이메일, 전화번호, 이름, 아이디) 쇼핑몰 -> (집주소), 백화점몰 -> (등급)

import com.ethan.security1.config.oauth.PrincipalOauth2UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@AllArgsConstructor
@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
//@EnableGlobalMethodSecurity // deprecated 됨. EnableMethodSecurity 를 대신 사용하라고 한다.
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
// securedEnabled => Secured 애노테이션 사용 여부, prePostEnabled => PreAuthorize 어노테이션 사용 여부.
public class SecurityConfig {

    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http.csrf(CsrfConfigurer::disable);
        http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                        .anyRequest().permitAll()
        ).formLogin(formLogin ->
                        formLogin
                                .loginPage("/loginForm")
//                        .usernameParameter("파라미터 바꾸고싶으면 여기에 입력")
                                .loginProcessingUrl("/login") // 시큐리티에서 낚아챔
                                .defaultSuccessUrl("/")
        ).oauth2Login(oauth2 -> // .oauth2Login(Customizer.withDefaults());
                oauth2
                        .loginPage("/loginForm")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                        .userService(principalOauth2UserService) // 구글 로그인이 완료된 뒤의 후처리가 필요함. Tip.  코드 x (엑세스토큰+사용자 정보 o)
                )
        );


        return http.build();
    }

}
