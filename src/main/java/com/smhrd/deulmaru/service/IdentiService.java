package com.smhrd.deulmaru.service;

import com.smhrd.deulmaru.entity.IdentiEntity;
import com.smhrd.deulmaru.repository.IdentiRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class IdentiService {

    private final IdentiRepository repository;

    @Value("${file.upload-dir:${user.home}/uploads}")	
    private String uploadDir;

    public IdentiService(IdentiRepository repository) {
        this.repository = repository;
    }

    // 파일 저장 메소드: 업로드 디렉토리 존재 확인 및 파일 중복 여부 체크
    public String storeImage(MultipartFile file, boolean overwrite) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("파일명이 존재하지 않습니다.");
        }
        
        Path filePath = uploadPath.resolve(originalFilename);
        if (Files.exists(filePath) && !overwrite) {
            throw new IOException("파일이 이미 존재합니다. 덮어쓰려면 overwrite 옵션을 true로 설정하세요.");
        }
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }

    // DB에 판별 결과 저장
    public IdentiEntity saveIdentiResult(String userId, String diseaseName, String cropName, double confidenceScore, String imagePath) {
        IdentiEntity entity = new IdentiEntity();
        entity.setUserId(userId);
        entity.setDiseaseName(diseaseName);
        entity.setCropName(cropName);
        entity.setConfidenceScore(confidenceScore);
        entity.setImagePath(imagePath);
        entity.setIdentificationTime(LocalDateTime.now());
        return repository.save(entity);
    }
    
    // 미리 정의된 병해충 관련 정보 조회 (추후 외부 연동 가능)
    public String getRelatedInfo(String diseaseName) {
        Map<String, String> infoMap = new HashMap<>();
        infoMap.put("병명1", "병명1 관련 상세 정보: 원인, 증상, 관리 방법 등.");
        infoMap.put("병명2", "병명2 관련 상세 정보: 발생 시기, 피해 규모, 대응 방법 등.");
        infoMap.put("병명3", "병명3 관련 상세 정보: 병해충의 특성, 예방 및 치료법 등.");
        return infoMap.getOrDefault(diseaseName, "해당 병해충 정보가 없습니다.");
    }
}
