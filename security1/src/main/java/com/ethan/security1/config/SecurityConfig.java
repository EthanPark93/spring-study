package com.ethan.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
//@EnableGlobalMethodSecurity // deprecated 됨. EnableMethodSecurity 를 대신 사용하라고 한다.
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) // securedEnabled => Secured 애노테이션 사용 여부, prePostEnabled => PreAuthorize 어노테이션 사용 여부.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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
        );

        return http.build();
    }

}
