document.addEventListener("DOMContentLoaded", function() {
    console.log("✅ deulmaru_const.js 로드 완료");

    function showTab(tabName) {
        document.querySelectorAll(".tab-content").forEach(tab => tab.style.display = "none");
        let targetTab = document.getElementById(tabName);
        if (targetTab) {
            targetTab.style.display = "block";
        }
    }

    window.showTab = showTab;

    // ✅ 기본 탭을 "내 프로필"로 설정
    if(document.getElementById("profileTab")) {
        showTab('profileTab');
    }
});
