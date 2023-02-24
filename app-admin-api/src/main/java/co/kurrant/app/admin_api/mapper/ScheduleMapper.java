package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.dto.DiningTypeServiceDateDto;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.ScheduleDto;
import org.mapstruct.Mapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", imports = {DateUtils.class})
public interface ScheduleMapper {
    default List<ScheduleDto.GroupSchedule> toGroupSchedule(List<DailyFood> dailyFoods, Map<DailyFood, Integer> dailyFoodMap) {
        MultiValueMap<DiningTypeServiceDateDto, DailyFood> dailyFoodByServiceMap = new LinkedMultiValueMap<>();
        // 식사 날짜/식사 일정 별로 식단 묶기
        for (DailyFood dailyFood : dailyFoods) {
            DiningTypeServiceDateDto diningTypeServiceDateDto = new DiningTypeServiceDateDto(dailyFood.getServiceDate(), dailyFood.getDiningType());
            dailyFoodByServiceMap.add(diningTypeServiceDateDto, dailyFood);
        }
        List<ScheduleDto.GroupSchedule> groupSchedules = new ArrayList<>();
        for (DiningTypeServiceDateDto diningTypeServiceDateDto : dailyFoodByServiceMap.keySet()) {


            // 식사 날짜/식사 일정 > 그룹별로 식단 묶기
            MultiValueMap<Group, DailyFood> groupDailyFoodMap = new LinkedMultiValueMap<>();
            List<DailyFood> serviceDailyFoods = dailyFoodByServiceMap.get(diningTypeServiceDateDto);
            for (DailyFood serviceDailyFood : serviceDailyFoods) {
                groupDailyFoodMap.add(serviceDailyFood.getGroup(), serviceDailyFood);
            }

            for (Group group : groupDailyFoodMap.keySet()) {
                // 식사 날짜/식사 일정 > 그룹별 > 메이커스별로 식단 묶기
                MultiValueMap<Makers, DailyFood> makersDailyFoodMap = new LinkedMultiValueMap<>();
                List<DailyFood> groupDailyFoods = groupDailyFoodMap.get(group);
                for (DailyFood groupDailyFood : groupDailyFoods) {
                    makersDailyFoodMap.add(groupDailyFood.getFood().getMakers(), groupDailyFood);
                }

                List<ScheduleDto.MakersSchedule> makersSchedules = new ArrayList<>();
                for (Makers makers : makersDailyFoodMap.keySet()) {
                    List<ScheduleDto.FoodSchedule> foodSchedules = new ArrayList<>();
                    List<DailyFood> makersDailyFoods = makersDailyFoodMap.get(makers);
                    for (DailyFood makersDailyFood : makersDailyFoods) {
                        Integer count = dailyFoodMap.get(makersDailyFood);
                        ScheduleDto.FoodSchedule foodSchedule = toFoodSchedule(makersDailyFood, count);
                        foodSchedules.add(foodSchedule);
                    }
                    ScheduleDto.MakersSchedule makersSchedule = toMakersSchedule(makers, diningTypeServiceDateDto.getDiningType(), foodSchedules);
                    makersSchedules.add(makersSchedule);
                }
                ScheduleDto.GroupSchedule groupSchedule = new ScheduleDto.GroupSchedule();
                groupSchedule.setServiceDate(DateUtils.format(diningTypeServiceDateDto.getServiceDate()));
                groupSchedule.setDiningType(diningTypeServiceDateDto.getDiningType().getCode());
                groupSchedule.setGroupId(group.getId());
                groupSchedule.setGroupCapacity(100); //TODO: 설정 필요
                groupSchedule.setDeliveryTime("9:00"); //TODO: 스팟 배송이 가장 빠른 배송시간
                groupSchedule.setMakersSchedules(makersSchedules);

                groupSchedules.add(groupSchedule);
            }
        }
        return groupSchedules;
    };

    default ScheduleDto.FoodSchedule toFoodSchedule(DailyFood dailyFood, Integer count) {
        ScheduleDto.FoodSchedule foodSchedule = new ScheduleDto.FoodSchedule();
        foodSchedule.setFoodId(dailyFood.getFood().getId());
        foodSchedule.setFoodName(dailyFood.getFood().getName());
        foodSchedule.setFoodStatus(dailyFood.getFood().getFoodStatus().getCode());
        foodSchedule.setFoodCount(count);
        return foodSchedule;
    }

    default ScheduleDto.MakersSchedule toMakersSchedule(Makers makers, DiningType diningType, List<ScheduleDto.FoodSchedule> foodSchedules) {
        ScheduleDto.MakersSchedule makersSchedule = new ScheduleDto.MakersSchedule();
        makersSchedule.setMakersId(makers.getId());
        makersSchedule.setMakersName(makers.getName());
        makersSchedule.setMakersCapacity(makers.getMakersCapacity(diningType).getCapacity()); //TODO: 설정 필요
        makersSchedule.setMakersCount(makers.getMakersCapacity(diningType).getCapacity()); //TODO: 설정 필요
        makersSchedule.setMakersPickupTime("08:00"); //TODO: 설정 필요
        makersSchedule.setFoodSchedules(foodSchedules);
        return makersSchedule;
    }

}
