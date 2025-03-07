package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.config.KakaoConfig;
import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final KakaoConfig kakaoConfig;

    public AuthController(UserService userService, KakaoConfig kakaoConfig) {
        this.userService = userService;
        this.kakaoConfig = kakaoConfig;
    }

    // 마이페이지 조회
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        UserEntity user = userService.findById(userId);
        model.addAttribute("user", user);
        return "auth/mypage";
    }

    // 마이페이지에서 프로필 업데이트
    @PostMapping("/mypage")
    public String updateProfile(HttpSession session,
                                @RequestParam(required = false) String password,
                                @RequestParam String nickname) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        userService.updateUserProfile(userId, password, nickname);
        return "redirect:/auth/mypage";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Optional<UserEntity> user = userService.loginUser(username, password);
        if (user.isPresent()) {
            session.setAttribute("user_id", user.get().getId());
            session.setAttribute("username", user.get().getUsername());
            session.setAttribute("nickname", user.get().getNickname());
            return "redirect:/";
        } else {
            model.addAttribute("error", "로그인 실패: 아이디 또는 비밀번호가 틀립니다.");
            return "auth/login";
        }
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // 회원가입 페이지
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String nickname, Model model) {
        try {
            userService.registerUser(username, password, nickname);
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    // 카카오 연동 페이지 (동적으로 URL 생성)
    @GetMapping("/link-kakao")
    public String linkKakao(Model model) {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + "d25cc0917b9ca618939f361de68c33ac"
                + "&redirect_uri=" + "http://localhost:8082/auth/kakao/callback"
                + "&response_type=code";

        model.addAttribute("kakaoAuthUrl", kakaoAuthUrl);
        return "auth/link-kakao";
    }

}
