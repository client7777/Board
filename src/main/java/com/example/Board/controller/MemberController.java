package com.example.Board.controller;

import com.example.Board.dto.MemberJoinDTO;
import com.example.Board.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController
{   
    //의존성 주입
    private final MemberService memberService;

    @GetMapping("/login")
    public void loginGET(String errorCode, String logout)
    {
        log.info("login get....");
        log.info("logout: " + logout);

        if(logout != null)
        {
            log.info("user logout...");
        }
    }

    //get 방식으로 회원가입 페이지를 볼 수 있도록 구성하고 post 방식으로 데이터베이스에 추가
    @GetMapping("/join")
    public void joinGET()
    {
        log.info("join get");
    }

    @PostMapping("/join")
    public String joinPOST(MemberJoinDTO memberJoinDTO, RedirectAttributes redirectAttributes)
    {
        log.info("join post");
        log.info(memberJoinDTO);

        try {
            memberService.join(memberJoinDTO);
        }catch (MemberService.MidExistException e)
        {
            redirectAttributes.addFlashAttribute("error", "mid");
            return "redirect:/member/join";
        }
        redirectAttributes.addAttribute("result", "success");

        return "redirect:/member/login"; // 회원가입 후 로그인
    }
}