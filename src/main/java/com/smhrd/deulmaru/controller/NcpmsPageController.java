package com.smhrd.deulmaru.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class NcpmsPageController {

    // ✅ 병해충 상세 페이지 (Thymeleaf 렌더링)
    @GetMapping("/disease-detail")
    public String diseaseDetail(@RequestParam(name = "sick_key", required = false) String sickKey, Model model) {
        model.addAttribute("sickKey", sickKey);
        return "ncpms/disease-detail";  // ✅ templates/ncpms/disease-detail.html 반환
    }

    // ✅ 병해충 상담 상세 페이지 추가 (오류 해결)
    @GetMapping("/consult-detail")
    public String consultDetail(@RequestParam(name = "consult_id", required = false) String consultId, Model model) {
        model.addAttribute("consultId", consultId);
        return "ncpms/consult-detail";  // ✅ templates/ncpms/consult-detail.html 반환
    }
}
