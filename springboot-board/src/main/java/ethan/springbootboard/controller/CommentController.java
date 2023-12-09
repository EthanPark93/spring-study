package ethan.springbootboard.controller;

import ethan.springbootboard.dto.CommentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/comment")
public class CommentController {

    @PostMapping("/save")
    @ResponseBody
    public String save(@ModelAttribute CommentDTO commentDTO) {
        log.info("commentDTO = {}", commentDTO);
        return "요청성공";
    }
}
