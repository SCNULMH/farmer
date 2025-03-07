package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.service.NcpmsService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NcpmsController {
    private final NcpmsService ncpmsService;

    public NcpmsController(NcpmsService ncpmsService) {
        this.ncpmsService = ncpmsService;
    }

    // ✅ 병해충 검색 API
    @GetMapping("/search")
    public Map<String, Object> searchDisease(@RequestParam("query") String query, @RequestParam("type") String type) {
        return ncpmsService.searchDisease(query, type);
    }

    // ✅ 병해충 상세정보 API
    @GetMapping("/sick_detail")
    public Map<String, Object> getDiseaseDetail(@RequestParam("sick_key") String sickKey) {
        return ncpmsService.getDiseaseDetail(sickKey);
    }

    // ✅ 병해충 상담 검색 API
    @GetMapping("/consult")
    public Map<String, Object> searchConsult(@RequestParam("query") String query, @RequestParam("page") int page) {
        return ncpmsService.searchConsult(query, page);
    }

    // ✅ 병해충 상담 상세보기 API
    @GetMapping("/consult_detail")
    public Map<String, Object> getConsultDetail(@RequestParam("consult_id") String consultId) {
        return ncpmsService.getConsultDetail(consultId);
    }
}
