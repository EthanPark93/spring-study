package com.ethan.security1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // view return
public class IndexController {

    @GetMapping({"", "/"})
    public String index() {
        // 머스태치 기본 폴더 src/main/resources
        // 뷰리졸버 (prefix) /templates (suffix) .mustache 생략 가능
        return "index"; // src/main/resources/templates/index.mustache
    }

}
