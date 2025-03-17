package com.smhrd.deulmaru.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smhrd.deulmaru.service.SupportService;

@RestController
@RequestMapping("/support")  
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @GetMapping("/list")
    public String grantsPage() {
        return "support/support";
    }
    
    @GetMapping("/detail/{seq}")
    public String grantDetailPage(@PathVariable String seq, Model model) {
        model.addAttribute("seq", seq);
        return "support/support-detail";
    }
    
    
    
    
    
}
