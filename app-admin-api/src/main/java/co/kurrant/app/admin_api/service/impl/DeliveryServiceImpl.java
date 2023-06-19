package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;
import co.kurrant.app.admin_api.dto.delivery.ServiceDateDto;
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
import java.util.concurrent.atomic.AtomicInteger;
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
    public DeliveryDto getDeliverySchedule(String start, String end, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll) {
        List<Group> groupAllList = groupRepository.findAll();
        // 그룹과 연관된 스팟만 보여주기
        List<Spot> spotAllList = spotRepository.findAll();

        List<Group> groups = (groupIds == null) ? null : groupAllList.stream().filter(group -> groupIds.contains(group.getId())).collect(Collectors.toList());
        LocalDate startDate = (start == null) ? null : DateUtils.stringToDate(start);
        LocalDate endDate = (end == null) ? null : DateUtils.stringToDate(end);
        List<Spot> spots = (spotIds == null) ? null : spotAllList.stream().filter(spot -> spotIds.contains(spot.getId())).toList();

        List<DailyFood> dailyFoodList = qDailyFoodRepository.findAllFilterGroupAndSpot(startDate, endDate, groups, spots);
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findByDailyFoodAndOrderStatus(dailyFoodList);

        if(orderItemDailyFoods.isEmpty() || orderItemDailyFoods == null) return null;

        // service date 묶기
        MultiValueMap<ServiceDateDto, OrderItemDailyFood> serviceDateDtoMap = new LinkedMultiValueMap<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            ServiceDateDto serviceDateDto = new ServiceDateDto(orderItemDailyFood.getDailyFood().getServiceDate(), orderItemDailyFood.getDeliveryTime());
            serviceDateDtoMap.add(serviceDateDto, orderItemDailyFood);
        }
        List<DeliveryDto.DeliveryInfo> deliveryInfoList = new ArrayList<>();
        for (ServiceDateDto serviceDateDto : serviceDateDtoMap.keySet()) {
            List<OrderItemDailyFood> serviceDaysOrderItemDailyFoodList = serviceDateDtoMap.get(serviceDateDto);

            Map<Spot, List<OrderItemDailyFood>> spotDailyFoodMap = serviceDaysOrderItemDailyFoodList.stream()
                    .filter(orderItemDailyFood -> Hibernate.unproxy(orderItemDailyFood.getOrder()) instanceof OrderDailyFood)
                    .collect(Collectors.groupingBy(orderItemDailyFood -> ((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpot()));

            deliveryInfoList = spotDailyFoodMap.entrySet().stream()
                    .map(entry -> {
                        Spot spot = entry.getKey();
                        List<OrderItemDailyFood> spotOrderItemDailyFoodList = entry.getValue();

                        Map<Makers, List<OrderItemDailyFood>> makersOrderItemDailyFoodMap = spotOrderItemDailyFoodList.stream()
                                .collect(Collectors.groupingBy(orderItemDailyFood -> orderItemDailyFood.getDailyFood().getFood().getMakers()));

                        DiningType diningType = spotOrderItemDailyFoodList.get(0).getDailyFood().getDiningType();
                        LocalTime pickupTime = spotOrderItemDailyFoodList.stream()
                                .flatMap(orderItemDailyFood -> orderItemDailyFood.getDailyFood().getDailyFoodGroup().getDeliverySchedules().stream())
                                .filter(deliverySchedule -> deliverySchedule.getDeliveryTime().equals(serviceDateDto.getDeliveryTime()))
                                .map(DeliverySchedule::getPickupTime)
                                .findFirst()
                                .orElse(null);

                        List<DeliveryDto.DeliveryMakers> deliveryMakersList = makersOrderItemDailyFoodMap.entrySet().stream()
                                .sorted(Comparator.comparing(makersEntry -> makersEntry.getValue().get(0).getDailyFood().getId()))
                                .map(makersEntry -> {
                                    Makers makers = makersEntry.getKey();
                                    List<OrderItemDailyFood> makersOrderItemDailyFoodList = makersEntry.getValue();

                                    Map<DailyFood, Integer> dailyFoodIntegerMap = makersOrderItemDailyFoodList.stream()
                                            .sorted(Comparator.comparing(orderItemDailyFood -> orderItemDailyFood.getDailyFood().getId()))
                                            .collect(Collectors.groupingBy(OrderItemDailyFood::getDailyFood, Collectors.summingInt(OrderItemDailyFood::getCount)));

                                    List<DeliveryDto.DeliveryFood> deliveryFoodList = dailyFoodIntegerMap.entrySet().stream()
                                            .map(orderItemDailyFood -> deliveryMapper.toDeliveryFood(orderItemDailyFood.getKey(), orderItemDailyFood.getValue()))
                                            .sorted(Comparator.comparing(DeliveryDto.DeliveryFood::getFoodName))
                                            .collect(Collectors.toList());

                                    return deliveryMapper.toDeliveryMakers(makers, deliveryFoodList, pickupTime);
                                })
                                .collect(Collectors.toList());

                        deliveryMakersList = deliveryMakersList.stream()
                                .sorted(Comparator.comparing(deliveryMakers -> LocalTime.parse(deliveryMakers.getPickupTime())))
                                .collect(Collectors.toList());

                        DeliveryDto.DeliveryGroup deliveryGroup = deliveryMapper.toDeliveryGroup(spot, diningType.getCode(), serviceDateDto.getDeliveryTime(), deliveryMakersList);
                        return deliveryMapper.toDeliveryInfo(serviceDateDto, Collections.singletonList(deliveryGroup));
                    })
                    .collect(Collectors.toList());

            if (groups != null && !groups.isEmpty()) {
                spotAllList = spotAllList.stream()
                        .filter(spot -> groups.contains(spot.getGroup()))
                        .collect(Collectors.toList());
            }
        }
        return DeliveryDto.create(groupAllList, deliveryInfoList, spotAllList);
    }
}
