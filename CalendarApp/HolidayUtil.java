package CalendarApp;

import java.time.LocalDate;
import java.time.MonthDay;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.LocalDate;

public class HolidayUtil {
    public static boolean isHoliday(LocalDate date) {
        MonthDay md = MonthDay.from(date);
        int year = date.getYear();

        // 簡易的に一部の祝日をハードコーディング
        return md.equals(MonthDay.of(1, 1)) || // 元日
               md.equals(MonthDay.of(2, 11)) || // 建国記念の日
               md.equals(MonthDay.of(4, 29)) || // 昭和の日
               md.equals(MonthDay.of(5, 3)) ||  // 憲法記念日
               md.equals(MonthDay.of(5, 4)) ||  // みどりの日
               md.equals(MonthDay.of(5, 5)) ||  // こどもの日
               md.equals(MonthDay.of(11, 3)) || // 文化の日
               md.equals(MonthDay.of(11, 23));  // 勤労感謝の日
    }
}
