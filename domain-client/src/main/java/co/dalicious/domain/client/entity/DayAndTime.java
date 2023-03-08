package co.dalicious.domain.client.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
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
