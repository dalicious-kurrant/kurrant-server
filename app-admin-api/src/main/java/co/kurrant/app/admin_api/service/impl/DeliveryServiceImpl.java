package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.DailyFoodGroup;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.dto.OrderDto;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.QOrder;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.order.repository.QOrderRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.DeliveryDto;
import co.kurrant.app.admin_api.mapper.DeliveryMapper;
import co.kurrant.app.admin_api.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.N;
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

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto getDeliverySchedule(String start, String end, List<BigInteger> groupIds, List<BigInteger> spotIds) {
        List<Group> groupAllList = groupRepository.findAll();
        List<Spot> spotAllList = spotRepository.findAll();
        List<Group> groups = (groupIds == null) ? null : groupAllList.stream().filter(group -> groupIds.contains(group.getId())).collect(Collectors.toList());
        LocalDate startDate = (start == null) ? null : DateUtils.stringToDate(start);
        LocalDate endDate = (end == null) ? null : DateUtils.stringToDate(end);
        List<Spot> spots = (spotIds == null) ? null : spotAllList.stream().filter(spot -> spotIds.contains(spot.getId())).toList();
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllFilterGroup(startDate, endDate, groups, spots);

        List<OrderDailyFood> orderList = new ArrayList<>();
        for(OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            Order order = (Order) Hibernate.unproxy(orderItemDailyFood.getOrder());
            if(order instanceof OrderDailyFood orderDailyFood) {
                orderList.add(orderDailyFood);
            }
        }

        // daily food count 구하기, pickup time 가져오기
        Map<DailyFood, Integer> dailyFoodCount = new HashMap<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            DailyFood dailyFood = orderItemDailyFood.getDailyFood();
            int count = orderItemDailyFood.getCount();
            dailyFoodCount.put(dailyFood, dailyFoodCount.getOrDefault(dailyFood, 0) + count);
        }

        // service date 묶기
        MultiValueMap<LocalDate, OrderItemDailyFood> serviceDateMap = new LinkedMultiValueMap<>();
        for(OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            LocalDate serviceDate = orderItemDailyFood.getDailyFood().getServiceDate();
            serviceDateMap.add(serviceDate, orderItemDailyFood);
        }

        List<DeliveryDto.DeliveryInfo> deliveryInfoList = new ArrayList<>();
        for(LocalDate serviceDate : serviceDateMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoodList = serviceDateMap.get(serviceDate);

            // spot 묶기
            MultiValueMap<Spot, OrderItemDailyFood> spotMap = new LinkedMultiValueMap<>();
            for(OrderItemDailyFood orderItemDailyFood : Objects.requireNonNull(orderItemDailyFoodList)) {
                Spot spot = orderList.stream()
                                .filter(orderDailyFood -> orderDailyFood.getId().equals(orderItemDailyFood.getOrder().getId()))
                                .map(OrderDailyFood::getSpot)
                                .findFirst().orElse(null);

                spotMap.add(Objects.requireNonNull(spot), orderItemDailyFood);
            }

            List<DeliveryDto.DeliveryGroup> deliveryGroupList = new ArrayList<>();
            for(Spot spot : spotMap.keySet()) {
                List<OrderItemDailyFood> spotOrderItemDailyFoodList = spotMap.get(spot);


                // makers 묶기
                MultiValueMap<Makers, OrderItemDailyFood> makersMap = new LinkedMultiValueMap<>();
                for(OrderItemDailyFood orderItemDailyFood : Objects.requireNonNull(spotOrderItemDailyFoodList)) {
                    Makers makers = orderItemDailyFood.getDailyFood().getFood().getMakers();
                    makersMap.add(makers, orderItemDailyFood);
                }

                DiningType diningType = null;
                LocalTime deliveryTime = null;

                List<DeliveryDto.DeliveryMakers> deliveryMakersList = new ArrayList<>();
                for(Makers makers : makersMap.keySet()) {
                    List<OrderItemDailyFood> makersOrderItemDailyFoodList = makersMap.get(makers);

                    LocalTime pickupTime = null;
                    List<DeliveryDto.DeliveryFood> deliveryFoodList = new ArrayList<>();
                    for(OrderItemDailyFood orderItemDailyFood : Objects.requireNonNull(makersOrderItemDailyFoodList)) {
                        DailyFood dailyFood = orderItemDailyFood.getDailyFood();
                        Integer count = dailyFoodCount.get(dailyFood);

                        // delivery food 만들기
                        DeliveryDto.DeliveryFood deliveryFood = deliveryMapper.toDeliveryFood(dailyFood, count);

                        deliveryFoodList.add(deliveryFood);

                        if(pickupTime == null) {
                            pickupTime = dailyFood.getDailyFoodGroup().getPickupTime();
                        }
                        if(diningType == null) {
                            diningType = dailyFood.getDiningType();
                        }
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

        return DeliveryDto.create(groupAllList, deliveryInfoList, spotAllList);
    }
}
