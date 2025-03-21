document.addEventListener("DOMContentLoaded", function() {
    console.log("✅ deulmaru_const.js 로드 완료");

    function showTab(tabName) {
        document.querySelectorAll(".tab-content").forEach(tab => tab.style.display = "none");
        let targetTab = document.getElementById(tabName);
        if (targetTab) {
            targetTab.style.display = "block";
            // benefit 탭 선택 시 관심 지원금 목록 로드
            if (tabName === "benefitTab") {
                loadInterestGrants();
            }
        
			if (tabName === "diagHistoryTab") {
			                loadDiagnosisHistory();
			}
			
			}
		
		}

    window.showTab = showTab;

    // 기본 탭을 "내 프로필"로 설정
    if(document.getElementById("profileTab")) {
        showTab('profileTab');
    }
});
