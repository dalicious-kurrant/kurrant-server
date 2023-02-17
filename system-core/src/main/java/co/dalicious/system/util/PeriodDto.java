package co.dalicious.system.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class PeriodDto {
    private final LocalDate startDate;
    private final LocalDate endDate;

    @Builder
    public PeriodDto(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
