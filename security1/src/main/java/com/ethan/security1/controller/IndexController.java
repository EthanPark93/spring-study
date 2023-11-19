package com.ethan.security1.controller;

import com.ethan.security1.config.auth.PrincipalDetails;
import com.ethan.security1.model.User;
import com.ethan.security1.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@AllArgsConstructor
@Controller // view return
public class IndexController {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    @ResponseBody
    public String testLogin(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails,
            @AuthenticationPrincipal PrincipalDetails annotationPrincipalDetails) { // DI (의존성 주입)
        log.info("/test/login ==================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); // 다운 캐스팅으로 기능 회복
        log.info("(principalDetails) authentication.getUser()={}", principalDetails.getUser()); // UserDetails를 implements한 prinsipalDetails로 다운

        log.info("userDetails.getUsername()={}", userDetails.getUsername()); // UserDetails에는 getUser 함수가 없다
        log.info("annotationPrincipalDetails.getUser()={}", annotationPrincipalDetails.getUser()); // @AuthenticationPrincipal을 통해 처음부터 하위 타입으로 받아와, getUser 함수 사용 가능

        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    @ResponseBody
    public String testOAuthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oauth) { // DI (의존성 주입)
        log.info("/test/oauth/login ==================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // 다운 캐스팅으로 기능 회복
        log.info("authentication.getAttributes()={}", oAuth2User.getAttributes());
        log.info("oauth2User={}", oauth.getAttributes());

        return " OAuth 세션 정보 확인하기";
    }

    @GetMapping({"", "/"})
    public String index() {
        // 머스태치 기본 폴더 src/main/resources
        // 뷰리졸버 (prefix) /templates (suffix) .mustache 생략 가능
        return "index"; // src/main/resources/templates/index.mustache
    }

    @GetMapping("/user")
    @ResponseBody
    public String user() {
        return "user";
    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    @ResponseBody
    public String manager() {
        return "manager";
    }

    // 스프링 시큐리티가 낚아챔 -> securityConfig 작성 후 동작 안함
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
//        System.out.println(user);
        log.info("user={}", user);

        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user); //회원가입 되지만 비밀번호 encode하지 않을 경우 : 1234 => 암호화 안되어서 시큐리티로 로그인 불가

        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    @ResponseBody
    public String info() {
        return "개인정보";
    }

//    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/data")
    @ResponseBody
    public String data() {
        return "데이터정보";
    }

    // 유저 권한
    // update user set role = 'ROLE_MANAGER' where username = 'manager';
    // update user set role = 'ROLE_ADMIN' where username = 'admin';
    // commit;

}
