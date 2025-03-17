package com.smhrd.deulmaru.repository;

import com.smhrd.deulmaru.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {  // ✅ 단일 PK 사용

    List<UserInterest> findByUserId(String userId);  // ✅ 관심 목록 조회
    
    
    boolean existsByUserIdAndGrantId(String userId, String grantId); // ✅ 중복 체크
}
