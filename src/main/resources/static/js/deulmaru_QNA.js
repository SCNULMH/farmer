document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ deulmaru_QNA.js 로드 완료");

    // 드롭다운 동작 처리
    const dropdownItems = document.querySelectorAll(".dropdown-menu .dropdown-item");
    dropdownItems.forEach(item => {
        item.addEventListener("click", function (e) {
            e.preventDefault();
            const selectedValue = this.getAttribute("data-value");
            const selectedText = this.textContent;

            const button = document.getElementById("dropdownMenuButton");
            button.textContent = selectedText;
            button.setAttribute("data-search-type", selectedValue);
        });
    });

    // 기본값 세팅 (제목+내용 → 실제 API에서는 '제목'만 검색됨) 이거 점검해야해
    // 기본값 세팅 (제목+내용 → 실제 API에서는 '제목'만 검색됨) 이거 점검해야해
    // 기본값 세팅 (제목+내용 → 실제 API에서는 '제목'만 검색됨) 이거 점검해야해
    // 기본값 세팅 (제목+내용 → 실제 API에서는 '제목'만 검색됨) 이거 점검해야해
    // 기본값 세팅 (제목+내용 → 실제 API에서는 '제목'만 검색됨) 이거 점검해야해
    const dropdownButton = document.getElementById("dropdownMenuButton");
    dropdownButton.textContent = "제목+내용";
    dropdownButton.setAttribute("data-search-type", "title"); // 기본은 제목 검색

    const consultQuery = document.getElementById("consultQuery");
    consultQuery.value = "";
    consultQuery.placeholder = "검색어를 입력하세요!";

    // 페이지 로드시 자동 검색 실행
    fetchConsultData("벼");

    // 검색창 엔터 입력 시 검색 실행
    consultQuery.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            fetchConsultData();
        }
    });
});

// 병해충 상담 검색 요청
window.fetchConsultData = function (customQuery) {
    const query = customQuery || document.getElementById("consultQuery").value;
    if (!query) {
        alert("검색어를 입력하세요!");
        return;
    }

    // 드롭다운 값 가져오기 (제목 or 내용)
    const searchType = document.getElementById("dropdownMenuButton").getAttribute("data-search-type");
    const paramKey = (searchType === "content") ? "reqestCn" : "dgnssReqSj"; // 기본은 제목

    const url = `http://localhost:8082/ncpms/consult?query=${encodeURIComponent(query)}&type=${paramKey}&page=1`;
    console.log("🔍 병해충 상담 요청 URL:", url);

    fetch(url)
        .then(response => response.text())
        .then(str => new window.DOMParser().parseFromString(str, "application/xml"))
        .then(data => {
            const items = data.getElementsByTagName("item");
            const tableBody = document.querySelector("#consultResultTable tbody");
            tableBody.innerHTML = "";

            for (let item of items) {
                const title = item.getElementsByTagName("dgnssReqSj")[0]?.textContent || "정보 없음";
                const consultId = item.getElementsByTagName("dgnssReqNo")[0]?.textContent || "";
                const requestDate = item.getElementsByTagName("registDatetm")[0]?.textContent || "정보 없음";

                const row = `
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
	            console.log("🔍 응답 content-type:", contentType);  // 응답의 content-type 확인

	            if (contentType && contentType.includes("text/plain")) {
	                return response.text();  // 응답이 텍스트일 경우
	            } else {
	                throw new Error("알 수 없는 응답 형식");
	            }
	        })
	        .then(data => {
	            console.log("🔍 상세보기 응답 데이터:", data);  // 응답 데이터 확인

	            // XML 파싱
	            let parser = new DOMParser();
	            let xmlDoc = parser.parseFromString(data, "application/xml");

	            // 데이터를 추출
	            let title = xmlDoc.getElementsByTagName("dgnssReqSj")[0]?.textContent || "정보 없음";
	            let requestContent = xmlDoc.getElementsByTagName("reqestCn")[0]?.textContent || "상담 요청 내용 없음";
	            let opinion = xmlDoc.getElementsByTagName("dgnssOpin")[0]?.textContent || "전문가 의견 없음";

	            // 이미지 추출
	            let imageItems = xmlDoc.getElementsByTagName("imageList")[0]?.getElementsByTagName("item");
	            let imageHtml = "";

	            if (imageItems) {
	                for (let i = 0; i < imageItems.length; i++) {
	                    let imageUrl = imageItems[i].getElementsByTagName("image")[0]?.textContent || "";
	                    if (imageUrl) {
	                        imageHtml += `<img src="${imageUrl}" alt="상담 이미지" width="150" style="margin:5px;" onclick="showImageModal('${imageUrl}')">`;
	                    }
	                }
	            }

	            // 데이터를 HTML로 생성
	            let html = `
	                <div class="consult-detail-container">
	                    <h2 class="consult-title">${title}</h2>
	                    <p><strong>상담 요청 내용:</strong> <span class="consult-content">${requestContent}</span></p>
	                    <p><strong>전문가 의견:</strong> <span class="consult-opinion">${opinion}</span></p>
	                    <div class="consult-images">${imageHtml}</div>
	                </div>
	            `;

	            // 모달에 내용 삽입 후 표시
	            document.getElementById("consultDetailContainer").innerHTML = html;
	            document.getElementById("consultDetailModal").style.display = "block";
	        })
	        .catch(error => {
	            console.error("🔴 병해충 상담 상세보기 에러:", error);
	        });
	};
	

// 이미지 클릭 시 모달로 확대보기
	function showImageModal(imageSrc) {
	    var modal = document.getElementById("imageModal");
	    var modalImage = document.getElementById("modalImage");

	    modalImage.src = imageSrc; // 클릭한 이미지의 src를 모달 이미지에 설정
	    modal.style.display = "block"; // 모달 표시
	}

	// 모달 닫기
	function closeImageModal() {
	    document.getElementById("imageModal").style.display = "none"; // 모달 숨김
	}


    // 모달 닫기 함수
    window.closeModal = function() {
        document.getElementById('consultDetailModal').style.display = 'none';
    };
