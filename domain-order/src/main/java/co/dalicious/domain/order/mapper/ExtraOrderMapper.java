package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.dto.DiningTypeServiceDateDto;
import co.dalicious.domain.order.dto.ExtraOrderDto;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ExtraOrderMapper {
    default List<ExtraOrderDto.DailyFoodList> toDailyFoodList(List<DailyFood> dailyFoods) {
        // 1. 식사일정별로 묶는다.
        MultiValueMap<DiningTypeServiceDateDto, DailyFood> dailyFoodMap = new LinkedMultiValueMap<>();

        for (DailyFood dailyFood : dailyFoods) {
            DiningTypeServiceDateDto diningTypeServiceDateDto = new DiningTypeServiceDateDto(dailyFood.getServiceDate(), dailyFood.getDiningType());
            dailyFoodMap.add(diningTypeServiceDateDto, dailyFood);
        }

        List<ExtraOrderDto.DailyFoodList> dailyFoodLists = new ArrayList<>();
        for (DiningTypeServiceDateDto diningTypeServiceDateDto : dailyFoodMap.keySet()) {
            ExtraOrderDto.DailyFoodList dailyFoodList = new ExtraOrderDto.DailyFoodList();

            // 2. 음식별로 묶는다.
            List<DailyFood> dailyFoodsByServiceDate = dailyFoodMap.get(diningTypeServiceDateDto);
            MultiValueMap<Food, DailyFood> foodDailyFoodMap = new LinkedMultiValueMap<>();
            for (DailyFood dailyFood : dailyFoodsByServiceDate) {
                foodDailyFoodMap.add(dailyFood.getFood(), dailyFood);
            }

            List<ExtraOrderDto.DailyFood> dailyFoodDtos = new ArrayList<>();
            for (Food food : foodDailyFoodMap.keySet()) {
                ExtraOrderDto.DailyFood dailyFood = new ExtraOrderDto.DailyFood();

                // 3. 그룹별로 묶는다.
                List<DailyFood> dailyFoodByGroup = foodDailyFoodMap.get(food);
                MultiValueMap<Group, DailyFood> groupDailyFoodMap = new LinkedMultiValueMap<>();
                for (DailyFood dailyFood1 : dailyFoodByGroup) {
                    groupDailyFoodMap.add(dailyFood1.getGroup(), dailyFood1);
                }

                List<ExtraOrderDto.Group> groupDtos = new ArrayList<>();
                for (Group group : groupDailyFoodMap.keySet()) {
                    ExtraOrderDto.Group groupDto = new ExtraOrderDto.Group();
                    groupDto.setGroupId(group.getId());
                    groupDto.setGroupName(group.getName());
                    groupDtos.add(groupDto);
                }
                dailyFood.setFoodId(food.getId());
                dailyFood.setFoodName(food.getName());
                dailyFood.setPrice(food.getPrice()); //TODO: 할인된 가격인지, 할인이 되지 않은 가격인지 확인 필요
                dailyFood.setDailyFoodStatus(dailyFoodByGroup.get(0).getDailyFoodStatus().getStatus());
                dailyFood.setFoodCapacity(0);
                dailyFood.setGroupList(groupDtos);
                dailyFoodDtos.add(dailyFood);
            }
            dailyFoodList.setServiceDate(DateUtils.format(diningTypeServiceDateDto.getServiceDate()));
            dailyFoodList.setDiningType(diningTypeServiceDateDto.getDiningType().getDiningType());
            dailyFoodList.setDailyFoods(dailyFoodDtos);
            dailyFoodLists.add(dailyFoodList);
        }

        return dailyFoodLists;
    }
}
