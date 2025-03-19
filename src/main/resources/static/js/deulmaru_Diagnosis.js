let uploadedFile = null; // 업로드된 파일 저장 (중복 방지)
let diagnosisStarted = false; // 진단이 시작되었는지 여부

// 파일 업로드 이벤트 핸들러
function handleFileUpload(files) {
    const fileInput = document.getElementById("chooseFile");
    const resultText = document.getElementById("result-text");
    const diagnosisButton = document.getElementById("diagnosis-button");

    if (files.length === 0) {
        return;
    }

    const file = files[0]; // 하나의 파일만 업로드 처리
    const maxFileSize = 10 * 1024 * 1024; // 최대 파일 크기 10MB

    if (file.size > maxFileSize) {
        resultText.textContent = "🚨 파일 크기가 너무 큽니다. 10MB 이하로 업로드 해주세요.";
        return;
    }

    // 중복 파일 업로드 방지
    if (uploadedFile && uploadedFile.name === file.name && uploadedFile.size === file.size) {
        resultText.textContent = "🚨 이미 업로드된 파일입니다.";
        return;
    }

    uploadedFile = file; // 새로운 파일 저장
    resultText.textContent = "📂 파일이 업로드되었습니다. '진단하기' 버튼을 눌러주세요.";
    diagnosisButton.disabled = false; // 진단 버튼 활성화

    // 파일 이름 표시
    updateFileList(file);
}

// 파일 목록 업데이트 함수
function updateFileList(file) {
    const fileLabel = document.querySelector(".file-label");
    const fileList = document.getElementById("file-list");

    // 기존 파일 목록 초기화 후 새 파일 추가
    fileList.innerHTML = `<div class="file-item">📂 ${file.name}</div>`;

    // 버튼을 "진단하기"로 변경하고 색상 변경
    fileLabel.textContent = "진단하기";
    fileLabel.style.backgroundColor = "#ffba42"; // 버튼 색 변경
}

// 진단 버튼 클릭 시 실행될 함수
function startDiagnosis() {
    if (!uploadedFile || diagnosisStarted) {
        return;
    }

    const resultText = document.getElementById("result-text");
    const formData = new FormData();
    formData.append("file", uploadedFile);

    resultText.textContent = "🔍 분석 중...";

    fetch('/predict', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        resultText.textContent = `🦠 병해충 진단 결과: ${data.prediction}`;
    })
    .catch(error => {
        resultText.textContent = "❌ 진단 실패! 다시 시도해주세요.";
        console.error('Error:', error);
    });

    diagnosisStarted = true; // 진단 시작
}

// 안내 가이드를 토글하는 함수
function toggleGuide() {
    const guideContent = document.getElementById("aiGuide");
    guideContent.classList.toggle("show");
}

// 드래그 앤 드롭 기능 추가
function setUpDragAndDrop() {
    const dropZone = document.getElementById("drop-file");
    const fileInput = document.getElementById("chooseFile");

    dropZone.addEventListener("dragover", function (event) {
        event.preventDefault();
        dropZone.style.border = "3px solid #1e824c"; // 강조 효과
    });

    dropZone.addEventListener("dragleave", function () {
        dropZone.style.border = "3px dashed #dbdbdb"; // 원래대로
    });

    dropZone.addEventListener("drop", function (event) {
        event.preventDefault();
        dropZone.style.border = "3px dashed #dbdbdb"; // 원래대로
        const files = event.dataTransfer.files;
        fileInput.files = files; // input에도 반영
        handleFileUpload(files);
    });
}

// 파일 선택 이벤트 리스너 추가
document.getElementById("chooseFile").addEventListener("change", function(event) {
    handleFileUpload(event.target.files);
});

// 진단 버튼 이벤트 리스너 추가
document.getElementById("diagnosis-button").addEventListener("click", startDiagnosis);

// 페이지 로드 시 드래그 앤 드롭 기능 활성화
document.addEventListener("DOMContentLoaded", setUpDragAndDrop);
