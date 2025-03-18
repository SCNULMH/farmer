package com.smhrd.deulmaru.repository;

import com.smhrd.deulmaru.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    // 관심 목록 조회
    List<UserInterest> findByUserId(String userId);

    // 중복 체크
    boolean existsByUserIdAndGrantId(String userId, String grantId);

    // 관심 등록 삭제
    void deleteByUserIdAndGrantId(String userId, String grantId);

    // 전체 Top 추천 쿼리 (LIMIT 구문 제거)
    @Query(value =
        "SELECT GRANT_ID, COUNT(*) AS interestCount " +
        "FROM TB_USER_INTEREST " +
        "GROUP BY GRANT_ID " +
        "ORDER BY interestCount DESC",
        nativeQuery = true)
    List<Object[]> findOverallTopRecommendationsWithoutLimit();

    // 개인 맞춤 추천 쿼리 (LIMIT 구문 제거)
    @Query(value =
        "SELECT i.GRANT_ID, COUNT(*) AS interestCount " +
        "FROM TB_USER_INTEREST i " +
        "JOIN TB_USER u ON i.USER_ID = u.USER_ID " +
        "WHERE u.USER_GENDER = ?1 " +
        "  AND YEAR(CURDATE()) - YEAR(u.USER_BIRTH) BETWEEN ?2 AND ?3 " +
        "  AND u.USER_LOCATE LIKE CONCAT('%', ?4, '%') " +
        "GROUP BY i.GRANT_ID " +
        "ORDER BY interestCount DESC",
        nativeQuery = true)
    List<Object[]> findPersonalTopRecommendationsWithoutLimit(
        String gender,   // ?1
        int minAge,      // ?2
        int maxAge,      // ?3
        String locate    // ?4
    );
}
