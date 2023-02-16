package co.dalicious.domain.user.dto;

import co.dalicious.system.enums.DiningType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Schema(description = "아파트 생성 요청 DTO")
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
