package com.smhrd.deulmaru.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/predict")
public class PredictController {

    @PostMapping
    public String predict( MultipartRequest request ) {
    	MultipartFile file = request.getFile("file");
    	
    	try {
            String uploadDir = "uploads/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File uploadedFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(uploadedFile);

            ProcessBuilder processBuilder = new ProcessBuilder("python", "predict.py", uploadedFile.getAbsolutePath());
            Process process = processBuilder.start();

            java.io.InputStream inputStream = process.getInputStream();
            java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
            String result = scanner.hasNext() ? scanner.next() : "예측 실패";
            scanner.close();

            return "{\"prediction\": \"" + result + "\"}";

        } catch (IOException e) {
            return "{\"error\": \"파일 처리 중 오류 발생: " + e.getMessage() + "\"}";
        }
    }

    @GetMapping
    public String handleGetRequest() {
        return "{\"error\": \"GET 요청은 지원되지 않습니다. 반드시 POST 요청을 사용하세요.\"}";
    }
}
