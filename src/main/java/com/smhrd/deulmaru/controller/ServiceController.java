package com.smhrd.deulmaru.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServiceController {

    @GetMapping("/service/deulmaru_const")
    public String deulmaruConstPage() {
        return "service/deulmaru_const";
    }
}
