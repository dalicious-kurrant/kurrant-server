package co.dalicious.domain.food.dto;

import co.dalicious.system.enums.DiningType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Schema(description = "메이커스 일일 가능 수량 DTO")
public class MakersCapacityDto {
    private Integer diningType;
    private Integer capacity;
}
