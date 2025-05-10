package cn.magic.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PastDate {
    public static List<String> getPastDays(int num) {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // i = 0 包括今天  i = 1 从昨天开始
        for (int i = 0; i <= num; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(formatter));
        }
        return dates;
    }
}
