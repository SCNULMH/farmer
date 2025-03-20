package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.entity.IdentiEntity;
import com.smhrd.deulmaru.service.IdentiService;
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
     * - userId (선택): 사용자 ID
     * - diseaseName (필수): 판별된 병명
     * - cropName (필수): 작물명
     * - file (필수): 업로드한 이미지 파일
     * - overwrite (옵션, 기본값 false): 파일 덮어쓰기 여부
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveIdenti(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam("diseaseName") String diseaseName,
            @RequestParam("cropName") String cropName,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "overwrite", defaultValue = "false") boolean overwrite
    ) {
        try {
            // 파일 저장 처리
            String imagePath = identiService.storeImage(file, overwrite);
            // DB에 판별 결과 저장
            IdentiEntity entity = identiService.saveIdentiResult(userId, diseaseName, cropName, imagePath);
            // 관련 정보 조회 (예시)
            String relatedInfo = identiService.getRelatedInfo(diseaseName);
            // 응답 DTO 생성 후 반환
            return ResponseEntity.ok().body(new IdentiResponse(entity, relatedInfo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
