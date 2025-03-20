let uploadedFile = null; // 업로드된 파일 저장
let diagnosisStarted = false; // 진단 진행 여부
let predictionResult = ""; // 예측된 병 코드 및 이름 저장

// 파일 업로드 이벤트 핸들러
function handleFileUpload(files) {
    const resultText = document.getElementById("result-text");
    const diagnosisButton = document.getElementById("diagnosis-button");

    if (files.length === 0) {
        return;
    }

    const file = files[0]; // 하나의 파일만 처리
    const maxFileSize = 10 * 1024 * 1024; // 10MB 제한

    if (file.size > maxFileSize) {
        resultText.textContent = "🚨 파일 크기가 너무 큽니다. 10MB 이하로 업로드 해주세요.";
        return;
    }

    // 중복 업로드 방지
    if (uploadedFile && uploadedFile.name === file.name && uploadedFile.size === file.size) {
        resultText.textContent = "🚨 이미 업로드된 파일입니다.";
        return;
    }

    uploadedFile = file;
    resultText.textContent = "📂 파일이 업로드되었습니다. 작물명을 입력하고 '진단하기' 버튼을 눌러주세요.";
    diagnosisButton.disabled = false;

    // 파일 이름 표시
    updateFileList(file);

    // 업로드한 이미지의 썸네일로 대체
    const reader = new FileReader();
    reader.onload = function(e) {
        const dropFile = document.getElementById("drop-file");
        const imageElement = dropFile.querySelector("img");
        imageElement.src = e.target.result;
        const messageElement = dropFile.querySelector(".message");
        if (messageElement) {
            messageElement.style.display = "none";
        }
    };
    reader.readAsDataURL(file);
}

// 파일 목록 업데이트 함수
function updateFileList(file) {
    const fileList = document.getElementById("file-list");
    fileList.innerHTML = `<div class="file-item">📂 ${file.name}</div>`;
}

// 진단 버튼 클릭 시 실행될 함수
function startDiagnosis() {
    if (!uploadedFile) {
        alert("❌ 파일을 먼저 업로드하세요!");
        return;
    }

    const cropName = document.getElementById("cropNameInput").value.trim();
    if (!cropName) {
        alert("❌ 작물명을 입력하세요.");
        return;
    }

    const resultText = document.getElementById("result-text");
    const formData = new FormData();
    formData.append("file", uploadedFile);
    formData.append("cropName", cropName);

    resultText.textContent = "🔍 분석 중... 잠시만 기다려주세요.";

    // '/upload' 엔드포인트로 POST 요청 (FileUploadController에 대응)
    fetch('/upload', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`🚨 서버 오류: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.prediction) {
            // 예시: 예측 결과 문자열이 "병명1" 또는 "정상" 등으로 반환
            predictionResult = data.prediction;
            resultText.textContent = `🦠 병해충 진단 결과: ${predictionResult}`;
            
            // 업로드한 이미지 표시
            const resultImage = document.getElementById("result-image");
            resultImage.src = data.imageUrl;
            resultImage.style.display = "block";
            
            // 진단 이력 저장 영역 표시
            document.getElementById("save-history-container").style.display = "block";
        } else if (data.message) {
            resultText.textContent = `❌ ${data.message}`;
        } else {
            resultText.textContent = "❌ 예측 실패: 서버에서 데이터를 받지 못했습니다.";
        }
    })
    .catch(error => {
        resultText.textContent = "❌ 진단 실패! 다시 시도해주세요.";
        console.error('Error:', error);
    });
}

// 진단 이력 저장 버튼 클릭 시 실행될 함수 (추후 추가 구현)
function saveDiagnosisHistory() {
    const userId = "test_user";  // 실제 로그인한 사용자 ID로 변경 필요
    const cropName = document.getElementById("cropNameInput").value.trim();
    
    // 기존 전체 진단 결과 텍스트
    const fullResultText = document.getElementById("result-text").innerText;
    const keyword = "예측 결과:";
    let diseaseName = "";
    
    // "예측 결과:" 이후의 텍스트만 추출
    const idx = fullResultText.indexOf(keyword);
    if (idx !== -1) {
        diseaseName = fullResultText.substring(idx + keyword.length).trim();
    } else {
        diseaseName = fullResultText.trim();
    }
    
    const fileInput = document.getElementById("chooseFile"); // 파일 선택 input

    if (!cropName || !diseaseName || fileInput.files.length === 0) {
        alert("❌ 모든 정보를 입력하세요. (작물명, 진단 결과, 이미지)");
        return;
    }

    const formData = new FormData();
    formData.append("userId", userId);
    formData.append("cropName", cropName);
    formData.append("diseaseName", diseaseName);
    formData.append("file", fileInput.files[0]);
	formData.append("overwrite", true);

    console.log("📤 서버로 보낼 데이터:");
    console.log("   🔹 userId:", userId);
    console.log("   🔹 cropName:", cropName);
    console.log("   🔹 diseaseName:", diseaseName);
    console.log("   🔹 file:", fileInput.files[0].name);

    fetch("/api/ident/save", {
        method: "POST",
        body: formData // FormData 사용시 Content-Type 자동 설정 (multipart/form-data)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`🚨 서버 오류: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        alert("✅ 진단 이력이 저장되었습니다!");
        console.log("서버 응답:", data);
    })
    .catch(error => {
        alert("❌ 진단 이력 저장 실패! 다시 시도해주세요.");
        console.error("에러 발생:", error);
    });
}


// 이벤트 리스너 등록
document.getElementById("chooseFile").addEventListener("change", function(event) {
    handleFileUpload(event.target.files);
});
document.getElementById("diagnosis-button").addEventListener("click", startDiagnosis);
document.getElementById("save-history-button").addEventListener("click", saveDiagnosisHistory);
document.addEventListener("DOMContentLoaded", setUpDragAndDrop);
