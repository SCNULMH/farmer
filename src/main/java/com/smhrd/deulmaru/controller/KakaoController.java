package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.repository.UserRepository;
import com.smhrd.deulmaru.service.KakaoAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
        String email = (String) kakaoUserInfo.getOrDefault("email", "");
        String nickname = (String) kakaoUserInfo.getOrDefault("nickname", "카카오사용자");

        // ✅ 1. 카카오 ID가 이미 등록된 경우 -> 자동 로그인
        Optional<UserEntity> existingUser = userRepository.findByKakaoId(kakaoId);
        if (existingUser.isPresent()) {
            session.setAttribute("user", existingUser.get());
            model.addAttribute("user", existingUser.get());
            return "redirect:/mypage";
        }

        // ✅ 2. 일반 회원가입한 이메일과 동일한 경우 -> 자동 연동
        Optional<UserEntity> normalUser = userRepository.findByUserId(email);
        if (normalUser.isPresent()) {
            UserEntity user = normalUser.get();
            user.setKakaoId(kakaoId);
            userRepository.save(user);

            session.setAttribute("user", user);
            return "redirect:/mypage";
        }

        // ✅ 3. 새로운 회원이면 카카오 정보 세션에 저장 후 회원가입 페이지로 이동
        session.setAttribute("kakaoUserInfo", kakaoUserInfo);
        return "auth/kakao-register";
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
    public String kakaoRegister(
            @RequestParam String userId,
            @RequestParam String userPw,
            @RequestParam String userNickname,
            @RequestParam(required = false) String userLocate,
            @RequestParam(required = false) String userBirth,
            @RequestParam(required = false) String userGender,
            HttpSession session, Model model) {

        Map<String, Object> kakaoUserInfo = (Map<String, Object>) session.getAttribute("kakaoUserInfo");
        if (kakaoUserInfo == null || !kakaoUserInfo.containsKey("kakaoId")) {
            return "redirect:/auth/kakao/login";
        }

        Long kakaoId = ((Number) kakaoUserInfo.get("kakaoId")).longValue();
        String email = (String) kakaoUserInfo.getOrDefault("email", "");

        if (userRepository.findByUserId(userId).isPresent()) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            return "auth/kakao-register";
        }

        // ✅ 회원 정보 생성
        UserEntity newUser = new UserEntity();
        newUser.setUserId(userId);
        newUser.setUserPw(userPw);
        newUser.setUserNickname(userNickname);
        newUser.setUserEmail(email);
        newUser.setKakaoId(kakaoId);
        newUser.setUserLocate(userLocate != null ? userLocate : "");

        // ✅ 생년월일 변환 (YYYY-MM-DD 형식)
        if (userBirth != null && !userBirth.isEmpty()) {
            newUser.setUserBirth(LocalDate.parse(userBirth));
        } else {
            newUser.setUserBirth(LocalDate.of(2000, 1, 1)); // 기본 생년월일 설정
        }

        // ✅ 성별 변환
        if (userGender != null) {
            newUser.setUserGender(UserEntity.Gender.valueOf(userGender.toUpperCase()));
        } else {
            newUser.setUserGender(UserEntity.Gender.M);
        }

        // ✅ DB 저장 및 세션 반영
        userRepository.save(newUser);
        session.setAttribute("user", newUser);
        model.addAttribute("user", newUser);

        return "redirect:/mypage";
    }
}
