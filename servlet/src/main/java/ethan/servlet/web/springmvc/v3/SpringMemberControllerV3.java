package ethan.servlet.web.springmvc.v3;

import ethan.servlet.domain.member.Member;
import ethan.servlet.domain.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerV3 {
    private MemberRepository memberRepository = MemberRepository.getInstance();
    @RequestMapping("new-form")
    public String newForm() {

        return "new-form";
    }

    @RequestMapping("save")
    public String save(
            @RequestParam String username,
            @RequestParam int age,
            Model model) {

        Member member = new Member(username, age);
        memberRepository.save(member);

        model.addAttribute("member", member);
        return "save-result";
    }

    @RequestMapping
    public String members(Model model) {
        
        List<Member> members = memberRepository.findAll();

        model.addAttribute("members", members);

        return "members";
    }
}
