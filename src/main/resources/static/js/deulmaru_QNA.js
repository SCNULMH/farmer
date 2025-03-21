// ✅ 드롭다운 및 검색 이벤트 등록

document.addEventListener("DOMContentLoaded", function () {
  console.log("✅ deulmaru_QNA.js 로드 완료");

  // 드롭다운 항목 클릭 시 선택값 업데이트
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

  // 기본값 세팅 (제목 + "벼")
  const dropdownButton = document.getElementById("dropdownMenuButton");
  dropdownButton.textContent = "제목";
  dropdownButton.setAttribute("data-search-type", "dgnssReqSj");

  const consultQuery = document.getElementById("consultQuery");
  consultQuery.value = "";
  consultQuery.placeholder = "검색어를 입력하세요!";

  fetchConsultData("벼");

  // 검색창 엔터 입력 시 검색 실행
  consultQuery.addEventListener("keypress", function (event) {
    if (event.key === "Enter") {
      fetchConsultData();
    }
  });
});

// ✅ 병해충 상담 검색 요청
let currentPage = 1;
const resultsPerPage = 10;
let totalResults = 0;
let cachedItems = [];

window.fetchConsultData = function (customQuery) {
  const query = customQuery || document.getElementById("consultQuery").value;
  if (!query) {
    alert("검색어를 입력하세요!");
    return;
  }

  const searchType = document.getElementById("dropdownMenuButton").getAttribute("data-search-type") || "dgnssReqSj";
  const paramKey = searchType;
  const url = `http://localhost:8082/ncpms/consult?query=${encodeURIComponent(query)}&type=${paramKey}&page=1`;

  fetch(url)
    .then(response => response.text())
    .then(str => new window.DOMParser().parseFromString(str, "application/xml"))
    .then(data => {
      const items = data.getElementsByTagName("item");
      cachedItems = Array.from(items);
      totalResults = cachedItems.length;
      currentPage = 1;
      displayCurrentPage();
    })
    .catch(error => {
      console.error("🔴 병해충 상담 에러:", error);
    });
};

function displayCurrentPage() {
  const tableBody = document.querySelector("#consultResultTable tbody");
  tableBody.innerHTML = "";
  const startIdx = (currentPage - 1) * resultsPerPage;
  const endIdx = startIdx + resultsPerPage;
  const pageItems = cachedItems.slice(startIdx, endIdx);

  pageItems.forEach(item => {
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
  });

  updatePagination();
}

function updatePagination() {
  const pageInfo = document.getElementById("pageInfo");
  const pageNumbers = document.getElementById("pageNumbers");

  const totalPages = Math.ceil(totalResults / resultsPerPage);
  pageInfo.textContent = `페이지 ${currentPage} / ${totalPages}`;

  pageNumbers.innerHTML = "";
  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    btn.className = "btn btn-sm btn-outline-dark mx-1" + (i === currentPage ? " active" : "");
    btn.onclick = () => {
      currentPage = i;
      displayCurrentPage();
    };
    pageNumbers.appendChild(btn);
  }
}

window.changePage = function (delta) {
  const totalPages = Math.ceil(totalResults / resultsPerPage);
  const newPage = currentPage + delta;
  if (newPage >= 1 && newPage <= totalPages) {
    currentPage = newPage;
    displayCurrentPage();
  }
};

// ✅ 상세보기 모달 열기
window.fetchConsultDetail = function (consultId) {
  if (!consultId || consultId.trim() === "") return;
  let url = `http://localhost:8082/ncpms/consult_detail?consult_id=${consultId}`;

  fetch(url)
    .then(response => response.text())
    .then(data => {
      let parser = new DOMParser();
      let xmlDoc = parser.parseFromString(data, "application/xml");
      let title = xmlDoc.getElementsByTagName("dgnssReqSj")[0]?.textContent || "정보 없음";
      let requestContent = xmlDoc.getElementsByTagName("reqestCn")[0]?.textContent || "상담 요청 내용 없음";
      let opinion = xmlDoc.getElementsByTagName("dgnssOpin")[0]?.textContent || "전문가 의견 없음";

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

      let html = `
        <div class="consult-detail-container">
          <h2 class="consult-title">${title}</h2>
          <p><strong>상담 요청 내용:</strong> <span class="consult-content">${requestContent}</span></p>
          <p><strong>전문가 의견:</strong> <span class="consult-opinion">${opinion}</span></p>
          <div class="consult-images">${imageHtml}</div>
        </div>`;

      document.getElementById("consultDetailContainer").innerHTML = html;
      document.getElementById("consultDetailModal").style.display = "block";
    })
    .catch(error => {
      console.error("🔴 병해충 상담 상세보기 에러:", error);
    });
};

// 이미지 클릭 시 모달로 확대보기
window.showImageModal = function (imageSrc) {
  const modal = document.getElementById("imageModal");
  const modalImage = document.getElementById("modalImage");
  modalImage.src = imageSrc;
  modal.style.display = "block";
};

function closeImageModal() {
  document.getElementById("imageModal").style.display = "none";
}

window.closeModal = function () {
  document.getElementById("consultDetailModal").style.display = "none";
};

// ✅ ESC 키로 모달 닫기
window.addEventListener("keydown", function (e) {
  if (e.key === "Escape") {
    const imageModal = document.getElementById("imageModal");
    const detailModal = document.getElementById("consultDetailModal");
    if (imageModal && imageModal.style.display === "block") {
      closeImageModal();
    } else if (detailModal && detailModal.style.display === "block") {
      closeModal();
    }
  }
  
  
  
  
  
  
  
       // AI 챗봇 토글
       function toggleChatbot() {
      var chatbot = document.getElementById("chatbot");

      // 챗봇 창이 열려있으면 숨기고, 닫혀있으면 표시
      if (chatbot.style.display === "none" || chatbot.style.display === "") {
          chatbot.style.display = "block";
      } else {
          chatbot.style.display = "none";
      }
  }
});