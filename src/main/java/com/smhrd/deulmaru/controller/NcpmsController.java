package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.service.NcpmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // CORS 허용
public class NcpmsController {

    private final NcpmsService ncpmsService;

    public NcpmsController(NcpmsService ncpmsService) {
        this.ncpmsService = ncpmsService;
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