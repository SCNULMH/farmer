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
    
 // ✅ 카카오 로그인 페이지 (로그인 버튼 제공)
    @GetMapping("/login")
    public String kakaoLoginPage() {
        return "auth/kakao-login"; // 🔹 templates/auth/kakao-login.html 렌더링
    }

    
    // ✅ 카카오 로그인 콜백 (회원 확인 후 이동)
 // ✅ 카카오 로그인 후 기존 회원인지 확인하고 연동 가능하도록 설정
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

        Long kakaoId = (Long) kakaoUserInfo.get("kakaoId");

        // ✅ 기존 회원인지 확인
        Optional<UserEntity> existingUser = userRepository.findByKakaoId(kakaoId);
        if (existingUser.isPresent()) {
            session.setAttribute("user", existingUser.get());
            model.addAttribute("user", existingUser.get());
            return "redirect:/mypage";
        }

        // ✅ 일반 회원인지 확인하여 연동 페이지로 이동
        String email = (String) kakaoUserInfo.get("email");
        Optional<UserEntity> normalUser = userRepository.findByUsername(email);
        
        if (normalUser.isPresent()) {
            session.setAttribute("kakaoUserInfo", kakaoUserInfo);
            return "redirect:/auth/kakao/link"; // ✅ 연동 페이지로 이동
        } else {
            session.setAttribute("kakaoUserInfo", kakaoUserInfo);
            return "redirect:/auth/kakao/register"; // ✅ 신규 회원 가입 페이지로 이동
        }
    }

 // ✅ 카카오 계정 연동 페이지
    @GetMapping("/link")
    public String showKakaoLinkPage(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Map<String, Object> kakaoUserInfo = (Map<String, Object>) session.getAttribute("kakaoUserInfo");

        // ✅ 로그인한 사용자 없으면 로그인 페이지로 이동
        if (user == null) {
            return "redirect:/auth/login";
        }

        // ✅ 이미 카카오 계정과 연동된 경우
        if (user.getKakaoId() != null) {
            model.addAttribute("alreadyLinked", true);
            model.addAttribute("linkedKakaoId", user.getKakaoId());
            return "auth/kakao-link";
        }

        // ✅ 카카오 로그인 정보가 없는 경우
        if (kakaoUserInfo == null || !kakaoUserInfo.containsKey("kakaoId")) {
            return "redirect:/auth/kakao/login";
        }

        model.addAttribute("alreadyLinked", false);
        model.addAttribute("kakaoUserInfo", kakaoUserInfo);
        return "auth/kakao-link";
    }


    // ✅ 카카오 계정 연동 처리 (POST)
    @PostMapping("/link")
    public String linkKakaoAccount(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Map<String, Object> kakaoUserInfo = (Map<String, Object>) session.getAttribute("kakaoUserInfo");

        if (user == null || kakaoUserInfo == null) {
            return "redirect:/auth/kakao/login"; // ✅ 로그인 안 한 상태면 로그인 페이지로 이동
        }

        Long kakaoId = (Long) kakaoUserInfo.get("kakaoId");
        user.setKakaoId(kakaoId);
        userRepository.save(user);
        
        session.setAttribute("user", user);
        model.addAttribute("user", user);

        return "redirect:/mypage"; // ✅ 연동 후 마이페이지로 이동
    }

    // ✅ 카카오 계정 연동 해제
    @PostMapping("/unlink")
    public String unlinkKakao(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login"; // ✅ 로그인 안 한 상태면 로그인 페이지로 이동
        }

        user.setKakaoId(null); // ✅ 카카오 ID 제거
        userRepository.save(user);

        session.setAttribute("user", user);
        model.addAttribute("user", user);

        return "redirect:/mypage"; // ✅ 연동 해제 후 마이페이지로 이동
    }
 // ✅ 카카오 회원가입 페이지 (세션 확인 후 이동)
    @GetMapping("/register")
    public String kakaoRegisterPage(HttpSession session, Model model) {
        Map<String, Object> kakaoUserInfo = (Map<String, Object>) session.getAttribute("kakaoUserInfo");

        if (kakaoUserInfo == null || !kakaoUserInfo.containsKey("kakaoId")) {
            return "redirect:/auth/kakao/login"; // ✅ 세션 없으면 카카오 로그인 페이지로 이동
        }
        model.addAttribute("kakaoUserInfo", kakaoUserInfo);
        return "auth/kakao-register";
    }


 // ✅ 카카오 회원가입 처리 (POST 요청 지원)
    @PostMapping("/register")
    public String kakaoRegister(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String nickname,
                                HttpSession session, Model model) {

        Map<String, Object> kakaoUserInfo = (Map<String, Object>) session.getAttribute("kakaoUserInfo");
        if (kakaoUserInfo == null || !kakaoUserInfo.containsKey("kakaoId")) {
            return "redirect:/auth/kakao/login"; // ✅ 세션 없으면 로그인 페이지로 이동
        }
        Long kakaoId = (Long) kakaoUserInfo.get("kakaoId");

        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            return "auth/kakao-register";
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setNickname(nickname);
        newUser.setProfileImage((String) kakaoUserInfo.get("profileImage"));
        newUser.setKakaoId(kakaoId);

        userRepository.save(newUser);
        session.setAttribute("user", newUser);
        model.addAttribute("user", newUser);

        return "redirect:/mypage";
    }

    
 

   


}
