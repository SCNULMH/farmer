package com.smhrd.deulmaru.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smhrd.deulmaru.dto.RecommendationDTO;
import com.smhrd.deulmaru.repository.UserInterestRepository;

@Service
public class RecommendationService {

    private final UserInterestRepository userInterestRepository;

    public RecommendationService(UserInterestRepository userInterestRepository) {
        this.userInterestRepository = userInterestRepository;
    }

    // 전체 Top 추천 (Service 계층에서 결과 개수를 자름)
    public List<RecommendationDTO> getOverallTopRecommendations(int limit) {
        List<Object[]> results = userInterestRepository.findOverallTopRecommendationsWithoutLimit();
        if (results.size() > limit) {
            results = results.subList(0, limit);
        }
        List<RecommendationDTO> recommendations = new ArrayList<>();
        for (Object[] row : results) {
            RecommendationDTO dto = new RecommendationDTO();
            dto.setGrantId(String.valueOf(row[0]));
            dto.setInterestCount(((Number) row[1]).longValue());
            recommendations.add(dto);
        }
        return recommendations;
    }

    // 개인 맞춤 추천 (Service 계층에서 결과 개수를 자름)
    public List<RecommendationDTO> getPersonalRecommendations(String gender, int minAge, int maxAge, String locate, int limit) {
        List<Object[]> results = userInterestRepository.findPersonalTopRecommendationsWithoutLimit(gender, minAge, maxAge, locate);
        if (results.size() > limit) {
            results = results.subList(0, limit);
        }
        List<RecommendationDTO> recommendations = new ArrayList<>();
        for (Object[] row : results) {
            RecommendationDTO dto = new RecommendationDTO();
            dto.setGrantId(String.valueOf(row[0]));
            dto.setInterestCount(((Number) row[1]).longValue());
            recommendations.add(dto);
        }
        return recommendations;
    }
}
