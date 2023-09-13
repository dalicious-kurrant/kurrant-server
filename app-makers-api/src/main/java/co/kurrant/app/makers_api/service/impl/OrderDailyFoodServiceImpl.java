package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.mappper.DeliveryInstanceMapper;
import co.dalicious.domain.delivery.repository.QDeliveryInstanceRepository;
import co.dalicious.domain.food.entity.FoodCapacity;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.QFoodCapacityRepository;
import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.mapper.OrderDailyFoodByMakersMapper;
import co.dalicious.domain.order.repository.QOrderItemDailyFoodRepository;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DiningTypesUtils;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.OrderDailyFoodService;
import co.kurrant.app.makers_api.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    private final UserUtil userUtil;
    private final QOrderItemDailyFoodRepository qOrderItemDailyFoodRepository;
    private final OrderDailyFoodByMakersMapper orderDailyFoodByMakersMapper;
    private final DeliveryInstanceMapper deliveryInstanceMapper;
    private final QDeliveryInstanceRepository qDeliveryInstanceRepository;
    private final QFoodCapacityRepository qFoodCapacityRepository;
    @Override
    @Transactional
    public OrderDailyFoodByMakersDto.ByPeriod getOrder(SecurityUser securityUser, Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<Integer> diningTypes = !parameters.containsKey("diningTypes") || parameters.get("diningTypes").equals("") ? null : StringUtils.parseIntegerList((String) parameters.get("diningTypes"));
        Makers makers = userUtil.getMakers(securityUser);
//        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("makersId")));

        List<FoodCapacity> foodCapacities = qFoodCapacityRepository.getFoodCapacitiesByMakers(makers);
        List<OrderItemDailyFood> orderItemDailyFoodList = qOrderItemDailyFoodRepository.findAllByMakersFilter(startDate, endDate, makers, diningTypes);

        return orderDailyFoodByMakersMapper.toDto(orderItemDailyFoodList, foodCapacities);
    }

    @Override
    @Transactional
    public OrderDailyFoodByMakersDto.ByPeriod getOrderByDelivery(SecurityUser securityUser, Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<Integer> diningTypes = !parameters.containsKey("diningTypes") || parameters.get("diningTypes").equals("") ? null : StringUtils.parseIntegerList((String) parameters.get("diningTypes"));
        Makers makers = userUtil.getMakers(securityUser);

        List<DeliveryInstance> deliveryInstances = qDeliveryInstanceRepository.findByFilter(startDate, endDate, DiningTypesUtils.codesToDiningTypes(diningTypes), makers);
        List<FoodCapacity> foodCapacities = qFoodCapacityRepository.getFoodCapacitiesByMakers(makers);
        return deliveryInstanceMapper.toDto(deliveryInstances, foodCapacities);
    }
}
