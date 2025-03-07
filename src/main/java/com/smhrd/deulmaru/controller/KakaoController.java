package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.repository.UserRepository;
import com.smhrd.deulmaru.service.KakaoAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ 카카오 로그인 처리
    @GetMapping("/callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpSession session) {
        // 1️⃣ 카카오 Access Token 발급
        String accessToken = kakaoAuthService.getKakaoAccessToken(code);
        if (accessToken == null) {
            return "redirect:/error";
        }
        session.setAttribute("kakaoAccessToken", accessToken);

        // 2️⃣ 카카오 사용자 정보 가져오기
        Map<String, Object> kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(accessToken);
        Long kakaoId = (Long) kakaoUserInfo.get("kakaoId");

        // 3️⃣ 기존 회원 확인 (이미 가입된 경우 로그인 처리)
        Optional<UserEntity> existingUser = userRepository.findByKakaoId(kakaoId);
        if (existingUser.isPresent()) {
            session.setAttribute("user", existingUser.get());
            return "redirect:/mypage";
        } else {
            // 4️⃣ 신규 회원이면 추가 정보 입력 페이지로 이동
            session.setAttribute("kakaoUserInfo", kakaoUserInfo);
            return "redirect:/kakao-register";
        }
    }

    // ✅ 카카오 회원가입 (추가 정보 입력 후 저장)
    @PostMapping("/register")
    public String kakaoRegister(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String nickname,
                                HttpSession session) {

        // 1️⃣ 세션에서 카카오 정보 가져오기
        Map<String, Object> kakaoUserInfo = (Map<String, Object>) session.getAttribute("kakaoUserInfo");
        if (kakaoUserInfo == null) {
            return "redirect:/";
        }
        Long kakaoId = (Long) kakaoUserInfo.get("kakaoId");

        // 2️⃣ 아이디 중복 체크
        if (userRepository.findByUsername(username).isPresent()) {
            return "redirect:/kakao-register?error=username_taken";
        }

        // 3️⃣ 새 사용자 저장
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setNickname(nickname);
        newUser.setProfileImage((String) kakaoUserInfo.get("profileImage"));
        newUser.setKakaoId(kakaoId);

        userRepository.save(newUser);
        session.setAttribute("user", newUser);

        return "redirect:/mypage";
    }

    // ✅ 카카오 계정 연동 해제
    @PostMapping("/unlink")
    public String unlinkKakao(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 카카오 계정 연동 해제
        user.setKakaoId(null);
        userRepository.save(user);
        session.setAttribute("user", user);

        return "redirect:/mypage";
    }
}
