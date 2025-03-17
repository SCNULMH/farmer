package com.smhrd.deulmaru.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller  // ✅ @RestController → @Controller 변경
@RequestMapping("/support")  
public class SupportController {

    @GetMapping("/list")
    public String grantsPage() {
        return "supportApi/support";
    }
    
    @GetMapping("/detail/{seq}")
    public String grantDetailPage(@PathVariable String seq, Model model) {
        model.addAttribute("seq", seq);
        return "supportApi/support-detail";
    }
}
