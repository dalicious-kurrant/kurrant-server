package co.dalicious.domain.user.dto;

import co.dalicious.system.util.DiningType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CorporationDto {
    String name;
    Integer employeeCount;
    DiningType diningType;
    String deliveryTime;

    @Builder

    public CorporationDto(String name, Integer employeeCount, DiningType diningType, String deliveryTime) {
        this.name = name;
        this.employeeCount = employeeCount;
        this.diningType = diningType;
        this.deliveryTime = deliveryTime;
    }
}
