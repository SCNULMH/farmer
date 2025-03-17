package com.smhrd.deulmaru.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller  // ✅ @RestController → @Controller로 변경
@RequestMapping("/supportApi")  
public class SupportController {

    @GetMapping("/support")
    public String grantsPage() {
        return "supportApi/support";  // ✅ templates/support.html 반환
    }
    
    @GetMapping("/detail/{seq}")
    public String grantDetailPage(@PathVariable String seq, Model model) {
        model.addAttribute("seq", seq);
        return "supportApi/support-detail";  // ✅ templates/support-detail.html 반환
    }
}
