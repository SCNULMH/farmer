package com.smhrd.deulmaru.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/predict")
public class PredictController {

    @PostMapping  // 반드시 POST 요청만 받도록 설정
    public String predict(@RequestParam("file") MultipartFile file) {
        try {
            // 파일 저장 경로 설정
            String uploadDir = "uploads/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일 저장
            File uploadedFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(uploadedFile);

            // Python 스크립트 실행하여 예측 수행
            ProcessBuilder processBuilder = new ProcessBuilder("python", "predict.py", uploadedFile.getAbsolutePath());
            Process process = processBuilder.start();

            // 결과 읽기
            java.io.InputStream inputStream = process.getInputStream();
            java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
            String result = scanner.hasNext() ? scanner.next() : "예측 실패";
            scanner.close();

            return "{\"prediction\": \"" + result + "\"}"; // JSON 응답 반환
        } catch (IOException e) {
            return "{\"error\": \"파일 처리 중 오류 발생\"}";
        }
    }

    // GET 요청에 대한 예외 처리 (필요할 경우 추가)
    @GetMapping
    public String handleGetRequest() {
        return "{\"error\": \"GET 요청은 지원되지 않습니다. 반드시 POST 요청을 사용하세요.\"}";
    }
}
