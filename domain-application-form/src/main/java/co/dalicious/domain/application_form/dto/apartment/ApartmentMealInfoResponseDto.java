package co.dalicious.domain.application_form.dto.apartment;

import co.dalicious.domain.application_form.entity.ApartmentApplicationMealInfo;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "아파트 식사정보 응답 DTO")
@Getter
@Setter
public class ApartmentMealInfoResponseDto {
    private String diningType;
    private Integer expectedUserCount;
    private String serviceDays;
    private String deliveryTime;
}
