package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth") // ✅ 공통 URL Prefix 적용
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    
    // ✅ 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "/auth/deulmaru_Login";
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String mypage() {
        return "/auth/mypage";
    }

    // ✅ 로그인 처리 (일반 로그인)
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Optional<UserEntity> user = userService.loginUser(username, password, session);
        if (user.isPresent()) {
            session.setAttribute("user", user.get());
            model.addAttribute("user", user.get());
            return "redirect:/";
        } else {
            model.addAttribute("error", "로그인 실패: 아이디 또는 비밀번호가 틀립니다.");
            return "login";
        }
    }

    // ✅ 로그아웃 처리
    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ✅ 회원가입 선택 페이지
    @GetMapping("/register-options")
    public String showRegisterOptions() {
        return "auth/deulmaru_SignIn_Main"; // ✅ templates/auth/register-options.html
    }

    // ✅ 일반 회원가입 페이지
    @GetMapping("/register")
    public String registerPage() {
        return "auth/deulmaru_SignIn";
    }

    // ✅ 일반 회원가입 처리
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String nickname, HttpSession session, Model model) {
        try {
            UserEntity user = userService.registerUser(username, password, nickname, session);
            session.setAttribute("user", user);
            model.addAttribute("user", user);
            return "redirect:/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/deulmaru_Login")
    public String deulmaru_Login() {
        return "auth/deulmaru_Login"; // ✅ 이 경로가 templates 내부에 존재하는지 확인
    }
    
    
    @GetMapping("/signin")
    public String signInPage() {
        return "auth/deulmaru_SignIn_Main"; // ✅ 이 경로가 templates 내부에 존재하는지 확인
    }
    

    @GetMapping("/deulmaru_SignIn")
    public String deulmaruSignIn() {
        return "auth/deulmaru_SignIn"; // ✅ templates/auth/kakao-register.html을 찾도록 변경
    }
    
    
    
    
    
    
    
}
