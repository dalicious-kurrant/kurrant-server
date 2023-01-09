package co.dalicious.domain.application_form.dto.apartment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "아파트 식사 정보 요청 DTO")
@Getter
@Setter
public class ApartmentMealInfoRequestDto {
    private Integer diningType;
    private Integer expectedUserCount;
    private List<Integer> serviceDays;
    private String deliveryTime;
}
