package com.smhrd.deulmaru.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smhrd.deulmaru.dto.RecommendationDTO;
import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.service.RecommendationService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final HttpSession httpSession;

    public RecommendationController(RecommendationService recommendationService, HttpSession httpSession) {
        this.recommendationService = recommendationService;
        this.httpSession = httpSession;
    }

    // 로그인하지 않은 경우 전체 추천
    @GetMapping("/overall")
    public ResponseEntity<?> getOverallRecommendations() {
        List<RecommendationDTO> recommendations = recommendationService.getOverallTopRecommendations(3);
        return ResponseEntity.ok(recommendations);
    }

    // 로그인한 경우 개인 맞춤 추천
    @GetMapping("/personal")
    public ResponseEntity<?> getPersonalRecommendations() {
        UserEntity user = (UserEntity) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        int currentYear = LocalDate.now().getYear();
        int userAge = currentYear - user.getUserBirth().getYear();
        // 예를 들어, 같은 10년대 그룹으로 묶음 (20대: 20~29, 30대: 30~39 등)
        int minAge = (userAge / 10) * 10;
        int maxAge = minAge + 9;
        String gender = user.getUserGender().name(); // "M" 또는 "F"
        String locate = user.getUserLocate(); // 예: "전라남도" 등이 포함된 문자열
        List<RecommendationDTO> recommendations = recommendationService.getPersonalRecommendations(gender, minAge, maxAge, locate, 3);
        return ResponseEntity.ok(recommendations);
    }
}
