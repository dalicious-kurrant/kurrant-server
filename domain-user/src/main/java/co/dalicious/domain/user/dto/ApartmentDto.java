package co.dalicious.domain.user.dto;

import co.dalicious.system.util.DiningType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ApartmentDto {
    String name;
    Integer familyCount;
    DiningType diningType;
    String deliveryTime;

    @Builder
    public ApartmentDto(String name, Integer familyCount, DiningType diningType, String deliveryTime) {
        this.name = name;
        this.familyCount = familyCount;
        this.diningType = diningType;
        this.deliveryTime = deliveryTime;
    }
}
