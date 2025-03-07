package com.smhrd.deulmaru.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "forward:/deulmaru_const.html";  // ✅ 정적 리소스 서빙
    }
}
