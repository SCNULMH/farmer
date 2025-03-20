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
            resultText.textContent = ` ${predictionResult}`;
            
            // 업로드한 이미지 표시
            const resultImage = document.getElementById("result-image");
            resultImage.src = data.imageUrl;
            resultImage.style.display = "none";
            
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
async function saveDiagnosisHistory() {
    let userId = sessionStorage.getItem("userId"); // ✅ userId 세션에서 가져오기
    if (!userId || userId === "null") {
        console.warn("⚠️ userId가 null입니다. 세션에서 다시 가져옵니다.");
        const sessionUser = JSON.parse(sessionStorage.getItem("user")); // 세션에서 전체 유저 정보 가져오기
        userId = sessionUser ? sessionUser.userId : null;
    }

    let resultText = document.getElementById("result-text").innerText.trim();
    const cropName = document.getElementById("cropNameInput").value.trim();
    const file = document.getElementById("chooseFile").files[0];

    if (!file || !cropName) {
        alert("❌ 작물명과 이미지를 입력해야 합니다.");
        return;
    }

    let diseaseName = "알 수 없음";
    let confidenceScore = 0;

    try {
        // ✅ JSON 형식이면 파싱
        if (resultText.startsWith("{") && resultText.endsWith("}")) {
            const parsedResult = JSON.parse(resultText);
            diseaseName = parsedResult["병해충진단 결과"] || "알 수 없음";
            confidenceScore = parseFloat(parsedResult["정확도"].replace("%", "")) || 0;
        } else {
            // ✅ 일반 문자열 형태 ("예상 병명: 정상\n정확도: 99.8%") 처리
            const lines = resultText.split("\n");
            if (lines.length >= 2) {
                diseaseName = lines[0].replace("병해충진단 결과:", "").trim();
                confidenceScore = parseFloat(lines[1].replace("정확도:", "").replace("%", "").trim()) || 0;
            }
        }

        console.log("📤 서버로 전송할 데이터:");
        console.log("   🔹 userId:", userId);
        console.log("   🔹 diseaseName:", diseaseName);
        console.log("   🔹 confidenceScore:", confidenceScore);
        console.log("   🔹 cropName:", cropName);
        console.log("   🔹 file:", file.name);

        if (!userId) {
            alert("❌ 로그인 정보가 없습니다. 다시 로그인해주세요.");
            return;
        }

        const formData = new FormData();
        formData.append("userId", userId);
        formData.append("diseaseName", diseaseName);
        formData.append("confidenceScore", confidenceScore);
        formData.append("cropName", cropName);
        formData.append("file", file);

        const response = await fetch("/api/ident/save", {
            method: "POST",
            body: formData,
            credentials: "include",
        });

        if (response.ok) {
            console.log("✅ 진단 이력이 성공적으로 저장되었습니다.");
        } else {
            const result = await response.json();
            document.getElementById("result-text").textContent = "❌ 진단 이력 저장 실패: " + result.error;
        }
    } catch (error) {
        console.error("🚨 서버 오류 발생:", error);
        document.getElementById("result-text").textContent = "🚨 서버 오류 발생: " + error;
    }
}


// 이벤트 리스너 등록
document.getElementById("chooseFile").addEventListener("change", function(event) {
    handleFileUpload(event.target.files);
});
document.getElementById("diagnosis-button").addEventListener("click", startDiagnosis);
document.getElementById("save-history-button").addEventListener("click", saveDiagnosisHistory);
document.addEventListener("DOMContentLoaded", setUpDragAndDrop);
