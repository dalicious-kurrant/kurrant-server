package co.kurrant.app.public_api.dto.order;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.system.enums.DiningType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderByServiceDateNotyDto {
    private DiningType type;
    private List<String> serviceDays;
    private LocalTime lastOrderTime;
    private DayAndTime membershipBenefitTime;

    public static OrderByServiceDateNotyDto createOrderByServiceDateNotyDto(MealInfo mealInfo) {
        return OrderByServiceDateNotyDto.builder()
                .type(mealInfo.getDiningType())
                .lastOrderTime(mealInfo.getLastOrderTime())
                .serviceDays(List.of(mealInfo.getServiceDays().split(", ")))
                .membershipBenefitTime(mealInfo.getMembershipBenefitTime())
                .build();

    }
}
