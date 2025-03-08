document.addEventListener("DOMContentLoaded", function() {
    console.log("✅ script.js 로드 완료");

    document.getElementById("searchQuery").addEventListener("keypress", function(event) {
        if (event.key === "Enter") {
            fetchSearchData();
        }
    });

    // ✅ 탭 전환 기능
    function showTab(tabName) {
        document.querySelectorAll(".tab-content").forEach(tab => tab.style.display = "none");
        document.getElementById(tabName + "Tab").style.display = "block";
    }

    window.showTab = showTab;
    showTab('search'); // 기본 탭을 병해충 검색 탭으로 설정

    // ✅ 병해충 검색 요청 함수
    window.fetchSearchData = function() {
        let query = document.getElementById("searchQuery").value;
        let searchType = document.querySelector('input[name="searchType"]:checked').value;

        if (!query) {
            alert("검색어를 입력하세요!");
            return;
        }

        let url = `http://localhost:8082/api/search?query=${query}&type=${searchType}`;
        console.log("🔍 병해충 검색 요청 URL:", url);

        fetch(url)
            .then(response => response.text())
            .then(str => new window.DOMParser().parseFromString(str, "text/xml"))
            .then(data => {
                let resultContainer = document.getElementById("resultTable").getElementsByTagName('tbody')[0];
                resultContainer.innerHTML = "";

                let items = data.getElementsByTagName("item");

                for (let item of items) {
                    let cropName = item.getElementsByTagName("cropName")[0]?.textContent || "정보 없음";
                    let sickNameKor = item.getElementsByTagName("sickNameKor")[0]?.textContent || "정보 없음";
                    let sickNameEng = item.getElementsByTagName("sickNameEng")[0]?.textContent || "정보 없음";
                    let sickNameChn = item.getElementsByTagName("sickNameChn")[0]?.textContent || "정보 없음";
                    let thumbImg = item.getElementsByTagName("thumbImg")[0]?.textContent || "";
                    let sickKey = item.getElementsByTagName("sickKey")[0]?.textContent || "";

                    let row = `
                        <tr>
                            <td>${cropName}</td>
                            <td>${sickNameKor}</td>
                            <td>${sickNameEng}</td>
                            <td>${sickNameChn}</td>
                            <td><img src="${thumbImg}" alt="이미지 없음" width="100"></td>
                            <td><button onclick="fetchSickDetail('${sickKey}')">상세보기</button></td>
                        </tr>
                    `;
                    resultContainer.innerHTML += row;
                }
            })
            .catch(error => {
                console.error("🔴 병해충 검색 에러:", error);
            });
    };

    // ✅ 병해충 상세보기 요청 (새로운 페이지로 이동)
    window.fetchSickDetail = function(sickKey) {
        if (!sickKey || sickKey.trim() === "") {
            console.error("유효하지 않은 sickKey:", sickKey);
            return;
        }
        
        // disease-detail.html로 이동하며 sickKey 전달
        window.location.href = `disease-detail.html?sick_key=${sickKey}`;
    };

    // ✅ 병해충 상담 검색 요청 함수
    window.fetchConsultData = function() {
        let query = document.getElementById("consultQuery").value;
        if (!query) {
            alert("검색어를 입력하세요!");
            return;
        }

        let url = `http://localhost:8082/api/consult?query=${query}`;
        console.log("🔍 병해충 상담 요청 URL:", url);

        fetch(url)
            .then(response => response.text())
            .then(str => new window.DOMParser().parseFromString(str, "text/xml"))
            .then(data => {
                let items = data.getElementsByTagName("item");
                let tableBody = document.querySelector("#consultResultTable tbody");
                tableBody.innerHTML = "";

                for (let item of items) {
                    let title = item.getElementsByTagName("dgnssReqSj")[0]?.textContent || "정보 없음";
                    let consultId = item.getElementsByTagName("dgnssReqNo")[0]?.textContent || "";
                    let requestDate = item.getElementsByTagName("registDatetm")[0]?.textContent || "정보 없음";

                    let row = `
                        <tr>
                            <td>${title}</td>
                            <td>${consultId}</td>
                            <td>${requestDate}</td>
                            <td><button onclick="fetchConsultDetail('${consultId}')">상세보기</button></td>
                        </tr>
                    `;
                    tableBody.innerHTML += row;
                }
            })
            .catch(error => {
                console.error("🔴 병해충 상담 검색 에러:", error);
            });
    };

    // ✅ 병해충 상담 상세보기 요청 (새로운 페이지로 이동)
    window.fetchConsultDetail = function(consultId) {
        if (!consultId || consultId.trim() === "") {
            console.error("유효하지 않은 consult_id:", consultId);
            return;
        }

        // consult-detail.html로 이동하며 consultId 전달
        window.location.href = `consult-detail.html?consult_id=${consultId}`;
    };
});
