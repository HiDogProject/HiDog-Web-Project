package org.hidog.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component("timeUtils")
@RequiredArgsConstructor
public class TimeUtils {

    public static String formatRelativeTime(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);

        if (duration.toDays() >= 7) {
            // 1주일 이상인 경우 날짜로 표시
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
            return createdAt.format(formatter);
        } else if (duration.toDays() > 0) {
            return duration.toDays() + "일 전";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + "시간 전";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + "분 전";
        } else {
            return "방금 전";
        }
    }
}
