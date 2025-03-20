package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.entity.IdentiEntity;
import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.service.IdentiService;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ident")
public class IdentiController {

    private final IdentiService identiService;

    public IdentiController(IdentiService identiService) {
        this.identiService = identiService;
    }

    /**
     * 판별 결과 저장 API
     * 요청 파라미터:
     * - diseaseName (필수): 판별된 병명
     * - cropName (필수): 작물명
     * - file (필수): 업로드한 이미지 파일
     * - overwrite (옵션, 기본값 false): 파일 덮어쓰기 여부
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveIdenti(
            HttpSession session,
            @RequestParam("diseaseName") String diseaseName,
            @RequestParam("confidenceScore") double confidenceScore,
            @RequestParam("cropName") String cropName,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "overwrite", defaultValue = "true") boolean overwrite
    ) {
        try {
            // 🔍 세션에서 로그인된 사용자 정보 가져오기
            UserEntity user = (UserEntity) session.getAttribute("user");
            if (user == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "로그인 정보가 없습니다. 다시 로그인해주세요.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String userId = user.getUserId();
            
            // 🔍 diseaseName에서 "예측 결과:" 이후의 텍스트만 추출
            if (diseaseName.contains("병해충 진단 결과:")) {
                diseaseName = diseaseName.substring(diseaseName.indexOf("병해충 진단 결과:") + 11).trim();
            }
            
            // 🔍 서버에서 받은 요청 데이터 출력
            System.out.println("📥 받은 요청 데이터:");
            System.out.println("   🔹 userId: " + userId);
            System.out.println("   🔹 cropName: " + cropName);
            System.out.println("   🔹 diseaseName: " + diseaseName);
            System.out.println("   🔹 confidenceScore: " + confidenceScore);
            System.out.println("   🔹 file: " + (file != null ? file.getOriginalFilename() : "파일 없음"));

            // 파일 저장 처리
            String imagePath = identiService.storeImage(file, overwrite);

            // DB에 판별 결과 저장
            IdentiEntity entity = identiService.saveIdentiResult(userId, diseaseName, cropName, confidenceScore, imagePath);

            // 관련 정보 조회
            String relatedInfo = identiService.getRelatedInfo(diseaseName);

            // 응답 반환 (JSON 형태)
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "진단 이력이 저장되었습니다.");
            successResponse.put("data", new IdentiResponse(entity, relatedInfo));

            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            System.out.println("❌ 저장 실패: " + e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "진단 이력 저장 실패: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // 응답 DTO 클래스
    public static class IdentiResponse {
        private IdentiEntity identiEntity;
        private String relatedInfo;

        public IdentiResponse(IdentiEntity identiEntity, String relatedInfo) {
            this.identiEntity = identiEntity;
            this.relatedInfo = relatedInfo;
        }

        public IdentiEntity getIdentiEntity() {
            return identiEntity;
        }

        public void setIdentiEntity(IdentiEntity identiEntity) {
            this.identiEntity = identiEntity;
        }

        public String getRelatedInfo() {
            return relatedInfo;
        }

        public void setRelatedInfo(String relatedInfo) {
            this.relatedInfo = relatedInfo;
        }
    }
}
