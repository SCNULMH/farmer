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

    @Value("${upload.path}")
    private String uploadDir;

    @Value("${predict.script.path}")
    private String pythonScriptPath;

    @Value("${python.interpreter.path}")
    private String pythonInterpreterPath;

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "🚨 파일이 선택되지 않았습니다.");
            return response;
        }

        try {
            // uploadDir가 절대경로가 아니라면 현재 사용자 홈 디렉터리를 기준으로 변환
            Path path = Paths.get(uploadDir);
            if (!path.isAbsolute()) {
                path = Paths.get(System.getProperty("user.dir"), uploadDir);
            }
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // 업로드된 파일 저장
            String filename = file.getOriginalFilename();
            Path filePath = path.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // Python 스크립트 실행 및 결과 반환
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
                    pythonInterpreterPath,
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
