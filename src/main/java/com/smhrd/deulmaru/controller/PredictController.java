package com.smhrd.deulmaru.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@RestController
@RequestMapping("/predict")
public class PredictController {

    @Value("${upload.path}") // application.properties에서 업로드 경로 가져오기
    private String uploadDir;

    @PostMapping
    public Map<String, String> predict(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "🚨 업로드된 파일이 없습니다. 다시 시도하세요.");
            return response;
        }

        try {
            // ✅ 업로드 폴더 자동 생성
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // ✅ 파일 저장
            File uploadedFile = new File(directory, file.getOriginalFilename());
            file.transferTo(uploadedFile);

            // ✅ Python 실행
            ProcessBuilder processBuilder = new ProcessBuilder(
                "python", "predict.py", uploadedFile.getAbsolutePath()
            );
            processBuilder.environment().put("PYTHONIOENCODING", "UTF-8");

            Process process = processBuilder.start();

            // ✅ 실행 결과 확인
            Scanner scanner = new Scanner(process.getInputStream(), "UTF-8").useDelimiter("\\A");
            String result = scanner.hasNext() ? scanner.next().trim() : "예측 실패";
            scanner.close();

            response.put("prediction", result);
            return response;

        } catch (IOException e) {
            response.put("error", "파일 처리 중 오류 발생: " + e.getMessage());
            return response;
        }
    }
}
