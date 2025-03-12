document.addEventListener("DOMContentLoaded", function(){
  console.log("✅ deulmaru_dictionary_search.js 로드 완료");

  // 드롭다운 메뉴 항목 클릭 시 선택값 업데이트
  var dropdownItems = document.querySelectorAll('#searchTypeDropdown + .dropdown-menu .dropdown-item');
  dropdownItems.forEach(function(item){
    item.addEventListener("click", function(e){
      e.preventDefault();
      var selectedValue = this.getAttribute("data-value");
      var selectedText = this.textContent;
      var dropdownButton = document.getElementById("searchTypeDropdown");
      dropdownButton.textContent = selectedText;
      dropdownButton.setAttribute("data-value", selectedValue);
    });
  });

  // 검색 입력창 엔터키 이벤트 등록
  const searchQueryElem = document.getElementById("searchQuery");
  if (searchQueryElem) {
    searchQueryElem.addEventListener("keypress", function(event) {
      if (event.key === "Enter") {
        fetchSearchData();
      }
    });
  }
});

// 병해충 검색 요청 함수
window.fetchSearchData = function() {
  let query = document.getElementById("searchQuery").value;
  let searchType = document.getElementById("searchTypeDropdown").getAttribute("data-value") || "sick";

  if (!query) {
    alert("검색어를 입력하세요!");
    return;
  }

  let url = `http://localhost:8082/ncpms/search?query=${encodeURIComponent(query)}&type=${searchType}`;
  console.log("🔍 병해충 검색 요청 URL:", url);

  fetch(url)
    .then(response => response.text())
    .then(str => new window.DOMParser().parseFromString(str, "application/xml"))
    .then(data => {
      let resultContainer = document.getElementById("resultTable");
      resultContainer.innerHTML = "";  // 기존 카드 리스트 초기화

      let items = data.getElementsByTagName("item");

      for (let item of items) {
        let cropName = item.getElementsByTagName("cropName")[0]?.textContent || "정보 없음";
        let sickNameKor = item.getElementsByTagName("sickNameKor")[0]?.textContent || "정보 없음";
        let sickNameEng = item.getElementsByTagName("sickNameEng")[0]?.textContent || "정보 없음";
        let sickNameChn = item.getElementsByTagName("sickNameChn")[0]?.textContent || "정보 없음";
        let thumbImg = item.getElementsByTagName("thumbImg")[0]?.textContent || "";
        let sickKey = item.getElementsByTagName("sickKey")[0]?.textContent || "";
		

        // 카드 HTML 생성
        let cardHtml = `
          <a href="#" class="col-md-4 col-sm-6 mb-4" onclick="fetchSickDetail('${sickKey}')">
            <div class="card dictionary-card">
                <img src="${thumbImg}" class="card-img-top" alt="${sickNameKor}">
                <div class="card-body">
                    <h3 class="insect-ttl">${sickNameKor}</h3>
                    <h4 class="crop-type">${cropName}</h4>
                    <p class="insect-ttl-eng">${sickNameEng}</p>
                    <p class="insect-ttl-chn">${sickNameChn}</p>
                </div>
            </div>
          </a>
        `;

        // 카드 HTML을 결과 컨테이너에 추가
        resultContainer.innerHTML += cardHtml;
      }

      // 검색 결과 영역 보이기
      document.getElementById("dictionary").classList.remove("hidden");
    })
    .catch(error => {
      console.error("🔴 병해충 검색 에러:", error);
    });
};

// 병해충 상세보기 요청 함수 (모달로 표시)
window.fetchSickDetail = function(sickKey) {
    if (!sickKey || sickKey.trim() === "") {
        console.error("유효하지 않은 sickKey:", sickKey);
        return;
    }

    let url = `http://localhost:8082/ncpms/sick_detail?sick_key=${encodeURIComponent(sickKey)}`;
    console.log("🔍 병해충 상세보기 요청 URL:", url);

    fetch(url)
      .then(response => response.text())
      .then(xmlText => {
          // XML 파싱
          let xmlDoc = new window.DOMParser().parseFromString(xmlText, "application/xml");

          // XML에서 필요한 정보 추출
          let cropName = xmlDoc.getElementsByTagName("cropName")[0]?.textContent || "정보 없음";
          let sickNameKor = xmlDoc.getElementsByTagName("sickNameKor")[0]?.textContent || "정보 없음";
          let sickNameChn = xmlDoc.getElementsByTagName("sickNameChn")[0]?.textContent || "정보 없음";
          let preventionMethod = xmlDoc.getElementsByTagName("preventionMethod")[0]?.textContent || "정보 없음";
          let developmentCondition = xmlDoc.getElementsByTagName("developmentCondition")[0]?.textContent || "정보 없음";
          let symptoms = xmlDoc.getElementsByTagName("symptoms")[0]?.textContent || "정보 없음";

          // 관련 이미지 처리
          let virusImgList = xmlDoc.getElementsByTagName("virusImgList")[0];
          let virusImagesHtml = "";
          if (virusImgList) {
              let items = virusImgList.getElementsByTagName("item");
              for (let i = 0; i < items.length; i++) {
                  let imageUrl = items[i].getElementsByTagName("image")[0]?.textContent || "";
                  let imageTitle = items[i].getElementsByTagName("imageTitle")[0]?.textContent || "";
                  if (imageUrl) {
                      virusImagesHtml += `<div style="display:inline-block; margin:10px; text-align:center;">
                          <img src="${imageUrl}" alt="${imageTitle || '병해충 이미지'}" style="max-width:200px;">
                          <div>${imageTitle}</div>
                      </div>`;
                  }
              }
          }

          // HTML 내용 구성
          let detailHtml = `
              <h3>${sickNameKor} (${sickNameChn})</h3>
              <p><strong>작물명:</strong> ${cropName}</p>
              <p><strong>예방 방법:</strong> ${preventionMethod}</p>
              <p><strong>발병 조건:</strong> ${developmentCondition}</p>
              <p><strong>증상:</strong> ${symptoms}</p>
              <div><strong>관련 이미지:</strong><br>${virusImagesHtml}</div>
          `;

          // 모달에 상세 정보 삽입 후 표시
          document.getElementById("sickDetailContainer").innerHTML = detailHtml;
          document.getElementById("sickDetailModal").style.display = "block";
      })
      .catch(error => {
          console.error("🔴 상세보기 에러:", error);
      });
};

// 모달 닫기 함수
function closeSickDetailModal() {
    document.getElementById("sickDetailModal").style.display = "none";
}
