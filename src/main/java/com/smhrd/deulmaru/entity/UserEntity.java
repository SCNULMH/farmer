package com.smhrd.deulmaru.entity;

import jakarta.persistence.*;

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
    private String profileImage;

    @Column(unique = true, nullable = true)
    private Long kakaoId;

    // ✅ Getter 메서드 직접 추가
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public Long getKakaoId() {
        return kakaoId;
    }

    // ✅ Setter 메서드 직접 추가
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setKakaoId(Long kakaoId) {
        this.kakaoId = kakaoId;
    }
}
