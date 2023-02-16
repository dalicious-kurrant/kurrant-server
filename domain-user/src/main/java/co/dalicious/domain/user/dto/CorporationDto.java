package co.dalicious.domain.user.dto;

import co.dalicious.system.enums.DiningType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Schema(description = "기업 생성 요청 DTO")
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
