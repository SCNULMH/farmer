package com.smhrd.deulmaru.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nickname;

    private String profileImage; // 프로필 이미지 저장

    @Column(unique = true)  // ✅ `kakao_id`를 DB에 저장할 경우 UNIQUE 설정
    private String kakaoId; // ✅ 기존 Long → String 변경

}
