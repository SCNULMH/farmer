document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ script.js 로드 완료");
    function showTab(tabName) {
  		document.querySelectorAll(".tab-content").forEach(tab => tab.style.display = "none");
  		document.getElementById(tabName + "Tab").style.display = "block";
  	}
  	window.showTab = showTab;
  	showTab('search'); // 기본 탭 설정
    // 병해충 검색
    window.fetchSearchData = function () {
        let query = document.getElementById("searchQuery").value;
        let searchType = document.querySelector('input[name="searchType"]:checked').value;

        if (!query) {
            alert("검색어를 입력하세요!");
            return;
        }

        let url = `/api/search?query=${encodeURIComponent(query)}&type=${searchType}`;
        console.log("🔍 병해충 검색 요청 URL:", url);

        fetch(url)
            .then(response => response.json())
            .then(data => {
                if (!data.items || data.items.length === 0) {
                    console.warn("⚠ 검색 결과 없음");
                    alert("검색 결과가 없습니다.");
                    return;
                }

                let resultContainer = document.getElementById("resultTable").getElementsByTagName('tbody')[0];
                resultContainer.innerHTML = "";

                data.items.forEach(item => {
                    let row = `
                        <tr>
                            <td>${item.cropName || "정보 없음"}</td>
                            <td>${item.sickNameKor || "정보 없음"}</td>
                            <td><img src="${item.thumbImg || ''}" alt="이미지 없음" width="100"></td>
                            <td><button onclick="fetchSickDetail('${item.sickKey}')">상세보기</button></td>
                        </tr>
                    `;
                    resultContainer.innerHTML += row;
                });
            })
            .catch(error => console.error("🔴 병해충 검색 에러:", error));
    };
});
