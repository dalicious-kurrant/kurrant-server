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

    @Getter
    @Setter
    public static class PeriodStringDto {
        private final String startDate;
        private final String endDate;

        public PeriodStringDto(String startDate, String endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public PeriodDto toPeriodDto() {
            return  PeriodDto.builder()
                    .startDate(DateUtils.stringToDate(this.startDate))
                    .endDate(DateUtils.stringToDate(this.endDate))
                    .build();
        }
    }
}
