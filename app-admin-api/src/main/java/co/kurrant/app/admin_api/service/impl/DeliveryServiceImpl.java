package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.DailyFoodGroup;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.DeliveryDto;
import co.kurrant.app.admin_api.mapper.DeliveryMapper;
import co.kurrant.app.admin_api.service.DeliveryService;
import lombok.RequiredArgsConstructor;
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
    private final QGroupRepository qGroupRepository;
    private final DeliveryMapper deliveryMapper;
    private final GroupRepository groupRepository;

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto getDeliverySchedule(String start, String end, List<BigInteger> groupIds) {
        List<Group> groupAllList = groupRepository.findAll();
        List<Group> groups = (groupIds == null) ? null : groupAllList.stream().filter(group -> groupIds.contains(group.getId())).collect(Collectors.toList());
        LocalDate startDate = (start == null) ? null : DateUtils.stringToDate(start);
        LocalDate endDate = (end == null) ? null : DateUtils.stringToDate(end);
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllFilterGroup(startDate, endDate, groups);

        Set<DailyFood> dailyFoodSet = orderItemDailyFoods.stream().map(OrderItemDailyFood::getDailyFood).collect(Collectors.toSet());

        // daily food count 구하기, pickup time 가져오기
        Map<DailyFood, Integer> dailyFoodCount = new HashMap<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            DailyFood dailyFood = orderItemDailyFood.getDailyFood();
            int count = orderItemDailyFood.getCount();
            dailyFoodCount.put(dailyFood, dailyFoodCount.getOrDefault(dailyFood, 0) + count);
        }

        MultiValueMap<DeliveryDto.MakersGrouping, DeliveryDto.DeliveryFood> deliveryFoodMap = new LinkedMultiValueMap<>();
        for(DailyFood dailyFood : dailyFoodCount.keySet()) {
            Integer count = dailyFoodCount.get(dailyFood);
            DeliveryDto.DeliveryFood deliveryFood = deliveryMapper.toDeliveryFood(dailyFood, count);
            DeliveryDto.MakersGrouping makersGrouping = DeliveryDto.MakersGrouping.create(dailyFood);
            deliveryFoodMap.add(makersGrouping, deliveryFood);
        }

        MultiValueMap<DeliveryDto.GroupGrouping, DeliveryDto.DeliveryMakers> deliveryMakersMap = new LinkedMultiValueMap<>();
        for(DeliveryDto.MakersGrouping makersGrouping : deliveryFoodMap.keySet()) {
            List<DeliveryDto.DeliveryFood> deliveryFoodList = deliveryFoodMap.get(makersGrouping);

            LocalTime pickupTime = dailyFoodSet.stream()
                            .filter(v -> v.getGroup().equals(makersGrouping.getGroup()) && v.getServiceDate().equals(makersGrouping.getServiceDate())
                                    && v.getFood().getMakers().equals(makersGrouping.getMakers()) && v.getDiningType().equals(makersGrouping.getDiningType()))
                            .map(DailyFood::getDailyFoodGroup).findFirst()
                    .map(DailyFoodGroup::getPickupTime).orElse(null);

            DeliveryDto.DeliveryMakers deliveryMakers = deliveryMapper.toDeliveryMakers(makersGrouping.getMakers(), deliveryFoodList, pickupTime);

            DeliveryDto.GroupGrouping groupGrouping = DeliveryDto.GroupGrouping.create(makersGrouping);
            deliveryMakersMap.add(groupGrouping, deliveryMakers);
        }

        MultiValueMap<LocalDate, DeliveryDto.DeliveryGroup> deliveryGroupMap = new LinkedMultiValueMap<>();
        for(DeliveryDto.GroupGrouping groupGrouping : deliveryMakersMap.keySet()) {
            List<DeliveryDto.DeliveryMakers> deliveryMakersList = Objects.requireNonNull(deliveryMakersMap.get(groupGrouping))
                    .stream().sorted(Comparator.comparing(deliveryMakers -> LocalTime.parse(deliveryMakers.getPickupTime()))).collect(Collectors.toList());

            DiningType diningType = dailyFoodSet.stream()
                    .filter(v -> v.getGroup().equals(groupGrouping.getGroup()) && v.getServiceDate().equals(groupGrouping.getServiceDate()) && v.getDiningType().equals(groupGrouping.getDiningType()))
                    .map(DailyFood::getDiningType).findFirst().orElse(null);

            LocalTime deliveryTime = groupGrouping.getGroup().getMealInfos().stream()
                    .filter(v -> v.getDiningType().equals(diningType))
                    .map(MealInfo::getDeliveryTime).findFirst().orElse(null);

            DeliveryDto.DeliveryGroup deliveryGroup = deliveryMapper.toDeliveryGroup(groupGrouping.getGroup(), Objects.requireNonNull(diningType).getCode(), deliveryTime, deliveryMakersList);
            deliveryGroupMap.add(groupGrouping.getServiceDate(), deliveryGroup);
        }

        List<DeliveryDto.DeliveryInfo> deliveryInfoList = new ArrayList<>();
        for(LocalDate serviceDate : deliveryGroupMap.keySet()) {
            List<DeliveryDto.DeliveryGroup> deliveryGroupList = deliveryGroupMap.get(serviceDate);
            DeliveryDto.DeliveryInfo deliveryInfo = deliveryMapper.toDeliveryInfo(serviceDate, deliveryGroupList);
            deliveryInfoList.add(deliveryInfo);
        }
        deliveryInfoList = deliveryInfoList.stream().sorted(Comparator.comparing(DeliveryDto.DeliveryInfo::getServiceDate)).collect(Collectors.toList());

        return DeliveryDto.create(groupAllList, deliveryInfoList);
    }
}
