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
        ;

        List<Group> groups = (groupIds == null) ? null : groupAllList.stream().filter(group -> groupIds.contains(group.getId())).collect(Collectors.toList());
        LocalDate startDate = (start == null) ? null : DateUtils.stringToDate(start);
        LocalDate endDate = (end == null) ? null : DateUtils.stringToDate(end);
        List<Spot> spots = (spotIds == null) ? null : spotAllList.stream().filter(spot -> spotIds.contains(spot.getId())).toList();

        List<DailyFood> dailyFoodList = qDailyFoodRepository.findAllFilterGroupAndSpot(startDate, endDate, groups, spots);
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findByDailyFoodAndOrderStatus(dailyFoodList);

        List<DeliveryDto.DeliveryInfo> deliveryInfoList = deliveryMapper.toDeliveryInfo(orderItemDailyFoods);
        // service date 묶기
        MultiValueMap<ServiceDateDto, OrderItemDailyFood> serviceDateDtoMap = new LinkedMultiValueMap<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            ServiceDateDto serviceDateDto = new ServiceDateDto(orderItemDailyFood.getDailyFood().getServiceDate(), orderItemDailyFood.getDeliveryTime());
            serviceDateDtoMap.add(serviceDateDto, orderItemDailyFood);
        }

//        List<DeliveryDto.DeliveryInfo> deliveryInfoList = new ArrayList<>();
        for (ServiceDateDto serviceDateDto : serviceDateDtoMap.keySet()) {
            List<OrderItemDailyFood> serviceDaysOrderItemDailyFoodList = serviceDateDtoMap.get(serviceDateDto);

            // spot 묶기
            MultiValueMap<Spot, OrderItemDailyFood> spotDailyFoodMap = new LinkedMultiValueMap<>();
            for (OrderItemDailyFood orderItemDailyFood : serviceDaysOrderItemDailyFoodList) {
                if(Hibernate.unproxy(orderItemDailyFood.getOrder()) instanceof OrderDailyFood orderDailyFood) {
                    Spot spot = orderDailyFood.getSpot();
                    spotDailyFoodMap.add(spot, orderItemDailyFood);
                }
            }

            List<DeliveryDto.DeliveryGroup> deliveryGroupList = new ArrayList<>();
            for (Spot spot : spotDailyFoodMap.keySet()) {
                List<OrderItemDailyFood> spotOrderItemDailyFoodList = spotDailyFoodMap.get(spot);

                // maker 묶기
                MultiValueMap<Makers, OrderItemDailyFood> makersOrderItemDailyFoodMap = new LinkedMultiValueMap<>();
                for (OrderItemDailyFood orderItemDailyFood : spotOrderItemDailyFoodList) {
                    makersOrderItemDailyFoodMap.add(orderItemDailyFood.getDailyFood().getFood().getMakers(), orderItemDailyFood);
                }

                DiningType diningType = null;
                LocalTime pickupTime = null;
                List<DeliveryDto.DeliveryMakers> deliveryMakersList = new ArrayList<>();
                for (Makers makers : makersOrderItemDailyFoodMap.keySet()) {
                    List<OrderItemDailyFood> makersOrderItemDailyFoodList = Objects.requireNonNull(makersOrderItemDailyFoodMap.get(makers)).stream().sorted(Comparator.comparing(o -> o.getDailyFood().getId())).toList();

                    int count = 0;
                    DailyFood dailyFood = null;
                    List<DeliveryDto.DeliveryFood> deliveryFoodList = new ArrayList<>();
                    for (OrderItemDailyFood orderItemDailyFood : makersOrderItemDailyFoodList) {
                        // pickup time
                        if (pickupTime == null) {
                            List<DeliverySchedule> deliverySchedules = orderItemDailyFood.getDailyFood().getDailyFoodGroup().getDeliverySchedules();
                            pickupTime = deliverySchedules.stream().filter(deliverySchedule -> deliverySchedule.getDeliveryTime().equals(serviceDateDto.getDeliveryTime())).map(DeliverySchedule::getPickupTime).findAny().orElse(null);
                        }

                        // dining type
                        if (diningType == null) {
                            diningType = orderItemDailyFood.getDailyFood().getDiningType();
                        }

                        if(dailyFood == null || !dailyFood.equals(orderItemDailyFood.getDailyFood())) {
                            count = 0;
                            dailyFood = orderItemDailyFood.getDailyFood();
                        }
                        // count
                        count += orderItemDailyFood.getCount();

                    }
                    DeliveryDto.DeliveryFood deliveryFood = deliveryMapper.toDeliveryFood(dailyFood, count);
                    deliveryFoodList.add(deliveryFood);
                    deliveryFoodList = deliveryFoodList.stream().sorted(Comparator.comparing(DeliveryDto.DeliveryFood::getFoodName)).collect(Collectors.toList());

                    DeliveryDto.DeliveryMakers deliveryMakers = deliveryMapper.toDeliveryMakers(makers, deliveryFoodList, pickupTime);
                    deliveryMakersList.add(deliveryMakers);
                }
                // delivery makers list 를 pickup time 으로 정렬
                deliveryMakersList = deliveryMakersList.stream()
                        .sorted(Comparator.comparing(deliveryMakers -> LocalTime.parse(deliveryMakers.getPickupTime()))).collect(Collectors.toList());

                // delivery group 만들기
                DeliveryDto.DeliveryGroup deliveryGroup = deliveryMapper.toDeliveryGroup(spot, Objects.requireNonNull(diningType).getCode(), deliveryMakersList);
                deliveryGroupList.add(deliveryGroup);
            }

            // delivery info 만들기
            DeliveryDto.DeliveryInfo deliveryInfo = deliveryMapper.toDeliveryInfo(serviceDateDto, deliveryGroupList);
            deliveryInfoList.add(deliveryInfo);
        }

        if (groups != null && !groups.isEmpty()) {
            spotAllList = spotAllList.stream().filter(spot -> groups.contains(spot.getGroup())).toList();
        }

        return DeliveryDto.create(groupAllList, deliveryInfoList, spotAllList);
    }
}
