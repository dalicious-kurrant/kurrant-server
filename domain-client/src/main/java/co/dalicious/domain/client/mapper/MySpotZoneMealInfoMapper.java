package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.domain.client.entity.MySpotZoneMealInfo;
import org.mapstruct.Mapper;

import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MySpotZoneMealInfoMapper {

        default MySpotZoneMealInfo toMealInfo(Group group, DiningType diningType, LocalTime deliveryTime, String lastOrderTime, String useDays, String membershipBenefitTime) {
            // MealInfo 를 생성하기 위한 기본값이 존재하지 않으면 객체 생성 X
            if (lastOrderTime == null || useDays == null) {
                return null;
            }

            return MySpotZoneMealInfo.builder()
                    .group(group)
                    .diningType(diningType)
                    .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                    .deliveryTimes(List.of(deliveryTime))
                    .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                    .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                    .build();

        }

        default MySpotZoneMealInfo toMealInfo(Group group, DiningType diningType, List<LocalTime> deliveryTime, String lastOrderTime, String useDays, String membershipBenefitTime) {
            // MealInfo 를 생성하기 위한 기본값이 존재하지 않으면 객체 생성 X
            if (lastOrderTime == null || useDays == null) {
                return null;
            }

            return MySpotZoneMealInfo.builder()
                    .group(group)
                    .diningType(diningType)
                    .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                    .deliveryTimes(deliveryTime)
                    .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                    .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                    .build();

        }
}
