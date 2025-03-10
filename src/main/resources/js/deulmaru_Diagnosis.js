function toggleGuide() {
    let guideContent = document.getElementById("aiGuide");
    let button = document.querySelector(".btn-show-more");

    // `show` 클래스를 토글하여 max-height 변경
    guideContent.classList.toggle("show");

    if (guideContent.classList.contains("show")) {
        button.innerHTML = "▲"; // 버튼 아이콘 변경
    } else {
        button.innerHTML = "▼"; // 버튼 아이콘 변경
    }
}





// 파일 업로드
document.addEventListener("DOMContentLoaded", function () {
    const dropZone = document.getElementById("drop-file");
    const fileInput = document.getElementById("chooseFile");
    const fileLabel = document.querySelector(".file-label");

    // 파일 목록을 표시할 요소 생성 (버튼 위에 추가)
    const fileList = document.createElement("div");
    fileList.classList.add("file-list");
    fileLabel.parentNode.insertBefore(fileList, fileLabel); // 버튼 위에 삽입

    // 파일 목록 업데이트 함수
    function updateFileList(files) {
        fileList.innerHTML = ""; // 기존 목록 초기화
        if (files.length > 0) {
            Array.from(files).forEach(file => {
                const fileItem = document.createElement("div");
                fileItem.classList.add("file-item");
                fileItem.textContent = `📂 ${file.name}`;
                fileList.appendChild(fileItem);
            });

            // 버튼을 "진단하기"로 변경하고 색상 변경
            fileLabel.textContent = "진단하기";
            fileLabel.style.backgroundColor = "#ffba42"; // 버튼 색 변경
        } else {
            fileLabel.textContent = "파일 선택";
            fileLabel.style.backgroundColor = "#5b975b"; // 원래 색상 복구
        }
    }

    // 파일 선택 시 이벤트
    fileInput.addEventListener("change", function (event) {
        updateFileList(event.target.files);
    });

    // 드래그 앤 드롭 기능 추가
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
    });
});



