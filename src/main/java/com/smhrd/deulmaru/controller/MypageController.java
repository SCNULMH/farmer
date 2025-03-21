package com.smhrd.deulmaru.controller;

import com.smhrd.deulmaru.entity.IdentiEntity;
import com.smhrd.deulmaru.entity.UserEntity;
import com.smhrd.deulmaru.repository.UserRepository;
import com.smhrd.deulmaru.service.IdentiService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mypage")
public class MypageController {

	@Autowired 
	UserRepository userRepository;
	
	@Autowired
	IdentiService identiService;

    @Autowired
    public MypageController(UserRepository userRepository, IdentiService identiService) {
        this.userRepository = userRepository;
        this.identiService = identiService;
    }


    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam String userNickname,
                                 @RequestParam(required = false) String userPw,
                                 @RequestParam String userLocate,
                                 @RequestParam(required = false) String userCrop,
                                 HttpSession session) {

        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        user.setUserNickname(userNickname);
        user.setUserLocate(userLocate);

        if (userCrop != null && !userCrop.isEmpty()) {
            user.setUserCrop(userCrop);
        }

        if (userPw != null && !userPw.isEmpty()) {
            user.setUserPw(userPw);
        }

        userRepository.save(user);
        session.setAttribute("user", user);
        return "redirect:/mypage";
    }
}
