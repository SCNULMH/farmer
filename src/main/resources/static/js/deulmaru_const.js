document.addEventListener("DOMContentLoaded", function() {
	console.log("✅ script.js 로드 완료");

	document.getElementById("searchQuery").addEventListener("keypress", function(event) {
		if (event.key === "Enter") {
			fetchSearchData();
		}
	});

	// 탭 전환 기능
	function showTab(tabName) {
		document.querySelectorAll(".tab-content").forEach(tab => tab.style.display = "none");
		document.getElementById(tabName + "Tab").style.display = "block";
	}

	window.showTab = showTab;
	showTab('search'); // 기본 탭을 병해충 검색 탭으로 설정

	// 병해충 검색 요청 함수
	window.fetchSearchData = function() {
		let query = document.getElementById("searchQuery").value;
		let searchType = document.querySelector('input[name="searchType"]:checked').value;  // 검색 타입 가져오기

		if (!query) {
			alert("검색어를 입력하세요!");
			return;
		}

		let url = `http://localhost:8082/api/search?query=${query}&type=${searchType}`;		console.log("🔍 병해충 검색 요청 URL:", url); // 요청 URL 확인

		fetch(url)
			.then(response => {
				const contentType = response.headers.get("Content-Type");
				if (contentType && contentType.includes("application/json")) {
					console.log("✅ JSON 응답 받음");
					return response.json(); // JSON 응답 처리
				} else if (contentType && contentType.includes("application/xml")) {
					console.log("✅ XML 응답 받음");
					return response.text(); // XML 응답 처리
				} else {
					throw new Error("알 수 없는 응답 형식");
				}
			})
			.then(data => {
				let resultContainer = document.getElementById("resultTable").getElementsByTagName('tbody')[0];
				resultContainer.innerHTML = ""; // 기존 결과 삭제

				if (typeof data === "string") {
					// XML 응답인 경우
					let xmlDoc = new window.DOMParser().parseFromString(data, "text/xml");
					let items = xmlDoc.getElementsByTagName("item");

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
                                <td><button onclick="fetchSickDetail('${sickKey}', '${cropName}')">상세보기</button></td> <!-- 상세보기 버튼 추가 -->
                            </tr>
                        `;
						resultContainer.innerHTML += row;
					}
				} else if (typeof data === "object") {
					// JSON 응답인 경우
					console.log("🔍 JSON 데이터:", data); // JSON 데이터 출력
					let html = "";
					data.service.list.forEach(item => {
						let cropName = item.cropName || "정보 없음";
						let sickNameKor = item.sickNameKor || "정보 없음";
						let sickNameEng = item.sickNameEng || "정보 없음";
						let sickNameChn = item.sickNameChn || "";
						let thumbImg = item.thumbImg || "";
						let sickKey = item.sickKey || "";

						html += `<tr>
                            <td>${cropName}</td>
                            <td>${sickNameKor}</td>
                            <td>${sickNameEng}</td>
                            <td>${sickNameChn}</td>
                            <td><img src="${thumbImg}" alt="이미지 없음" width="100"></td>
                            <td><button onclick="fetchSickDetail('${sickKey}')">상세보기</button></td>
                        </tr>`;
					});
					resultContainer.innerHTML = html;
				}
			})
			.catch(error => {
				console.error("🔴 병해충 검색 에러:", error);
			});
	};


	// 병해충 상세보기 요청 함수
	window.fetchSickDetail = function(sickKey) {
		if (!sickKey || sickKey.trim() === "") {
			console.error("유효하지 않은 sickKey:", sickKey);
			return;
		}

		// ✅ 병해충 상세보기 요청 URL 수정
		let url = `http://localhost:8082/api/sick_detail?sick_key=${sickKey}`;
		console.log("🔍 병해충 상세보기 요청 URL:", url); // 요청 URL 확인

		fetch(url)
			.then(response => response.text()) // XML 데이터 텍스트로 가져오기
			.then(str => new window.DOMParser().parseFromString(str, "text/xml")) // XML 파싱
			.then(data => {
				// 필요한 데이터 파싱
				let title = data.getElementsByTagName("sickNameKor")[0]?.textContent || "정보 없음";
				let description = data.getElementsByTagName("symptoms")[0]?.textContent || "설명 없음";
				let preventionMethod = data.getElementsByTagName("preventionMethod")[0]?.textContent || "방제 방법 없음";
				let images = data.getElementsByTagName("imageList")[0]?.getElementsByTagName("item");
				let imageHtml = "";

				// 병해충 이미지들
				if (images) {
					for (let imageItem of images) {
						let imageUrl = imageItem.getElementsByTagName("image")[0]?.textContent || "";
						if (imageUrl) {
							imageHtml += `<img src="${imageUrl}" alt="병해충 이미지" width="150" style="margin:5px;">`;
						}
					}
				}

				// HTML 내용 구성
				let html = `
                <div class="sick-detail-container">
                    <h2 class="sick-title">${title}</h2>
                    <p><strong>병해충 설명:</strong> <span class="sick-description">${description}</span></p>
                    <p><strong>방제 방법:</strong> <span class="prevention-method">${preventionMethod}</span></p>
                    <div class="sick-images">${imageHtml}</div>
                </div>
            `;
				// 상세보기 내용에 반영
				document.getElementById("sickDetailContainer").innerHTML = html;

				// 모달 표시
				document.getElementById("sickDetailModal").style.display = "block";
			})
			.catch(error => {
				console.error("🔴 병해충 상세보기 에러:", error);
			});
	};

	// 병해충 상세보기 모달 닫기 함수
	window.closeSickDetailModal = function() {
		document.getElementById('sickDetailModal').style.display = 'none';
	};



	let currentPage = 1;
	let totalPages = 5; // 전체 페이지 수

	// 병해충 상담 검색 요청 함수
	window.fetchConsultData = function() {
		let query = document.getElementById("consultQuery").value;
		if (!query) {
			alert("검색어를 입력하세요!");
			return;
		}


		let url = `http://localhost:8082/api/consult?query=${query}&page=${currentPage}`;
		console.log("🔍 병해충 상담 요청 URL:", url); // 요청 URL 확인

		fetch(url)
			.then(response => response.text()) // XML 데이터 텍스트로 가져오기
			.then(str => new window.DOMParser().parseFromString(str, "text/xml")) // XML 파싱
			.then(data => {
				let items = data.getElementsByTagName("item");
				let tableBody = document.querySelector("#consultResultTable tbody");
				tableBody.innerHTML = ""; // 기존 결과 삭제

				// 상담 결과 표로 출력
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

				// 페이지 정보 업데이트
				totalPages = parseInt(data.getElementsByTagName("totalCount")[0]?.textContent) / 10;
				updatePageInfo();
			})
			.catch(error => {
				console.error("🔴 병해충 상담 에러:", error);
			});
	};

	// 페이지 정보 업데이트 함수
	function updatePageInfo() {
		document.getElementById("pageInfo").innerText = `현재 페이지 : ${currentPage} 페이지`;
		document.getElementById("prevBtn").disabled = currentPage <= 1;
		document.getElementById("nextBtn").disabled = currentPage >= totalPages;
		generatePageNumbers();
	}

	// 페이지 번호 생성 함수
	function generatePageNumbers() {
		let pageNumbers = document.getElementById("pageNumbers");
		pageNumbers.innerHTML = ""; // 기존 페이지 번호 삭제

		let startPage = Math.max(1, currentPage - 2); // 현재 페이지 기준 -2
		let endPage = Math.min(totalPages, currentPage + 2); // 현재 페이지 기준 +2

		for (let i = startPage; i <= endPage; i++) {
			let button = document.createElement("button");
			button.textContent = i;
			button.onclick = function() {
				currentPage = i;
				fetchConsultData();
			};
			if (i === currentPage) {
				button.style.fontWeight = "bold"; // 현재 페이지 강조
			}
			pageNumbers.appendChild(button);
		}
	}

	// 페이지 변경 함수
	window.changePage = function(direction) {
		if (direction === -1 && currentPage > 1) {
			currentPage--;
		} else if (direction === 1 && currentPage < totalPages) {
			currentPage++;
		}
		window.fetchConsultData();
	};



	window.fetchConsultDetail = function(consultId) {
	    if (!consultId || consultId.trim() === "") {
	        console.error("유효하지 않은 consult_id:", consultId);
	        return;
	    }


		let url = `http://localhost:8082/api/consult_detail?consult_id=${consultId}`;
	    console.log("🔍 병해충 상담 상세보기 요청 URL:", url);

	    fetch(url)
	        .then(response => response.text())
	        .then(data => {
	            let xmlDoc = new window.DOMParser().parseFromString(data, "text/xml");
	            let title = xmlDoc.getElementsByTagName("dgnssReqSj")[0]?.textContent || "정보 없음";
	            let requestContent = xmlDoc.getElementsByTagName("reqestCn")[0]?.textContent || "상담 요청 내용 없음";
	            let opinion = xmlDoc.getElementsByTagName("dgnssOpin")[0]?.textContent || "전문가 의견 없음";

	            // ✅ 이미지 목록 가져오기
	            let imageItems = xmlDoc.getElementsByTagName("imageList")[0]?.getElementsByTagName("item");
	            let imageHtml = "";

	            if (imageItems && imageItems.length > 0) {
	                for (let i = 0; i < imageItems.length; i++) {
	                    let imageUrl = imageItems[i].getElementsByTagName("image")[0]?.textContent || "";
	                    if (imageUrl) {
	                        imageHtml += `<img src="${imageUrl}" alt="상담 이미지" width="150" style="margin:5px;">`;
	                    }
	                }
	            } else {
	                imageHtml = "<p>이미지가 없습니다.</p>";
	            }

	            // ✅ HTML 업데이트
	            let html = `
	                <div class="consult-detail-container">
	                    <h2 class="consult-title">${title}</h2>
	                    <p><strong>상담 요청 내용:</strong> ${requestContent}</p>
	                    <p><strong>전문가 의견:</strong> ${opinion}</p>
	                    <div class="consult-images">${imageHtml}</div>
	                </div>
	            `;

	            document.getElementById("consultDetailContainer").innerHTML = html;

	            // ✅ 모달 표시
	            document.getElementById("consultDetailModal").style.display = "block";
	        })
	        .catch(error => {
	            console.error("🔴 병해충 상담 상세보기 에러:", error);
	        });
	};


	// 모달 닫기 함수
	window.closeModal = function() {
		document.getElementById('consultDetailModal').style.display = 'none';
	};

	// 챗봇 열기 함수
	window.showChatbot = function() {
		document.getElementById('chatbot').style.display = 'block';
	};

	// 챗봇 닫기 함수
	window.closeChatbot = function() {
		document.getElementById('chatbot').style.display = 'none';
	};
	
	
	// 카카오 연동 해제
	document.getElementById("unlinkKakaoBtn").addEventListener("click", function() {
	    fetch("/auth/kakao/unlink", {
	        method: "POST"
	    }).then(response => {
	        if (response.redirected) {
	            window.location.href = response.url;
	        }
	    }).catch(error => {
	        console.error("🔴 카카오 연동 해제 에러:", error);
	    });
	});

});