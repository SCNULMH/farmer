    document.addEventListener("DOMContentLoaded", function() {
        const alarmIcon = document.getElementById("alarmIcon");
        const alarmList = document.getElementById("alarmList");

        alarmIcon.addEventListener("click", function() {
            // 알림 목록 API 호출: 로그인 후 /api/interest/list가 호출되어 notifyYn 업데이트
            fetch("/api/interest/list")
                .then(response => response.json())
                .then(data => {
                    // notifyYn이 true인 항목만 필터링
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
                // 예시 문구: grantId를 기반으로 상세 페이지 이동
                item.textContent = `지원금(${alarm.title}) 마감이 임박했습니다.`;
                item.addEventListener("click", function() {
                    window.location.href = `/supportApi/detail/${alarm.grantId}`;
                });
                alarmList.appendChild(item);
            });
        }
    });
