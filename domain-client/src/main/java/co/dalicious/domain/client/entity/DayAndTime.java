package co.dalicious.domain.client.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class DayAndTime {
    private Integer day;
    private LocalTime time;

    public DayAndTime(Integer day, LocalTime time) {
        this.day = day;
        this.time = time;
    }
}
