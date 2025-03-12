package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.service.NcpmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ncpms")
@CrossOrigin(origins = "*") // CORS 허용
public class NcpmsController {

    private final NcpmsService ncpmsService;

    public NcpmsController(NcpmsService ncpmsService) {
        this.ncpmsService = ncpmsService;
    }
    
    // 페이지 이동
    @GetMapping("/disease-detail")
    public String diseaseDetail(@RequestParam(name = "sick_key", required = false) String sickKey, Model model) {
        model.addAttribute("sickKey", sickKey);
        return "/disease-detail";  // ✅ templates/ncpms/disease-detail.html 반환
    }

    // ✅ 병해충 상담 상세 페이지 추가 (오류 해결)
    @GetMapping("/consult-detail")
    public String consultDetail(@RequestParam(name = "consult_id", required = false) String consultId, Model model) {
        model.addAttribute("consultId", consultId);
        return "/consult-detail";  // ✅ templates/ncpms/consult-detail.html 반환
    }

    
    @GetMapping("/deulmaru_dictionary")
    public String deulmaru_dictionary() {
        return "/deulmaru_dictionary"; // ✅ templates/auth/kakao-register.html을 찾도록 변경
    }
    
    @GetMapping("/deulmaru_QnA")
    public String deulmaru_QnA() {
        return "/deulmaru_QnA"; // ✅ templates/auth/kakao-register.html을 찾도록 변경
    }
    
    @GetMapping("/deulmaru_Diagnosis")
    public String deulmaru_Diagnosis() {
        return "/deulmaru_Diagnosis"; // ✅ templates/auth/kakao-register.html을 찾도록 변경
    }
    
    @GetMapping(value = "/search", produces = "application/xml")
    public ResponseEntity<String> search(@RequestParam String query, @RequestParam(defaultValue = "sick") String type) {
        return ResponseEntity.ok(ncpmsService.search(query, type));
    }

    @GetMapping(value = "/sick_detail", produces = "application/xml")
    public ResponseEntity<String> sickDetail(@RequestParam String sick_key) {
        return ResponseEntity.ok(ncpmsService.getSickDetail(sick_key));
    }

    @GetMapping("/consult")
    public ResponseEntity<String> consult(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(ncpmsService.getConsult(query, page));
    }

    @GetMapping("/consult_detail")
    public ResponseEntity<String> consultDetail(@RequestParam String consult_id) {
        return ResponseEntity.ok(ncpmsService.getConsultDetail(consult_id));
    }
}