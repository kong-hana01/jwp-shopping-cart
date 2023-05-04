package cart.controller;

import cart.authorization.AuthorizationInformation;
import cart.entity.Member;
import cart.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/settings")
public class MemberController {

    @Autowired
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @ModelAttribute
    public String displayMember(Model model) {
        model.addAttribute("members", memberService.findAll());
        return "settings";
    }

    @PostConstruct
    public void init() {
        memberService.save(new AuthorizationInformation("kong123@gmail.com", "password"));
        memberService.save(new AuthorizationInformation("kong@gmail.com", "123"));
    }
}
