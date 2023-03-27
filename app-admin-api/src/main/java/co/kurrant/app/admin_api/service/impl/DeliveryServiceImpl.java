package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QSpotRepository;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.DeliveryDto;
import co.kurrant.app.admin_api.mapper.DeliveryMapper;
import co.kurrant.app.admin_api.service.DeliveryService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final DeliveryMapper deliveryMapper;
    private final GroupRepository groupRepository;
    private final SpotRepository spotRepository;
    private final QDailyFoodRepository qDailyFoodRepository;

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto getDeliverySchedule(String start, String end, List<BigInteger> groupIds, List<BigInteger> spotIds, String isDefault) {
        List<Group> groupAllList = groupRepository.findAll();
        // 그룹과 연관된 스팟만 보여주기
        List<Spot> spotAllList = spotRepository.findAll();;

        List<Group> groups = (groupIds == null) ? null : groupAllList.stream().filter(group -> groupIds.contains(group.getId())).collect(Collectors.toList());
        LocalDate startDate = (start == null) ? null : DateUtils.stringToDate(start);
        LocalDate endDate = (end == null) ? null : DateUtils.stringToDate(end);
        List<Spot> spots = (spotIds == null) ? null : spotAllList.stream().filter(spot -> spotIds.contains(spot.getId())).toList();
        List<DailyFood> dailyFoodList = qDailyFoodRepository.findAllFilterGroupAndSpot(startDate, endDate, groups, spots);
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findByDailyFoodAndOrderStatus(dailyFoodList);

        List<OrderDailyFood> orderList = new ArrayList<>();
        for(OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            Order order = (Order) Hibernate.unproxy(orderItemDailyFood.getOrder());
            if(order instanceof OrderDailyFood orderDailyFood) {
                orderList.add(orderDailyFood);
            }
        }

        Map<DailyFood, Integer> dailyFoodIntegerMap = new HashMap<>();
        for(DailyFood dailyFood : dailyFoodList) {
            int count = 0;
            for(OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                if(orderItemDailyFood.getDailyFood().equals(dailyFood)) {
                    count += orderItemDailyFood.getCount();
                }
            }
            dailyFoodIntegerMap.put(dailyFood, count);
        }

        // service date 묶기
        MultiValueMap<LocalDate, DailyFood> serviceDateMap = new LinkedMultiValueMap<>();
        for(DailyFood dailyFood : dailyFoodList) {
            LocalDate serviceDate = dailyFood.getServiceDate();
            serviceDateMap.add(serviceDate, dailyFood);
        }

        List<DeliveryDto.DeliveryInfo> deliveryInfoList = new ArrayList<>();
        for(LocalDate serviceDate : serviceDateMap.keySet()) {
            List<DailyFood> serviceDateDailyFoodList = serviceDateMap.get(serviceDate);

            // spot 묶기
            MultiValueMap<Spot, DailyFood> spotDailyFoodMap = new LinkedMultiValueMap<>();
            if(isDefault == null) {
                for(DailyFood dailyFood : Objects.requireNonNull(serviceDateDailyFoodList)) {
                    OrderItemDailyFood orderItemDailyFood = orderItemDailyFoods.stream()
                            .filter(oidf -> oidf.getDailyFood().equals(dailyFood))
                            .findFirst().orElse(null);
                    if(orderItemDailyFood == null) continue;
                    Spot spot = orderList.stream()
                            .filter(orderDailyFood -> orderDailyFood.getId().equals(orderItemDailyFood.getOrder().getId()))
                            .map(OrderDailyFood::getSpot)
                            .findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));

                    spotDailyFoodMap.add(spot, dailyFood);
                }
            }
            else {
                for(DailyFood dailyFood : Objects.requireNonNull(serviceDateDailyFoodList)) {
                    Spot spot = spotAllList.stream()
                            .filter(s -> s.getGroup().equals(dailyFood.getGroup()))
                            .findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));

                    spotDailyFoodMap.add(spot, dailyFood);
                }
            }

            List<DeliveryDto.DeliveryGroup> deliveryGroupList = new ArrayList<>();
            for(Spot spot : spotDailyFoodMap.keySet()) {
                List<DailyFood> spotDailyFoodList = spotDailyFoodMap.get(spot);

                // makers 묶기
                MultiValueMap<Makers, DailyFood> makersMap = new LinkedMultiValueMap<>();
                if(isDefault == null) {
                    for(DailyFood dailyFood : Objects.requireNonNull(spotDailyFoodList)) {
                        Makers makers = orderItemDailyFoods.stream()
                                .map(OrderItemDailyFood::getDailyFood)
                                .filter(df -> df.equals(dailyFood))
                                .map(DailyFood::getFood)
                                .map(Food::getMakers)
                                .findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));

                        makersMap.add(makers, dailyFood);
                    }
                }
                else {
                    for(DailyFood dailyFood : Objects.requireNonNull(spotDailyFoodList)) {
                        Makers makers = dailyFood.getFood().getMakers();
                        makersMap.add(makers, dailyFood);
                    }
                }

                DiningType diningType = null;
                LocalTime deliveryTime = null;

                List<DeliveryDto.DeliveryMakers> deliveryMakersList = new ArrayList<>();
                for(Makers makers : makersMap.keySet()) {
                    List<DailyFood> makersDailyFoodList = makersMap.get(makers);

                    LocalTime pickupTime = null;
                    List<DeliveryDto.DeliveryFood> deliveryFoodList = new ArrayList<>();
                    for(DailyFood dailyFood : Objects.requireNonNull(makersDailyFoodList)) {

                        // count 구하기
                        int count = dailyFoodIntegerMap.get(dailyFood);

                        // pickup time
                        if(pickupTime == null) {
                            pickupTime = dailyFood.getDailyFoodGroup().getPickupTime();
                        }
                        // dining type
                        if(diningType == null) {
                            diningType = dailyFood.getDiningType();
                        }

                        // 주문한 delivery food 만 dto 만들기
                        if(count == 0) continue;
                        DeliveryDto.DeliveryFood deliveryFood = deliveryMapper.toDeliveryFood(dailyFood, count);

                        deliveryFoodList.add(deliveryFood);

                    }

                    // delivery makers 만들기
                    DeliveryDto.DeliveryMakers deliveryMakers = deliveryMapper.toDeliveryMakers(makers, deliveryFoodList, pickupTime);
                    deliveryMakersList.add(deliveryMakers);
                }

                if(diningType != null) {
                    DiningType finalDiningType = diningType;
                    deliveryTime = spot.getGroup().getMealInfos().stream()
                            .filter(mealInfo -> mealInfo.getDiningType().equals(finalDiningType))
                            .map(MealInfo::getDeliveryTime)
                            .findFirst().orElse(null);
                }

                // delivery makers list 를 pickup time 으로 정렬
                deliveryMakersList = deliveryMakersList.stream()
                        .sorted(Comparator.comparing(deliveryMakers -> LocalTime.parse(deliveryMakers.getPickupTime()))).collect(Collectors.toList());

                // delivery group 만들기
                DeliveryDto.DeliveryGroup deliveryGroup = deliveryMapper.toDeliveryGroup(spot, Objects.requireNonNull(diningType).getCode(), deliveryTime, deliveryMakersList);
                deliveryGroupList.add(deliveryGroup);
            }

            // delivery info 만들기
            DeliveryDto.DeliveryInfo deliveryInfo = deliveryMapper.toDeliveryInfo(serviceDate, deliveryGroupList);
            deliveryInfoList.add(deliveryInfo);
        }

        // service date 로 정렬
        deliveryInfoList = deliveryInfoList.stream().sorted(Comparator.comparing(DeliveryDto.DeliveryInfo::getServiceDate)).collect(Collectors.toList());

        if(groups != null && !groups.isEmpty()) {
            spotAllList = spotAllList.stream().filter(spot -> groups.contains(spot.getGroup())).toList();
        }

        return DeliveryDto.create(groupAllList, deliveryInfoList, spotAllList);
    }
}
