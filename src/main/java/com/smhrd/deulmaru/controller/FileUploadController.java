package com.smhrd.deulmaru.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Value("${upload.path}")
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
            // 업로드 경로 확인 및 폴더 생성
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);  // 폴더가 없으면 생성
            }

            // 파일 저장
            String filename = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);
            Files.copy(file.getInputStream(), filePath);

            // 예측 결과 받아오기
            String diseasePrediction = predictImage(filePath.toString());

            // 결과 반환
            response.put("prediction", diseasePrediction);
            response.put("imageUrl", "/uploads/" + filename);  // 업로드된 이미지 URL

            // 서버에서 반환되는 응답 확인 (디버깅용)
            System.out.println("Prediction: " + diseasePrediction);
            System.out.println("Image URL: " + "/uploads/" + filename);

        } catch (IOException e) {
            response.put("message", "🚨 파일 업로드 오류: " + e.getMessage());
            return response;
        }

        return response;  // 결과를 JSON으로 반환
    }

    // Python 스크립트를 실행하여 예측 결과 받기
    private String predictImage(String imagePath) {
        try {
            // Python 경로와 predict.py 경로 설정
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "C:/Users/smhrd/AppData/Local/Programs/Python/Python310/python.exe",  // Python 경로
                    "D:/dataset/saved_models/predict.py",  // predict.py 경로
                    imagePath  // 업로드된 이미지 경로
            );

            // Process 실행
            Process process = processBuilder.start();

            // Python 실행 결과 확인
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // Python 오류 출력 확인
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.err.println("Python Error: " + errorLine);
                }
            }

            // Python 출력 로그 확인 (디버깅용)
            System.out.println("Python Script Output: " + output.toString());

            return output.toString().trim();  // 예측된 결과 반환
        } catch (IOException e) {
            e.printStackTrace();
            return "예측 실패";
        }
    }
}
