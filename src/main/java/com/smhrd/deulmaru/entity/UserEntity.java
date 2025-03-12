package com.smhrd.deulmaru.entity;

import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

	private String nickname;
	private String profileImage;
	
	@Column(unique = true, nullable = true)
	private Long kakaoId;
	
	public UserEntity(String username, String password, String nickname, Long kakaoId, String profileImage) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.kakaoId = kakaoId;
		this.profileImage = profileImage;
	}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public Long getKakaoId() {
		return kakaoId;
	}

	public void setKakaoId(Long kakaoId) {
		this.kakaoId = kakaoId;
	}


}
