document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("searchBox");

    // 엔터 키 이벤트 감지 및 실행
    searchInput.addEventListener("keypress", function (event) {
        if (event.key === "Enter") { // 엔터 키 감지
            event.preventDefault(); // 기본 동작 방지 (폼 제출 방지)
            executeSearch(); // 검색 함수 실행
        }
    });
});

// 검색 실행 함수 (alert 예시)
function executeSearch() {
    const query = document.getElementById("searchBox").value.trim(); // 입력 값 가져오기
    if (query) {
        alert("검색 실행: " + query);
        // 실제 적용 시 API 호출 등으로 확장 가능
        // 예: window.location.href = "/search?query=" + encodeURIComponent(query);
    } else {
        alert("검색어를 입력하세요!");
    }
}

// 드롭다운 선택 시 검색 카테고리 변경
function setSearchCategory(category) {
    document.getElementById("dropdownMenuButton").textContent = category;
}
