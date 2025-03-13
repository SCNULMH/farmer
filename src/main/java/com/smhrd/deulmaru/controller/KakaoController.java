package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.repository.UserRepository;
import com.smhrd.deulmaru.service.KakaoAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/auth/kakao")
public class KakaoController {

    @Autowired
    private KakaoAuthService kakaoAuthService;

    @Autowired
    private UserRepository userRepository;
    
    // ✅ 카카오 로그인 페이지
    @GetMapping("/login")
    public String kakaoLoginPage() {
        return "auth/kakao-login";
    }
    
    // ✅ 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/";
    }

    // ✅ 카카오 로그인 후 콜백 처리
    @GetMapping("/callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpSession session, Model model) {
        String accessToken = kakaoAuthService.getKakaoAccessToken(code);
        if (accessToken == null) {
            return "redirect:/error";
        }
        session.setAttribute("kakaoAccessToken", accessToken);

        Map<String, Object> kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(accessToken);
        if (kakaoUserInfo == null || !kakaoUserInfo.containsKey("kakaoId")) {
            return "redirect:/error";
        }

        Long kakaoId = ((Number) kakaoUserInfo.get("kakaoId")).longValue();

        Optional<UserEntity> existingUser = userRepository.findByKakaoId(kakaoId);
        if (existingUser.isPresent()) {
            session.setAttribute("user", existingUser.get());
            model.addAttribute("user", existingUser.get());
            return "redirect:/mypage";
        }

        String email = kakaoUserInfo.containsKey("email") ? (String) kakaoUserInfo.get("email") : null;
        if (email != null) {
            Optional<UserEntity> normalUser = userRepository.findByUserId(email);
            if (normalUser.isPresent()) {
                session.setAttribute("kakaoUserInfo", kakaoUserInfo);
                return "redirect:/auth/kakao/link";
            }
        }
        
        session.setAttribute("kakaoUserInfo", kakaoUserInfo);
        return "redirect:/auth/kakao/register";
    }

    // ✅ 카카오 계정 연동 페이지
    @GetMapping("/link")
    public String showKakaoLinkPage(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Map<String, Object> kakaoUserInfo = (Map<String, Object>) session.getAttribute("kakaoUserInfo");

        if (user == null) {
            return "redirect:/auth/login"; // 일반 로그인 페이지로 리디렉트
        }

        if (user.getKakaoId() != null) {
            model.addAttribute("alreadyLinked", true);
            model.addAttribute("linkedKakaoId", user.getKakaoId());
            return "redirect:/mypage";
        }

        model.addAttribute("alreadyLinked", false);
        model.addAttribute("kakaoUserInfo", kakaoUserInfo);
        return "auth/kakao-link";
    }

    // ✅ 카카오 계정 연동 처리
    @PostMapping("/link")
    public String linkKakaoAccount(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Map<String, Object> kakaoUserInfo = (Map<String, Object>) session.getAttribute("kakaoUserInfo");

        if (user == null || kakaoUserInfo == null) {
            return "redirect:/auth/kakao/login";
        }

        Long kakaoId = ((Number) kakaoUserInfo.get("kakaoId")).longValue();
        user.setKakaoId(kakaoId);
        userRepository.save(user);
        
        session.setAttribute("user", user);
        model.addAttribute("user", user);

        return "redirect:/mypage";
    }

    // ✅ 카카오 계정 연동 해제
    @PostMapping("/unlink")
    public String unlinkKakao(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        user.setKakaoId(null);
        userRepository.save(user);

        session.setAttribute("user", user);
        model.addAttribute("user", user);

        return "redirect:/mypage";
    }

    // ✅ 카카오 회원가입 페이지
    @GetMapping("/register")
    public String kakaoRegisterPage(HttpSession session, Model model) {
        Map<String, Object> kakaoUserInfo = (Map<String, Object>) session.getAttribute("kakaoUserInfo");
        if (kakaoUserInfo == null || !kakaoUserInfo.containsKey("kakaoId")) {
            return "redirect:/auth/kakao/login";
        }
        model.addAttribute("kakaoUserInfo", kakaoUserInfo);
        return "auth/kakao-register";
    }

    // ✅ 카카오 회원가입 처리
    @PostMapping("/register")
    public String kakaoRegister(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String nickname,
                                HttpSession session, Model model) {
        Map<String, Object> kakaoUserInfo = (Map<String, Object>) session.getAttribute("kakaoUserInfo");
        if (kakaoUserInfo == null || !kakaoUserInfo.containsKey("kakaoId")) {
            return "redirect:/auth/kakao/login";
        }
        Long kakaoId = ((Number) kakaoUserInfo.get("kakaoId")).longValue();

        if (userRepository.findByUserId(username).isPresent()) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            return "auth/kakao-register";
        }

        UserEntity newUser = new UserEntity(username, password, nickname, kakaoId, (String) kakaoUserInfo.get("profileImage"));
        userRepository.save(newUser);
        session.setAttribute("user", newUser);
        model.addAttribute("user", newUser);

        return "redirect:/mypage";
    }
}
