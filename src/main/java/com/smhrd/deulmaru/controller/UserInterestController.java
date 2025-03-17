package com.smhrd.deulmaru.controller;

import java.time.LocalDate;
import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.service.UserInterestService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/interest")
public class UserInterestController {

    private final UserInterestService userInterestService;

    public UserInterestController(UserInterestService userInterestService) {
        this.userInterestService = userInterestService;
    }
    
 // ✅ 관심 등록 여부 확인 API
    @GetMapping("/check")
    public ResponseEntity<?> checkInterest(HttpSession session, @RequestParam String grantId) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");

        if (loggedInUser == null) {
            return ResponseEntity.badRequest().body("로그인이 필요합니다.");
        }

        boolean exists = userInterestService.existsByUserIdAndGrantId(loggedInUser.getUserId(), grantId);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    
    // ✅ 관심 등록 API (POST 요청만 허용)
    @PostMapping("/add")
    public ResponseEntity<String> addInterest(HttpSession session,
                                              @RequestParam String grantId,
                                              @RequestParam LocalDate applEdDt) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");

        if (loggedInUser == null) {
            return ResponseEntity.badRequest().body("로그인이 필요합니다.");
        }

        String userId = loggedInUser.getUserId();
        String result = userInterestService.addInterest(userId, grantId, applEdDt);
        return ResponseEntity.ok(result);
    }
    
    
    // ✅ 관심 등록 취소 API (DELETE 방식, 파라미터로 grantId 사용)
    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelInterest(HttpSession session,
                                                   @RequestParam String grantId) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");

        if (loggedInUser == null) {
            return ResponseEntity.badRequest().body("로그인이 필요합니다.");
        }

        String userId = loggedInUser.getUserId();
        String result = userInterestService.cancelInterest(userId, grantId);
        return ResponseEntity.ok(result);
    }
    
}



