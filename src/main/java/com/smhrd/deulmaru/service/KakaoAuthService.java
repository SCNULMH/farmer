package com.smhrd.deulmaru.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smhrd.deulmaru.config.KakaoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoAuthService {

    @Autowired
    private KakaoConfig kakaoConfig; // ✅ KakaoConfig에서 설정값 가져오기

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ 카카오 Access Token 발급
    public String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "grant_type=authorization_code"
                + "&client_id=" + kakaoConfig.getClientId()
                + "&redirect_uri=" + kakaoConfig.getRedirectUri()
                + "&code=" + code;

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(kakaoConfig.getTokenUrl(), HttpMethod.POST, entity, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ 카카오 사용자 정보 가져오기
    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(kakaoConfig.getUserInfoUrl(), HttpMethod.GET, entity, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("kakaoId", jsonNode.get("id").asLong());
            userInfo.put("nickname", jsonNode.path("properties").path("nickname").asText());
            userInfo.put("profileImage", jsonNode.path("properties").path("profile_image").asText());

            return userInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
