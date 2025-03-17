package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    private final UserRepository userRepository;

    public MypageController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ 마이페이지 이동
    @GetMapping("")
    public String myPage(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/deulmaru_Login";
        }
        model.addAttribute("user", user);
        return "/auth/deulmaru_Mypage";
    }

    // ✅ 회원정보 수정
    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam String userNickname,
                                @RequestParam(required = false) String userPw,
                                @RequestParam String userLocate,
                                HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        // 닉네임 변경
        user.setUserNickname(userNickname);
        
        // 주소지 변경
        user.setUserLocate(userLocate);
        
        // 비밀번호 변경이 입력되었을 경우에만 적용
        if (userPw != null && !userPw.isEmpty()) {
            user.setUserPw(userPw);
        }

        userRepository.save(user);
        session.setAttribute("user", user);
        return "redirect:/auth/mypage";
    }
}
