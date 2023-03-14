package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.system.util.PeriodDto;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final QGroupRepository qGroupRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto getDeliverySchedule(PeriodDto.PeriodStringDto periodDto, List<BigInteger> groupIds) {
        List<Group> groups = (groupIds == null) ? null : qGroupRepository.findAllByIds(groupIds);
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllFilterGroup(periodDto.toPeriodDto().getStartDate(), periodDto.toPeriodDto().getEndDate(), groups);

        // daily food count 구하기
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

        for(DeliveryDto.MakersGrouping makersGrouping : deliveryFoodMap.keySet()) {
            List<DeliveryDto.DeliveryFood> deliveryFoodList = deliveryFoodMap.get(makersGrouping);
            DeliveryDto.DeliveryMakers deliveryMakers = deliveryMapper.toDeliveryGroup()
        }



        return null;
    }
}
