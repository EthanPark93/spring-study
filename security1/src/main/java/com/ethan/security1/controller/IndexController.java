package com.ethan.security1.controller;

import com.ethan.security1.model.User;
import com.ethan.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // view return
public class IndexController {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public IndexController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping({"", "/"})
    public String index() {
        // 머스태치 기본 폴더 src/main/resources
        // 뷰리졸버 (prefix) /templates (suffix) .mustache 생략 가능
        return "index"; // src/main/resources/templates/index.mustache
    }

    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/manager")
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
        System.out.println(user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user); //회원가입 되지만 비밀번호 : 1234 => 암호화 안되어서 시큐리티로 로그인 불가

        return "redirect:/loginForm";
    }

}
