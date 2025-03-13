document.addEventListener("DOMContentLoaded", function () {
    flatpickr("#birthdateInput", {
        dateFormat: "Y/m/d", // YYYY/MM/DD 형식 적용
        minDate: "1900-01-01", // 최소 선택 날짜
        maxDate: "2005-12-31", // 최대 선택 날짜
        locale: "ko" // 한국어 적용
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("form");

    form.addEventListener("submit", function (event) {
        const userId = document.getElementById("userId").value;
        const userPw = document.getElementById("userPw").value;
        const userNickname = document.getElementById("userNickname").value;
        const userEmail = document.getElementById("userEmail").value;
        const userLocate = document.getElementById("userLocate").value;
        const userBirth = document.getElementById("userBirth").value;
        const userGender = document.getElementById("userGender").value;

        if (!userId || !userPw || !userNickname || !userEmail || !userLocate || !userBirth || !userGender) {
            alert("모든 필드를 입력해주세요!");
            event.preventDefault();
        }
    });
});
