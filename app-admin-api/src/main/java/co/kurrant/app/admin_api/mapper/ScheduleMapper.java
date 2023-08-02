package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.dto.CapacityDto;
import co.dalicious.domain.order.dto.ServiceDiningDto;
import co.dalicious.domain.order.dto.ServiceDateBy;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.ScheduleDto;
import org.mapstruct.Mapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalTime;
import java.util.*;

@Mapper(componentModel = "spring", imports = {DateUtils.class})
public interface ScheduleMapper {
    default List<ScheduleDto.GroupSchedule> toGroupSchedule(List<DailyFood> dailyFoods, Map<DailyFood, Integer> dailyFoodMap,
                                                            ServiceDateBy.MakersAndFood makersCapacities, Map<Group, Integer> userGroupCount) {
        MultiValueMap<ServiceDiningDto, DailyFood> dailyFoodByServiceMap = new LinkedMultiValueMap<>();
        // 식사 날짜/식사 일정 별로 식단 묶기
        for (DailyFood dailyFood : dailyFoods) {
            ServiceDiningDto serviceDiningDto = new ServiceDiningDto(dailyFood.getServiceDate(), dailyFood.getDiningType());
            dailyFoodByServiceMap.add(serviceDiningDto, dailyFood);
        }
        List<ScheduleDto.GroupSchedule> groupSchedules = new ArrayList<>();
        for (ServiceDiningDto serviceDiningDto : dailyFoodByServiceMap.keySet()) {
            // 식사 날짜/식사 일정 > 그룹별로 식단 묶기
            MultiValueMap<Group, DailyFood> groupDailyFoodMap = new LinkedMultiValueMap<>();
            List<DailyFood> serviceDailyFoods = dailyFoodByServiceMap.get(serviceDiningDto);
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

                List<LocalTime> deliveryTimes = group.getMealInfo(groupDailyFoods.get(0).getDiningType()).getDeliveryTimes();

                List<ScheduleDto.MakersSchedule> makersSchedules = new ArrayList<>();
                for (Makers makers : makersDailyFoodMap.keySet()) {
                    List<ScheduleDto.FoodSchedule> foodSchedules = new ArrayList<>();
                    List<DailyFood> makersDailyFoods = makersDailyFoodMap.get(makers);
                    Integer makersCount = makersCapacities.getMakersCount(makersDailyFoods.get(0));

                    List<LocalTime> makersPickupTimes = makersDailyFoods.get(0).getDailyFoodGroup().getDeliverySchedules().stream().map(DeliverySchedule::getPickupTime).toList();

                    for (DailyFood makersDailyFood : makersDailyFoods) {
                        Integer count = dailyFoodMap.get(makersDailyFood);
                        ScheduleDto.FoodSchedule foodSchedule = toFoodSchedule(makersDailyFood, count);
                        foodSchedules.add(foodSchedule);
                    }
                    ScheduleDto.MakersSchedule makersSchedule = toMakersSchedule(makers, serviceDiningDto.getDiningType(), makersPickupTimes, makersCount, foodSchedules);
                    makersSchedules.add(makersSchedule);
                }

                ScheduleDto.GroupSchedule groupSchedule = new ScheduleDto.GroupSchedule();
                groupSchedule.setServiceDate(DateUtils.format(serviceDiningDto.getServiceDate()));
                groupSchedule.setDiningType(serviceDiningDto.getDiningType().getCode());
                groupSchedule.setGroupName(group.getName());
                groupSchedule.setGroupCapacity(userGroupCount.get(group));
                groupSchedule.setDeliveryTime(DateUtils.timesToStringList(deliveryTimes));
                groupSchedule.setMakersSchedules(makersSchedules);

                groupSchedules.add(groupSchedule);
            }

        }
        return groupSchedules;
    }

    default Integer getMakersCount(Makers makers, List<CapacityDto.MakersCapacity> makersCapacityList) {
        Optional<CapacityDto.MakersCapacity> makersCapacityOptional = makersCapacityList.stream()
                .filter(v -> v.getMakers().equals(makers))
                .findAny();
        if (makersCapacityOptional.isEmpty()) return null;
        return makersCapacityOptional.get().getCapacity();
    }

//    default String getEarliestDeliveryTime(Group group, DiningType diningType) {
//        List<Spot> spots = group.getSpots();
//        LocalTime earliestDeliveryTime = LocalTime.MAX; // initialize to a value later than any possible delivery time
//
//        for (Spot spot : spots) {
//            LocalTime deliveryTime = spot.getDeliveryTime(diningType);
//            if(deliveryTime == null) {
//                return null;
//            }
//            if (deliveryTime.isBefore(earliestDeliveryTime)) {
//                earliestDeliveryTime = deliveryTime;
//            }
//        }
//
//        return DateUtils.timeToString(earliestDeliveryTime);
//    }

    default ScheduleDto.FoodSchedule toFoodSchedule(DailyFood dailyFood, Integer count) {
        ScheduleDto.FoodSchedule foodSchedule = new ScheduleDto.FoodSchedule();
        foodSchedule.setDailyFoodId(dailyFood.getId());
        foodSchedule.setFoodCapacity(dailyFood.getFood().getFoodCapacity(dailyFood.getDiningType()) == null ?
                dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType()).getCapacity() :
                dailyFood.getFood().getFoodCapacity(dailyFood.getDiningType()).getCapacity()
        );
        foodSchedule.setFoodName(dailyFood.getFood().getName());
        foodSchedule.setDailyFoodStatus(dailyFood.getDailyFoodStatus().getCode());
        foodSchedule.setFoodCount(count);
        return foodSchedule;
    }

    default ScheduleDto.MakersSchedule toMakersSchedule(Makers makers, DiningType diningType, List<LocalTime> makersPickupTime, Integer makersCount, List<ScheduleDto.FoodSchedule> foodSchedules) {
        ScheduleDto.MakersSchedule makersSchedule = new ScheduleDto.MakersSchedule();
        makersSchedule.setMakersName(makers.getName());
        makersSchedule.setMakersCapacity(makers.getMakersCapacity(diningType).getCapacity()); //TODO: 설정 필요
        makersSchedule.setMakersCount(makersCount);
        makersSchedule.setMakersPickupTime(makersPickupTime == null || makersPickupTime.isEmpty() ? Collections.singletonList("00:00") : DateUtils.timesToStringList(makersPickupTime));
        makersSchedule.setFoodSchedules(foodSchedules);
        return makersSchedule;
    }

}
