document.addEventListener("DOMContentLoaded", function() {
    console.log("✅ deulmaru_const.js 로드 완료");

    // 탭 전환 기능 (공통)
    function showTab(tabName) {
        document.querySelectorAll(".tab-content").forEach(tab => tab.style.display = "none");
        let targetTab = document.getElementById(tabName + "Tab");
        if (targetTab) {
            targetTab.style.display = "block";
        }
    }

    window.showTab = showTab;
    // 기본 탭을 병해충 검색 탭으로 설정 (해당 엘리먼트가 존재할 경우)
    if(document.getElementById("searchTab")) {
        showTab('search');
    }
});
