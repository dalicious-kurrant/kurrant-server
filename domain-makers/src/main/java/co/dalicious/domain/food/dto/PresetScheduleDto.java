package co.dalicious.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PresetScheduleDto {
    private String serviceDate;
    private String diningType;
    private String makersName;
    private String clientName;
}
