package com.smhrd.deulmaru.service;

import com.smhrd.deulmaru.entity.UserInterest;
import com.smhrd.deulmaru.repository.UserInterestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserInterestService {

    private final UserInterestRepository userInterestRepository;

    public UserInterestService(UserInterestRepository userInterestRepository) {
        this.userInterestRepository = userInterestRepository;
    }
    
    // ✅ 관심 등록 여부 확인
    public boolean existsByUserIdAndGrantId(String userId, String grantId) {
        return userInterestRepository.existsByUserIdAndGrantId(userId, grantId);
    }
    
    // ✅ 관심 지원금 등록
    @Transactional
    public String addInterest(String userId, String grantId, LocalDate applEdDt) {
        if (userInterestRepository.existsByUserIdAndGrantId(userId, grantId)) {
            return "이미 관심 등록된 지원금입니다.";
        }
        UserInterest interest = new UserInterest(null, userId, grantId, applEdDt, true, null);
        userInterestRepository.save(interest);
        return "관심 등록이 완료되었습니다.";
    }

    // ✅ 관심 지원금 목록 조회
    public List<UserInterest> getUserInterests(String userId) {
        return userInterestRepository.findByUserId(userId);
    }

    // ✅ 관심 지원금 삭제 (이제 interestIdx 기준)
    @Transactional
    public String removeInterest(Long interestIdx) {
        if (!userInterestRepository.existsById(interestIdx)) {
            return "해당 관심 등록 데이터가 존재하지 않습니다.";
        }
        userInterestRepository.deleteById(interestIdx);
        return "관심 지원금이 삭제되었습니다.";
    }
}
