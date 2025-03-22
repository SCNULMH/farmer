document.addEventListener("DOMContentLoaded", function() {
    const alarmIcon = document.getElementById("alarmIcon");
    const alarmList = document.getElementById("alarmList");

    // alarmIcon이 존재할 때만 이벤트 등록
    if (alarmIcon && alarmList) {
        alarmIcon.addEventListener("click", function() {
            // 알림 목록 API 호출
            fetch("/api/interest/list")
                .then(response => response.json())
                .then(data => {
                    const alarms = data.filter(item => item.notifyYn === true);
                    renderAlarmList(alarms);

                    // 드롭다운 토글
                    if (alarmList.style.display === "none" || alarmList.style.display === "") {
                        alarmList.style.display = "block";
                    } else {
                        alarmList.style.display = "none";
                    }
                })
                .catch(error => console.error("알림 목록 조회 오류:", error));
        });

        function renderAlarmList(alarms) {
            alarmList.innerHTML = "";
            if (alarms.length === 0) {
                alarmList.innerHTML = "<p style='padding:10px;'>새로운 알림이 없습니다.</p>";
                return;
            }
            alarms.forEach(alarm => {
                const item = document.createElement("div");
                item.style.padding = "10px";
                item.style.borderBottom = "1px solid #ccc";
                item.style.cursor = "pointer";
                item.textContent = `지원금(${alarm.title}) 마감이 임박했습니다.`;
                item.addEventListener("click", function() {
                    window.location.href = `/supportApi/detail/${alarm.grantId}`;
                });
                alarmList.appendChild(item);
            });
        }
    } else {
        console.log("알림 아이콘 또는 알림 목록 요소가 존재하지 않음 (비로그인 상태일 수 있음)");
    }
});
