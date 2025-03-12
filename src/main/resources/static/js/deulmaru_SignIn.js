document.addEventListener("DOMContentLoaded", function () {
    flatpickr("#birthdateInput", {
        dateFormat: "Y/m/d", // YYYY/MM/DD 형식 적용
        minDate: "1900-01-01", // 최소 선택 날짜
        maxDate: "2005-12-31", // 최대 선택 날짜
        locale: "ko" // 한국어 적용
    });
});
