package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.dto.ServiceDiningDto;
import co.dalicious.domain.order.dto.ExtraOrderDto;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.system.util.DateUtils;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface ExtraOrderMapper {
    default List<ExtraOrderDto.DailyFoodList> toDailyFoodList(List<DailyFood> dailyFoods, Map<DailyFood, Integer> dailyFoodCountMap) {
        if(dailyFoods.isEmpty()) {
            return  new ArrayList<>();
        }
        // 1. 식사일정별로 묶는다.
        MultiValueMap<ServiceDiningDto, DailyFood> dailyFoodMap = new LinkedMultiValueMap<>();

        for (DailyFood dailyFood : dailyFoods) {
            ServiceDiningDto serviceDiningDto = new ServiceDiningDto(dailyFood.getServiceDate(), dailyFood.getDiningType());
            dailyFoodMap.add(serviceDiningDto, dailyFood);
        }

        List<ExtraOrderDto.DailyFoodList> dailyFoodLists = new ArrayList<>();
        for (ServiceDiningDto serviceDiningDto : dailyFoodMap.keySet()) {
            ExtraOrderDto.DailyFoodList dailyFoodList = new ExtraOrderDto.DailyFoodList();

            // 2. 음식별로 묶는다.
            List<DailyFood> dailyFoodsByServiceDate = dailyFoodMap.get(serviceDiningDto);
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
                DiscountDto discountDto = null;
                List<ExtraOrderDto.Group> groupDtos = new ArrayList<>();
                for (Group group : groupDailyFoodMap.keySet()) {
                    ExtraOrderDto.Group groupDto = new ExtraOrderDto.Group();
                    groupDto.setGroupId(group.getId());
                    groupDto.setGroupName(group.getName());
                    groupDtos.add(groupDto);

                    if (group instanceof  Corporation) {
                        discountDto = DiscountDto.getDiscount(food);
                    }
                    else {
                        discountDto = DiscountDto.getDiscountWithoutMembership(food);
                    }
                }

                dailyFood.setFoodId(food.getId());
                dailyFood.setFoodName(food.getName());
                dailyFood.setPrice(discountDto != null ? discountDto.getDiscountedPrice() : BigDecimal.ZERO);
                dailyFood.setDailyFoodStatus(dailyFoodByGroup.get(0).getDailyFoodStatus().getStatus());
                dailyFood.setFoodCapacity(dailyFoodCountMap.get(dailyFoodByGroup.get(0))); //TODO: 음식 주문 가능 수량 추가
                dailyFood.setGroupList(groupDtos);
                dailyFoodDtos.add(dailyFood);
            }
            dailyFoodList.setServiceDate(DateUtils.format(serviceDiningDto.getServiceDate()));
            dailyFoodList.setDiningType(serviceDiningDto.getDiningType().getDiningType());
            dailyFoodList.setDailyFoods(dailyFoodDtos);
            dailyFoodLists.add(dailyFoodList);
        }

        return dailyFoodLists;
    }

    default List<ExtraOrderDto.Response> toExtraOrderDtos(List<OrderItemDailyFood> orderItemDailyFoods) {
        List<ExtraOrderDto.Response> responses = new ArrayList<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            responses.add(toExtraOrderDto(orderItemDailyFood));
        }
        return responses.stream().sorted(Comparator.comparing(ExtraOrderDto.Response::getServiceDate)
                .thenComparing(ExtraOrderDto.Response::getCreatedDateTime).reversed()).toList();
    }


    default ExtraOrderDto.Response toExtraOrderDto(OrderItemDailyFood orderItemDailyFood) {
        DiscountDto discountDto = DiscountDto.getDiscountWithoutMembership(orderItemDailyFood.getDailyFood().getFood());
        return ExtraOrderDto.Response.builder()
                .foodName(orderItemDailyFood.getName())
                .orderItemDailyFoodId(orderItemDailyFood.getId())
                .serviceDate(DateUtils.format(orderItemDailyFood.getDailyFood().getServiceDate()))
                .diningType(orderItemDailyFood.getDailyFood().getDiningType().getDiningType())
                .createdDateTime(DateUtils.localDateTimeToString(orderItemDailyFood.getCreatedDateTime().toLocalDateTime()))
                .usage(orderItemDailyFood.getUsage())
                .spotId(((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpot().getId())
                .spotName(((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpotName())
                .groupId(orderItemDailyFood.getDailyFood().getGroup().getId())
                .groupName(orderItemDailyFood.getDailyFood().getGroup().getName())
                .price(discountDto.getDiscountedPrice())
                .count(orderItemDailyFood.getCount())
                .totalPrice(orderItemDailyFood.getOrderItemTotalPrice())
                .dailyFoodStatus(orderItemDailyFood.getDailyFood().getDailyFoodStatus().getStatus())
                .orderStatus(orderItemDailyFood.getOrderStatus().getOrderStatus())
                .build();
    }

}
