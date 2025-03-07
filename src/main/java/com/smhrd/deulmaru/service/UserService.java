package com.smhrd.deulmaru.service;

import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ 회원가입 (세션 설정 추가)
    public UserEntity registerUser(String username, String password, String nickname, HttpSession session) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        userRepository.save(user);

        session.setAttribute("user", user); // ✅ 세션에 user 정보 저장
        return user;
    }

    // ✅ 로그인 (세션 설정 추가)
    public Optional<UserEntity> loginUser(String username, String password, HttpSession session) {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            session.setAttribute("user", user.get()); // ✅ 세션에 user 정보 저장
            return user;
        }
        return Optional.empty();
    }
   // ✅ 프로필 업데이트 (비밀번호 암호화 제거)
    public void updateUserProfile(Long userId, String password, String nickname) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (!password.isEmpty()) {
                user.setPassword(password);  // 🔹 일반 텍스트 저장
            }
            user.setNickname(nickname);
            userRepository.save(user);
        }
    }

    
 // ✅ 카카오 회원가입 처리 메서드 추가
    public UserEntity registerKakaoUser(String kakaoId, String nickname, HttpSession session) {
        Optional<UserEntity> existingUser = userRepository.findByKakaoId(Long.parseLong(kakaoId));

        if (existingUser.isPresent()) {
            throw new RuntimeException("이미 가입된 카카오 계정입니다.");
        }

        UserEntity user = new UserEntity();
        user.setKakaoId(Long.parseLong(kakaoId));
        user.setNickname(nickname);
        userRepository.save(user);

        session.setAttribute("user", user); // ✅ 세션 저장
        return user;
    }


}
