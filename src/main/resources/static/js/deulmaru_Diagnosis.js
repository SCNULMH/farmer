let filesUploaded = false; // 파일 업로드가 완료되었는지 확인하는 플래그

// 파일 목록 업데이트 함수
function updateFileList(files) {
    const fileLabel = document.querySelector(".file-label");
    const fileList = document.createElement("div");
    fileList.classList.add("file-list");
    fileLabel.parentNode.insertBefore(fileList, fileLabel); // 버튼 위에 삽입

    fileList.innerHTML = ""; // 기존 목록 초기화
    if (files.length > 0 && !filesUploaded) {  // 파일이 업로드되지 않았을 때만 처리
        Array.from(files).forEach(file => {
            const fileItem = document.createElement("div");
            fileItem.classList.add("file-item");
            fileItem.textContent = `📂 ${file.name}`;
            fileList.appendChild(fileItem);
        });

        // 버튼을 "진단하기"로 변경하고 색상 변경
        fileLabel.textContent = "진단하기";
        fileLabel.style.backgroundColor = "#ffba42"; // 버튼 색 변경
        filesUploaded = true;  // 파일 업로드 처리 완료 플래그 설정
    }
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
        updateFileList(files);

        // 드래그 앤 드롭된 파일을 서버로 전송하여 예측
        handleFileUpload(files);
    });
}

// dropFile 객체 정의 (파일 업로드 및 처리)
const dropFile = {
    handleFiles: function (files) {
        updateFileList(files);  // 파일 목록 업데이트
        // 파일 업로드 후 결과를 받아오는 작업을 처리하는 함수
        handleFileUpload(files);
    }
};

// 파일 업로드 후 분석 처리
function handleFileUpload(files) {
    const resultText = document.getElementById("result-text");

    // 결과 텍스트 초기화
    resultText.textContent = "분석 중...";

    // AJAX 요청으로 서버에 파일을 전송하고 분석 결과를 받기
    const formData = new FormData();
    for (let file of files) {
        formData.append("file", file);
    }

    fetch('/upload', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        // 예측 결과 받기
        resultText.textContent = `병해충 진단 결과: ${data.prediction}`;  // 예측된 결과
    })
    .catch(error => {
        resultText.textContent = "예측 실패";
        console.error('Error:', error);
    });
}

// DOMContentLoaded 이벤트가 발생했을 때 실행되는 함수
function initFileUpload() {
    const fileInput = document.getElementById("chooseFile");

    // 파일 선택 시 이벤트
    fileInput.addEventListener("change", function (event) {
        dropFile.handleFiles(event.target.files);
    });

    // 드래그 앤 드롭 기능 초기화
    setUpDragAndDrop();
}




// 파일 업로드 후 분석 처리
function handleFileUpload(files) {
    const resultText = document.getElementById("result-text");

    // 업로드 파일 크기 확인 (예: 최대 10MB)
    const maxFileSize = 10 * 1024 * 1024; // 10MB

    // 파일 크기 체크
    for (let file of files) {
        if (file.size > maxFileSize) {
            resultText.textContent = "🚨 파일 크기가 너무 큽니다. 10MB 이하로 업로드 해주세요.";
            return;
        }
    }

    // 결과 텍스트 초기화
    resultText.textContent = "분석 중...";

    // AJAX 요청으로 서버에 파일을 전송하고 분석 결과를 받기
    const formData = new FormData();
    for (let file of files) {
        formData.append("file", file);
    }

    fetch('/upload', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("서버 오류 발생");
        }
        return response.json();
    })
    .then(data => {
        if (data.prediction) {
            resultText.textContent = `병해충 진단 결과: ${data.prediction}`;
        } else {
            resultText.textContent = "예측 실패: 서버에서 데이터를 받지 못했습니다.";
        }
    })
    .catch(error => {
        resultText.textContent = "예측 실패";
        console.error('Error:', error);
    });
}


// DOMContentLoaded 이벤트가 발생했을 때 initFileUpload 함수 실행
document.addEventListener("DOMContentLoaded", initFileUpload);
