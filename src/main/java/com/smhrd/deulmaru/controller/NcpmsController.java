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
    public Map<String, Object> searchDisease(@RequestParam String query, @RequestParam String type) {
        return ncpmsService.searchDisease(query, type);
    }

    // ✅ 병해충 상세보기 API
    @GetMapping("/sick_detail")
    public String getDiseaseDetail(@RequestParam String sick_key) {
        return ncpmsService.getDiseaseDetail(sick_key);
    }
}
