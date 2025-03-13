document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll(".tab-button");
    const contents = document.querySelectorAll(".tab-content");

    buttons.forEach(button => {
        button.addEventListener("click", function () {
            // 모든 버튼의 active 클래스 제거
            buttons.forEach(btn => btn.classList.remove("active"));
            this.classList.add("active");

            // 모든 콘텐츠 숨기기
            contents.forEach(content => content.classList.add("hidden"));

            // 선택된 탭의 콘텐츠 보이기
            const targetId = this.getAttribute("data-tab");
            document.getElementById(targetId).classList.remove("hidden");
        });
    });
});




// 들마루 마이페이지에서의 탭 메뉴 활성화 함수
document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll(".tab-button");
    const contents = document.querySelectorAll(".tab-content");

    buttons.forEach(button => {
        button.addEventListener("click", function () {
            // 모든 버튼의 active 클래스 제거
            buttons.forEach(btn => btn.classList.remove("active"));
            this.classList.add("active");

            // 모든 콘텐츠 숨기기
            contents.forEach(content => content.classList.add("hidden"));

            // 선택된 탭의 콘텐츠 보이기
            const targetId = this.getAttribute("data-tab");
            document.getElementById(targetId).classList.remove("hidden");
        });
    });

    // 초기에 '내 프로필' 탭만 표시되도록 설정
    contents.forEach(content => content.classList.add("hidden"));
    document.getElementById("profile").classList.remove("hidden");
});
