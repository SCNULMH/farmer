package com.smhrd.deulmaru.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FileUploadController {

    @Value("${upload.path}") // application.properties에서 업로드 경로 설정
    private String uploadDir;

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "🚨 파일이 선택되지 않았습니다.");
            return response;
        }

        try {
            // 프로젝트 내 업로드 폴더 확인 후 생성
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // 업로드된 파일 저장
            String filename = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);
            Files.copy(file.getInputStream(), filePath);

            // Python 스크립트 실행 및 결과 반환
            String pythonScriptPath = "C:/Users/smhrd/git/farmer/predict.py";
            String diseasePrediction = predictImage(pythonScriptPath, filePath.toString());

            response.put("prediction", diseasePrediction);
            response.put("imageUrl", "/uploads/" + filename);

        } catch (IOException e) {
            response.put("message", "🚨 파일 업로드 오류: " + e.getMessage());
        }

        return response;
    }

    private String predictImage(String scriptPath, String imagePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "C:/Users/smhrd/AppData/Local/Programs/Python/Python310/python.exe",
                    scriptPath,
                    imagePath
            );

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            return output.toString().trim();

        } catch (IOException e) {
            return "예측 실패: " + e.getMessage();
        }
    }
}
