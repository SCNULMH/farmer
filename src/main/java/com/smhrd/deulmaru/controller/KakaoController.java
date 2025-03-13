package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.config.KakaoConfig;
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
    
    @Autowired
    private KakaoConfig kakaoConfig;

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
    
    // ✅ 카카오 로그인 후 콜백 처리 (회원가입/연동 여부 확인 포함)
    @GetMapping("/callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpSession session, Model model) {
        String accessToken = kakaoAuthService.getKakaoAccessToken(code);
        if (accessToken == null) {
            return "redirect:/error";
        }
        session.setAttribute("kakaoAccessToken", accessToken);

        // 1) 카카오 사용자 정보 요청
        Map<String, Object> kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(accessToken);
        if (kakaoUserInfo == null || !kakaoUserInfo.containsKey("kakaoId")) {
            return "redirect:/error";
        }

        Long kakaoId = ((Number) kakaoUserInfo.get("kakaoId")).longValue();
        String email = (String) kakaoUserInfo.getOrDefault("email", "");
        String nickname = (String) kakaoUserInfo.getOrDefault("nickname", "카카오사용자");

        // 2) linking 플래그 확인 (일반 회원이 카카오 계정 연동을 시도한 경우)
        Boolean linking = (Boolean) session.getAttribute("linking");
        if (linking != null && linking) {
            // linking 모드 해제
            session.removeAttribute("linking");

            // 로그인된 사용자 가져오기
            UserEntity loggedUser = (UserEntity) session.getAttribute("user");
            if (loggedUser == null) {
                // 세션 만료 등으로 인해 user가 없으면 로그인 페이지로
                return "redirect:/auth/login";
            }
            // 이미 카카오 계정이 연동된 경우
            if (loggedUser.getKakaoId() != null) {
                return "redirect:/mypage";
            }
            // 카카오 ID만 등록 후 저장
            loggedUser.setKakaoId(kakaoId);
            userRepository.save(loggedUser);

            // 세션 업데이트
            session.setAttribute("user", loggedUser);
            model.addAttribute("user", loggedUser);

            // 연동 완료 후 마이페이지로 이동
            return "redirect:/mypage";
        }

        // ✅ 기존 로직: 신규 가입 or 자동 로그인/연동

        // 3) 카카오 ID가 이미 등록된 경우 → 자동 로그인
        Optional<UserEntity> existingUser = userRepository.findByKakaoId(kakaoId);
        if (existingUser.isPresent()) {
            session.setAttribute("user", existingUser.get());
            model.addAttribute("user", existingUser.get());
            return "redirect:/mypage";
        }

        // 4) 일반 회원가입한 이메일과 동일한 경우 → 자동 연동
        Optional<UserEntity> normalUser = userRepository.findByUserId(email);
        if (normalUser.isPresent()) {
            UserEntity user = normalUser.get();
            user.setKakaoId(kakaoId);
            userRepository.save(user);
            session.setAttribute("user", user);
            return "redirect:/mypage";
        }

        // 5) 새로운 회원이면 카카오 정보 세션에 저장 후 회원가입 페이지로 이동
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
            @RequestParam String userId,          // 사용자가 입력한 로그인용 아이디
            @RequestParam String userEmail,       // 사용자가 입력한 이메일
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

        // 중복 체크: userId(로그인용 아이디)가 이미 존재하면 오류 처리
        if (userRepository.findByUserId(userId).isPresent()) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            return "auth/kakao-register";
        }

        UserEntity newUser = new UserEntity();
        newUser.setUserId(userId);
        newUser.setUserPw(userPw);
        newUser.setUserNickname(userNickname);
        newUser.setUserEmail(userEmail);
        newUser.setKakaoId(kakaoId);
        newUser.setUserLocate(userLocate != null ? userLocate : "");

        if (userBirth != null && !userBirth.isEmpty()) {
            newUser.setUserBirth(LocalDate.parse(userBirth));
        } else {
            newUser.setUserBirth(LocalDate.of(2000, 1, 1)); // 기본 생년월일 설정
        }
        
        if (userGender != null && !userGender.isEmpty()) {
            newUser.setUserGender(UserEntity.Gender.valueOf(userGender.toUpperCase()));
        } else {
            newUser.setUserGender(UserEntity.Gender.M);
        }
        
        userRepository.save(newUser);
        session.setAttribute("user", newUser);
        model.addAttribute("user", newUser);

        return "redirect:/mypage";
    }


    // ★★ 카카오 연동 (일반 회원용) ★★
    
    // ✅ 카카오 연동 페이지 : 로그인한 사용자가 카카오 연동을 원할 때 → 바로 카카오 OAuth URL로 리다이렉트
    @GetMapping("/link")
    public String linkKakaoRedirect(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        // 이미 연동되었다면 마이페이지로
        if (user.getKakaoId() != null) {
            return "redirect:/mypage";
        }
        // 연동 의도를 세션에 저장 (콜백에서 linking 플래그 확인)
        session.setAttribute("linking", true);

        // 카카오 OAuth URL 구성
        String oauthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + kakaoConfig.getClientId()
                + "&redirect_uri=" + kakaoConfig.getRedirectUri()
                + "&response_type=code";

        // → 카카오 로그인 화면으로 리다이렉트
        return "redirect:" + oauthUrl;
    }

    // ✅ 카카오 연동 해제
    @PostMapping("/unlink")
    public String unlinkKakao(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        user.setKakaoId(null);
        userRepository.save(user);
        session.setAttribute("user", user);
        return "redirect:/mypage";
    }
}
