document.addEventListener("DOMContentLoaded", function() {
    console.log("✅ deulmaru_consult.js 로드 완료");

    // 병해충 상담 검색 요청 함수 (스프링부트 엔드포인트 사용)
    window.fetchConsultData = function() {
        let query = document.getElementById("consultQuery").value;
        if (!query) {
            alert("검색어를 입력하세요!");
            return;
        }
    
        // /ncpms/consult 엔드포인트 호출 (페이지 정보 포함)
        let url = `http://localhost:8082/ncpms/consult?query=${encodeURIComponent(query)}&page=1`;
        console.log("🔍 병해충 상담 요청 URL:", url);
    
        fetch(url)
            .then(response => response.text())
            .then(str => new window.DOMParser().parseFromString(str, "application/xml"))
            .then(data => {
                let items = data.getElementsByTagName("item");
                let tableBody = document.querySelector("#consultResultTable tbody");
                tableBody.innerHTML = ""; // 기존 결과 초기화
    
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
    
                // 페이지 번호 및 전체 페이지 정보 업데이트(필요시)
            })
            .catch(error => {
                console.error("🔴 병해충 상담 에러:", error);
            });
    };
    
    // 병해충 상담 상세보기 요청 함수
    window.fetchConsultDetail = function(consultId) {
        if (!consultId || consultId.trim() === "") {
            console.error("유효하지 않은 consult_id:", consultId);
            return;
        }
    
        let url = `http://localhost:8082/ncpms/consult_detail?consult_id=${consultId}`;
        console.log("🔍 병해충 상담 상세보기 요청 URL:", url);
    
        fetch(url)
            .then(response => {
                const contentType = response.headers.get("Content-Type");
                if (contentType && contentType.includes("application/xml")) {
                    return response.text();
                } else {
                    throw new Error("알 수 없는 응답 형식");
                }
            })
            .then(data => {
                let consultDetailContainer = document.getElementById("consultDetailContainer");
                consultDetailContainer.innerHTML = "";
    
                let xmlDoc = new window.DOMParser().parseFromString(data, "application/xml");
                let title = xmlDoc.getElementsByTagName("dgnssReqSj")[0]?.textContent || "정보 없음";
                let requestContent = xmlDoc.getElementsByTagName("reqestCn")[0]?.textContent || "상담 요청 내용 없음";
                let opinion = xmlDoc.getElementsByTagName("dgnssOpin")[0]?.textContent || "전문가 의견 없음";
    
                let imageItems = xmlDoc.getElementsByTagName("imageList")[0]?.getElementsByTagName("item");
                let imageHtml = "";
    
                if (imageItems) {
                    for (let i = 0; i < imageItems.length; i++) {
                        let imageUrl = imageItems[i].getElementsByTagName("image")[0]?.textContent || "";
                        if (imageUrl) {
                            imageHtml += `<img src="${imageUrl}" alt="상담 이미지" width="150" style="margin:5px;">`;
                        }
                    }
                }
    
                let html = `
                    <div class="consult-detail-container">
                        <h2 class="consult-title">${title}</h2>
                        <p><strong>상담 요청 내용:</strong> <span class="consult-content">${requestContent}</span></p>
                        <p><strong>전문가 의견:</strong> <span class="consult-opinion">${opinion}</span></p>
                        <div class="consult-images">${imageHtml}</div>
                    </div>
                `;
                consultDetailContainer.innerHTML = html;
                // 모달(또는 별도 영역) 표시 – 모달 구현 방식에 따라 수정
                document.getElementById("consultDetailModal").style.display = "block";
            })
            .catch(error => {
                console.error("🔴 병해충 상담 상세보기 에러:", error);
            });
    };

    // 모달 닫기 함수 (필요 시)
    window.closeModal = function() {
        document.getElementById('consultDetailModal').style.display = 'none';
    };
});
