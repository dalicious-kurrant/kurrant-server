package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.repository.QDailyFoodDeliveryRepository;
import co.dalicious.domain.delivery.repository.QDeliveryInstanceRepository;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;
import co.kurrant.app.admin_api.dto.delivery.ServiceDateDto;
import co.kurrant.app.admin_api.mapper.DeliveryMapper;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.MakersMapper;
import co.kurrant.app.admin_api.service.DeliveryService;
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
    private final DeliveryMapper deliveryMapper;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final QDailyFoodDeliveryRepository qDailyFoodDeliveryRepository;
    private final MakersRepository makersRepository;
    private final UserRepository userRepository;
    private final QDeliveryInstanceRepository qDeliveryInstanceRepository;
    private final GroupRepository groupRepository;
    private final SpotRepository spotRepository;
    private final MakersMapper makersMapper;
    private final GroupMapper groupMapper;
    private final QGroupRepository qGroupRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto getDelivery(String start, String end, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll) {
        List<Group> groupAllList = groupRepository.findAll();
        // 그룹과 연관된 스팟만 보여주기
        List<Spot> spotAllList = spotRepository.findAll();

        List<Group> groups = (groupIds == null) ? null : groupAllList.stream().filter(group -> groupIds.contains(group.getId())).collect(Collectors.toList());
        LocalDate startDate = (start == null) ? null : DateUtils.stringToDate(start);
        LocalDate endDate = (end == null) ? null : DateUtils.stringToDate(end);
        List<Spot> spots = (spotIds == null) ? null : spotAllList.stream().filter(spot -> spotIds.contains(spot.getId())).toList();

        List<DailyFood> dailyFoodList = qDailyFoodRepository.findAllFilterGroupAndSpot(startDate, endDate, groups, spots);
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findByDailyFoodAndOrderStatus(dailyFoodList);

        if (orderItemDailyFoods.isEmpty() || orderItemDailyFoods == null) return null;

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

            List<DeliveryDto.DeliveryGroup> deliveryGroupList = spotDailyFoodMap.entrySet().stream()
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
                                .sorted(Comparator.comparing(deliveryMakers ->
                                                deliveryMakers.getPickupTime() != null ? LocalTime.parse(deliveryMakers.getPickupTime()) : null,
                                        Comparator.nullsFirst(Comparator.naturalOrder())
                                ))
                                .collect(Collectors.toList());

                        return deliveryMapper.toDeliveryGroup(spot, diningType.getCode(), serviceDateDto.getDeliveryTime(), deliveryMakersList);
                    }).toList();

            deliveryInfoList.add(deliveryMapper.toDeliveryInfo(serviceDateDto.getServiceDate(), deliveryGroupList));
        }
        if (groups != null && !groups.isEmpty()) {
            spotAllList = spotAllList.stream()
                    .filter(spot -> groups.contains(spot.getGroup()))
                    .collect(Collectors.toList());
        }
        return DeliveryDto.create(groupAllList, deliveryInfoList, spotAllList);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto getDeliverySchedule(String start, String end, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll) {
        LocalDate startDate = (start == null) ? null : DateUtils.stringToDate(start);
        LocalDate endDate = (end == null) ? null : DateUtils.stringToDate(end);

        List<Group> groupAllList = qGroupRepository.findAllExceptForMySpot();
        List<Group> groups = (groupIds == null) ? null : groupAllList.stream().filter(group -> groupIds.contains(group.getId())).toList();
        // 그룹과 연관된 스팟만 보여주기
        List<Spot> spotAllList = groups == null || groups.isEmpty() ? groupAllList.stream().flatMap(group -> group.getSpots().stream()).toList() : groups.stream().flatMap(group -> group.getSpots().stream()).toList();
        List<Spot> spots = (spotIds == null) ? null : spotAllList.stream().filter(spot -> spotIds.contains(spot.getId())).toList();

        List<DailyFood> dailyFoodList = qDailyFoodRepository.findAllFilterGroupAndSpot(startDate, endDate, groups, spots);
        List<DeliveryInstance> deliveryInstanceList = qDeliveryInstanceRepository.findByDailyFoodAndOrderStatus(dailyFoodList);

        List<DeliveryDto.DeliveryInfo> deliveryInfoList = new ArrayList<>();
        if (deliveryInstanceList.isEmpty()) return DeliveryDto.create(groupAllList, deliveryInfoList, spotAllList);
        return DeliveryDto.create(groupAllList, deliveryMapper.getDeliveryInfoList(deliveryInstanceList), spotAllList);
    }

    @Override
    @Transactional
    public List<DeliveryDto.DeliveryManifest> getDeliveryManifest(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        Integer spotType = !parameters.containsKey("spotType") || parameters.get("spotType").equals("") ? null : Integer.parseInt((String) parameters.get("spotType"));
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("makersId")));
        Integer diningTypeCode = !parameters.containsKey("diningType") || parameters.get("diningType").equals("") ? null : Integer.parseInt((String) parameters.get("diningType"));
        LocalTime deliveryTime = !parameters.containsKey("deliveryTime") || parameters.get("deliveryTime").equals("") ? null : DateUtils.stringToLocalTime((String) parameters.get("deliveryTime"));
        String orderNumber = !parameters.containsKey("orderNumber") || parameters.get("orderNumber").equals("") ? null : (String) parameters.get("orderNumber");
        BigInteger userId = !parameters.containsKey("userId") || parameters.get("userId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("userId")));

        Makers makers = null;
        User user = null;
        if (makersId != null) {
            makers = makersRepository.findById(makersId).orElse(null);

        }
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        List<DailyFoodDelivery> dailyFoodDeliveries = qDailyFoodDeliveryRepository.findByFilter(startDate, endDate, (spotType == null) ? null : GroupDataType.ofCode(spotType), makers, (diningTypeCode == null) ? null : DiningType.ofCode(diningTypeCode), deliveryTime, orderNumber, user);

        return deliveryMapper.toDeliveryManifests(dailyFoodDeliveries);
    }

    @Override
    @Transactional
    public List<MakersDto.Makers> getDeliverMakersByDate(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<DeliveryInstance> deliveryInstances = qDeliveryInstanceRepository.findByPeriod(startDate, endDate);
        Set<Makers> makers = deliveryInstances.stream()
                .map(DeliveryInstance::getMakers)
                .collect(Collectors.toSet());
        return makersMapper.makersToDtos(makers);
    }

    @Override
    public List<String> getDeliveryTimesByDate(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<DeliveryInstance> deliveryInstances = qDeliveryInstanceRepository.findByPeriod(startDate, endDate);
        Set<LocalTime> deliveryTimes = deliveryInstances.stream()
                .map(DeliveryInstance::getDeliveryTime)
                .collect(Collectors.toSet());
        return deliveryTimes.stream()
                .map(DateUtils::timeToString)
                .sorted()
                .toList();
    }

    @Override
    public List<String> getDeliveryCodesByDate(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<DeliveryInstance> deliveryInstances = qDeliveryInstanceRepository.findByPeriod(startDate, endDate);
        return deliveryInstances.stream()
                .map(DeliveryInstance::getDeliveryCode)
                .toList();
    }

    @Override
    @Transactional
    public List<GroupDto.User> getDeliverUsersByDate(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<DeliveryInstance> deliveryInstances = qDeliveryInstanceRepository.findByPeriod(startDate, endDate);
        Set<User> users = deliveryInstances.stream()
                .flatMap(v -> v.getOrderItemDailyFoods().stream())
                .map(v -> v.getOrder().getUser())
                .collect(Collectors.toSet());
        return groupMapper.usersToDtos(users);
    }
}
