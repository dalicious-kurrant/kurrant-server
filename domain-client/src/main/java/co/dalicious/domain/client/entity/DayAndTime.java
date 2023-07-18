package co.dalicious.domain.client.entity;

import co.dalicious.system.util.DateUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
public class DayAndTime {
    private Integer day;
    private LocalTime time;

    public DayAndTime(Integer day, LocalTime time) {
        this.day = day;
        this.time = time;
    }

    public String dayAndTimeToString() {
        String timeStr = this.getTime().toString();
        if (this.getDay() == null) {
            return "0일전 " + timeStr;
        } else {
            return this.getDay() + "일전 " + timeStr;
        }
    }

    public String dayAndTimeToStringByDate(LocalDate date) {
        String timeStr = this.getTime().toString();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd");

        if (this.getDay() == null) {
            return date.format(dateTimeFormatter) + " " + timeStr;
        } else {
            return date.minusDays(this.getDay()).format(dateTimeFormatter) + " " + timeStr;
        }
    }

    public static String dayAndTimeToString(DayAndTime dayAndTime) {
        if(dayAndTime == null) {
            return "정보 없음";
        }
        else {
            String timeStr = dayAndTime.getTime().toString();
            if (dayAndTime.getDay() == null) {
                return "0일전 " + timeStr;
            } else {
                return dayAndTime.getDay() + "일전 " + timeStr;
            }
        }
    }

    public static DayAndTime stringToDayAndTime(String str) {
        if (str == null || str.equals("정보 없음")) {
            return null;
        }
        else {
            String[] parts = str.split("일전 ");
            Integer day = 0;
            LocalTime time;
            if (parts.length == 1) {
                time = LocalTime.parse(parts[0]);
            } else {
                day = Integer.parseInt(parts[0]);
                time = LocalTime.parse(parts[1]);
            }
            return new DayAndTime(day, time);
        }
    }

    public static LocalDate toLocalDate(DayAndTime dayAndTime){
        return LocalDate.now().minusDays(dayAndTime.getDay());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayAndTime that = (DayAndTime) o;
        return Objects.equals(day, that.day) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, time);
    }
}
