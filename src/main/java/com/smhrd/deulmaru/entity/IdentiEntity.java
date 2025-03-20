package com.smhrd.deulmaru.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "identi")
@Data
public class IdentiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // (옵션) 사용자 ID : TB_USER와 연동할 수 있음
    private String userId;
    
    // 업로드된 이미지 파일 경로
    private String imagePath;
    
    // 판별된 병명
    private String diseaseName;
    
    // 작물명
    private String cropName;
    
    // 판별 시간
    private LocalDateTime identificationTime;
}
