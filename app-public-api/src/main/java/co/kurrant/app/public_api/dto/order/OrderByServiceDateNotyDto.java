package co.kurrant.app.public_api.dto.order;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DaysUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.parameters.P;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderByServiceDateNotyDto {
    private DiningType type;
    private List<String> serviceDays;
    private DayAndTime lastOrderTime;
    private DayAndTime membershipBenefitTime;

    public static OrderByServiceDateNotyDto createOrderByServiceDateNotyDto(MealInfo mealInfo) {
        return OrderByServiceDateNotyDto.builder()
                .type(mealInfo.getDiningType())
                .lastOrderTime(mealInfo.getLastOrderTime())
                .serviceDays(DaysUtil.serviceDaysToDaysStringList(mealInfo.getServiceDays()))
                .membershipBenefitTime(mealInfo.getMembershipBenefitTime())
                .build();

    }
}
