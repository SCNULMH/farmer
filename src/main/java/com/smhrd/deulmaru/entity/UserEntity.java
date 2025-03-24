package com.smhrd.deulmaru.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_USER")
public class UserEntity {

    @Id
    @Column(name = "USER_ID", length = 50, nullable = false)
    private String userId;

    @Column(name = "USER_PW", length = 255, nullable = false)
    private String userPw;

    @Column(name = "USER_EMAIL", length = 255, nullable = false)
    private String userEmail;

    @Column(name = "USER_NICKNAME", length = 255, nullable = false)
    private String userNickname;

    @Column(name = "USER_BIRTH", nullable = true) //  null 허용 (카카오 회원가입 시 선택 가능)
    private LocalDate userBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_GENDER", nullable = true) //  null 허용
    private Gender userGender;

    @Column(name = "USER_LOCATE", length = 255, nullable = true) //  null 허용 (카카오 회원가입 시 선택 가능)
    private String userLocate;

    @Column(name = "KAKAO_ID", nullable = true, unique = true)
    private Long kakaoId;

    @Column(name = "USER_CROP", length = 255, nullable = true) //  null 허용
    private String userCrop;

    @Column(name = "JOINED_AT", nullable = false, updatable = false, insertable = false)
    private LocalDateTime joinedAt;

    // 기본 생성자 (JPA용)
    public UserEntity() {}

    // 카카오 회원가입용 생성자 (추가 필드 포함)
    public UserEntity(String userId, String userPw, String userEmail, String userNickname,
                      LocalDate userBirth, Gender userGender, String userLocate, 
                      Long kakaoId, String userCrop) {
        this.userId = userId;
        this.userPw = userPw;
        this.userEmail = userEmail;
        this.userNickname = userNickname;
        this.userBirth = userBirth;
        this.userGender = userGender;
        this.userLocate = userLocate;
        this.kakaoId = kakaoId;
        this.userCrop = userCrop;
    }

    //  Getter & Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserPw() { return userPw; }
    public void setUserPw(String userPw) { this.userPw = userPw; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }

    public LocalDate getUserBirth() { return userBirth; }
    public void setUserBirth(LocalDate userBirth) { this.userBirth = userBirth; }

    public Gender getUserGender() { return userGender; }
    public void setUserGender(Gender userGender) { this.userGender = userGender; }

    public String getUserLocate() { return userLocate; }
    public void setUserLocate(String userLocate) { this.userLocate = userLocate; }

    public Long getKakaoId() { return kakaoId; }
    public void setKakaoId(Long kakaoId) { this.kakaoId = kakaoId; }

    public String getUserCrop() { return userCrop; }
    public void setUserCrop(String userCrop) { this.userCrop = userCrop; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    //  성별 ENUM 클래스 정의
    public enum Gender {
        M, F
    }
}
