document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ script.js 로드 완료");

    function showTab(tabName) {
        document.querySelectorAll(".tab-content").forEach(tab => tab.style.display = "none");
        document.getElementById(tabName + "Tab").style.display = "block";
    }

    window.showTab = showTab;
    showTab('search'); // 기본 탭 설정

    /**
     * ✅ 응답 데이터를 JSON 또는 XML에 맞춰 유연하게 처리하는 함수
     */
	async function parseResponse(response) {
	      const contentType = response.headers.get("content-type");
	      const responseText = await response.text(); // 우선 text로 받음

	      if (contentType.includes("application/json")) {
	          return JSON.parse(responseText); // JSON 파싱
	      } else if (contentType.includes("application/xml") || contentType.includes("text/xml")) {
	          const parser = new DOMParser();
	          return parser.parseFromString(responseText, "application/xml"); // XML 파싱
	      } else if (contentType.includes("text/plain")) {
	          return { text: responseText }; // 텍스트 그대로 반환
	      } else {
	          throw new Error("지원되지 않는 응답 형식: " + contentType);
	      }
	  }


    /**
     * ✅ 병해충 검색 기능 (JSON/XML 대응)
     */
    window.fetchSearchData = async function () {
        let query = document.getElementById("searchQuery").value;
        let searchType = document.querySelector('input[name="searchType"]:checked').value;

        if (!query) {
            alert("검색어를 입력하세요!");
            return;
        }

        let url = `/api/search?query=${encodeURIComponent(query)}&type=${searchType}`;
        console.log("🔍 병해충 검색 요청 URL:", url);

        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error(`서버 응답 오류: ${response.status} ${response.statusText}`);

            const data = await parseResponse(response); // JSON 또는 XML로 자동 파싱

            console.log("🔍 병해충 검색 응답 데이터:", data);

            let resultContainer = document.querySelector("#resultTable tbody");
            resultContainer.innerHTML = "";

            if (data instanceof Document) {
                // XML 처리
                const items = data.getElementsByTagName("item");
                if (items.length === 0) {
                    alert("검색 결과가 없습니다.");
                    return;
                }
                for (let item of items) {
                    let cropName = item.getElementsByTagName("cropName")[0]?.textContent || "정보 없음";
                    let sickNameKor = item.getElementsByTagName("sickNameKor")[0]?.textContent || "정보 없음";
                    let thumbImg = item.getElementsByTagName("thumbImg")[0]?.textContent || "";
                    let sickKey = item.getElementsByTagName("sickKey")[0]?.textContent || "";

                    let row = `
                        <tr>
                            <td>${cropName}</td>
                            <td>${sickNameKor}</td>
                            <td><img src="${thumbImg}" alt="이미지 없음" width="100"></td>
                            <td><button onclick="fetchSickDetail('${sickKey}')">상세보기</button></td>
                        </tr>
                    `;
                    resultContainer.innerHTML += row;
                }
            } else {
                // JSON 처리
                if (!data.items || data.items.length === 0) {
                    alert("검색 결과가 없습니다.");
                    return;
                }
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
            }

        } catch (error) {
            console.error("🔴 병해충 검색 에러:", error);
            alert("검색 중 오류가 발생했습니다.");
        }
    };

    /**
     * ✅ 병해충 상세보기 기능 (JSON/XML 대응)
     */
    window.fetchSickDetail = async function (sickKey) {
        let url = `/api/sick_detail?sick_key=${sickKey}`;
        console.log("🔍 병해충 상세보기 요청 URL:", url);

        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error(`서버 응답 오류: ${response.status} ${response.statusText}`);

            const data = await parseResponse(response);
            let html = "";

            if (data instanceof Document) {
                // XML 처리
                let name = data.getElementsByTagName("sickNameKor")[0]?.textContent || "정보 없음";
                let symptoms = data.getElementsByTagName("symptoms")[0]?.textContent || "설명 없음";
                let prevention = data.getElementsByTagName("preventionMethod")[0]?.textContent || "방제 방법 없음";
                let images = Array.from(data.getElementsByTagName("image")).map(img => `<img src="${img.textContent}" width="150">`).join('');

                html = `<h2>${name}</h2><p><strong>증상:</strong> ${symptoms}</p><p><strong>방제 방법:</strong> ${prevention}</p><div>${images}</div>`;
            } else {
                // JSON 처리
                html = `<h2>${data.sickNameKor || "정보 없음"}</h2>
                        <p><strong>증상:</strong> ${data.symptoms || "설명 없음"}</p>
                        <p><strong>방제 방법:</strong> ${data.preventionMethod || "방제 방법 없음"}</p>
                        <div>${data.imageList.map(img => `<img src="${img}" width="150">`).join('')}</div>`;
            }

            document.getElementById("sickDetailContainer").innerHTML = html;
            document.getElementById("sickDetailModal").style.display = "block";

        } catch (error) {
            console.error("🔴 병해충 상세보기 에러:", error);
        }
    };
	
	
	
	
	/**
	     * ✅ 병해충 상담 검색 기능 (JSON/XML 대응)
	     */
	window.fetchConsultData = async function () {
	        let query = document.getElementById("consultQuery").value;
	        if (!query) {
	            alert("검색어를 입력하세요!");
	            return;
	        }

	        let url = `/api/consult?query=${encodeURIComponent(query)}`;
	        console.log("🔍 병해충 상담 요청 URL:", url);

	        try {
	            const response = await fetch(url);
	            if (!response.ok) throw new Error(`서버 응답 오류: ${response.status} ${response.statusText}`);

	            const data = await parseResponse(response);
	            let tableBody = document.querySelector("#consultResultTable tbody");
	            tableBody.innerHTML = "";

	            if (data instanceof Document) {
	                // XML 처리
	                const items = data.getElementsByTagName("item");
	                if (items.length === 0) {
	                    alert("검색 결과가 없습니다.");
	                    return;
	                }
	                for (let item of items) {
	                    let title = item.getElementsByTagName("title")[0]?.textContent || "정보 없음";
	                    let consultId = item.getElementsByTagName("consultId")[0]?.textContent || "정보 없음";
	                    let requestDate = item.getElementsByTagName("requestDate")[0]?.textContent || "정보 없음";

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
	            } else if (data.text) {
	                // TEXT 처리 (text/plain 대응)
	                console.warn("⚠ 서버에서 텍스트 응답을 반환함:", data.text);
	                alert("서버에서 텍스트 응답을 받았습니다: " + data.text);
	            } else {
	                // JSON 처리
	                if (!data.items || data.items.length === 0) {
	                    alert("검색 결과가 없습니다.");
	                    return;
	                }
	                data.items.forEach(item => {
	                    let row = `
	                        <tr>
	                            <td>${item.title || "정보 없음"}</td>
	                            <td>${item.consultId || "정보 없음"}</td>
	                            <td>${item.requestDate || "정보 없음"}</td>
	                            <td><button onclick="fetchConsultDetail('${item.consultId}')">상세보기</button></td>
	                        </tr>
	                    `;
	                    tableBody.innerHTML += row;
	                });
	            }

	        } catch (error) {
	            console.error("🔴 병해충 상담 검색 에러:", error);
	            alert("검색 중 오류가 발생했습니다.");
	        }
	    };

	    /**
	     * ✅ 병해충 상담 상세보기 기능 (JSON/XML/TEXT 대응)
	     */
	    window.fetchConsultDetail = async function (consultId) {
	        let url = `/api/consult_detail?consult_id=${consultId}`;
	        console.log("🔍 병해충 상담 상세보기 요청 URL:", url);

	        try {
	            const response = await fetch(url);
	            if (!response.ok) throw new Error(`서버 응답 오류: ${response.status} ${response.statusText}`);

	            const data = await parseResponse(response);
	            let html = "";

	            if (data instanceof Document) {
	                // XML 처리
	                let title = data.getElementsByTagName("title")[0]?.textContent || "정보 없음";
	                let requestContent = data.getElementsByTagName("requestContent")[0]?.textContent || "내용 없음";
	                let opinion = data.getElementsByTagName("opinion")[0]?.textContent || "의견 없음";

	                html = `
	                    <h2>${title}</h2>
	                    <p><strong>상담 요청 내용:</strong> ${requestContent}</p>
	                    <p><strong>전문가 의견:</strong> ${opinion}</p>
	                `;
	            } else if (data.text) {
	                // TEXT 처리 (text/plain 대응)
	                console.warn("⚠ 서버에서 텍스트 응답을 반환함:", data.text);
	                html = `<p>서버 응답: ${data.text}</p>`;
	            } else {
	                // JSON 처리
	                html = `
	                    <h2>${data.title || "정보 없음"}</h2>
	                    <p><strong>상담 요청 내용:</strong> ${data.requestContent || "내용 없음"}</p>
	                    <p><strong>전문가 의견:</strong> ${data.opinion || "의견 없음"}</p>
	                `;
	            }

	            document.getElementById("consultDetailContainer").innerHTML = html;
	            document.getElementById("consultDetailModal").style.display = "block";

	        } catch (error) {
	            console.error("🔴 병해충 상담 상세보기 에러:", error);
	        }
	    };
});
