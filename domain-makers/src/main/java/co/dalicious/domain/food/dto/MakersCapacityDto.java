package co.dalicious.domain.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "메이커스 일일 가능 수량 DTO")
public class MakersCapacityDto {
    private String lastOrderTime;
    private Integer diningType;
    private Integer capacity;
}
