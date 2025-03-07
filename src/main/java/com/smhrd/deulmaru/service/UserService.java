package com.smhrd.deulmaru.service;

import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입
    public UserEntity registerUser(String username, String password, String nickname) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        return userRepository.save(user);
    }

    // 로그인 검증
    public Optional<UserEntity> loginUser(String username, String password) {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }

    // 사용자 정보 조회
    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // 프로필 업데이트 (비밀번호 & 닉네임 변경)
    public void updateUserProfile(Long userId, String password, String nickname) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (!password.isEmpty()) {
                user.setPassword(password);
            }
            user.setNickname(nickname);
            userRepository.save(user);
        }
    }
}
