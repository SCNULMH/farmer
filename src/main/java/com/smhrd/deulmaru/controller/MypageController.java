package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.entity.UserEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    // ✅ 마이페이지 접근 시 로그인 여부 확인
    @GetMapping("")
    public String myPage(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login"; // 🔹 로그인 안 했으면 로그인 페이지로 이동
        }

        model.addAttribute("user", user);
        return "auth/mypage"; // ✅ templates/auth/mypage.html
    }
}
