package com.smhrd.deulmaru.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NcpmsService {

    private final String BASE_URL = "http://ncpms.rda.go.kr/npmsAPI/service";
    private final String API_KEY = "202527b8644da53e300b8e2584ea2839576d";

    private final RestTemplate restTemplate;

    public NcpmsService() {
        this.restTemplate = new RestTemplate();
    }

    // ✅ 병해충 검색 요청
    public Map<String, Object> searchDisease(String query, String type) {
        String url = BASE_URL + "/pestList?apiKey=" + API_KEY + "&searchWord=" + query + "&searchType=" + type;
        return restTemplate.getForObject(url, HashMap.class);
    }

    // ✅ 병해충 상세보기 요청
    public String getDiseaseDetail(String sickKey) {
        String url = BASE_URL + "/pestDetail?apiKey=" + API_KEY + "&sickKey=" + sickKey;
        return restTemplate.getForObject(url, String.class);
    }
}
