package com.smhrd.deulmaru.repository;

import com.smhrd.deulmaru.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    // ✅ 사용자 아이디(username)로 회원 검색
    Optional<UserEntity> findByUsername(String username);

    // ✅ 카카오 ID(kakao_id)로 회원 검색 (카카오 로그인 연동 확인용)
    Optional<UserEntity> findByKakaoId(Long kakaoId);
}
