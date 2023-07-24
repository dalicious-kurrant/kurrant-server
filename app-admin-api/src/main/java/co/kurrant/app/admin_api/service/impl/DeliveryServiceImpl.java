package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.dalicious.client.core.filter.provider.SimpleJwtTokenProvider;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.entity.Driver;
import co.dalicious.domain.delivery.entity.enums.DeliveryStatus;
import co.dalicious.domain.delivery.repository.DeliveryInstanceRepository;
import co.dalicious.domain.delivery.repository.DriverRepository;
import co.dalicious.domain.delivery.repository.QDailyFoodDeliveryRepository;
import co.dalicious.domain.delivery.repository.QDeliveryInstanceRepository;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.repository.OrderItemDailyFoodRepository;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.Role;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.Code;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.kurrant.app.admin_api.dto.delivery.DeliveryStatusVo;
import co.kurrant.app.admin_api.dto.delivery.DeliveryVo;
import co.kurrant.app.admin_api.dto.user.LoginResponseDto;
import co.kurrant.app.admin_api.mapper.DeliveryMapper;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.MakersMapper;
import co.kurrant.app.admin_api.model.SecurityUser;
import co.kurrant.app.admin_api.service.DeliveryService;
import co.kurrant.app.admin_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryMapper deliveryMapper;
    private final QDailyFoodDeliveryRepository qDailyFoodDeliveryRepository;
    private final MakersRepository makersRepository;
    private final UserRepository userRepository;
    private final QDeliveryInstanceRepository qDeliveryInstanceRepository;
    private final MakersMapper makersMapper;
    private final GroupMapper groupMapper;
    private final QGroupRepository qGroupRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final SimpleJwtTokenProvider jwtTokenProvider;
    private final DriverRepository driverRepository;
    private final Map<BigInteger, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final TaskScheduler taskScheduler;
    private final DeliveryInstanceRepository deliveryInstanceRepository;
    private final OrderItemDailyFoodRepository orderItemDailyFoodRepository;


    @Override
    public LoginResponseDto login(Code loginCode) {
        Driver driver = driverRepository.findByCode(loginCode.getCode())
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));
        LoginTokenDto loginResponseDto = jwtTokenProvider.createToken(driver.getCode(), Collections.singletonList(Role.USER.getAuthority()));
        return new LoginResponseDto(loginResponseDto.getAccessToken(), loginResponseDto.getAccessTokenExpiredIn(), driver.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryVo getDelivery(String start, String end, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll) {
        LocalDate startDate = (start == null) ? null : DateUtils.stringToDate(start);
        LocalDate endDate = (end == null) ? null : DateUtils.stringToDate(end);

        List<Group> groupAllList = qGroupRepository.findAllExceptForMySpot();
        List<Group> groups = (groupIds == null) ? null : groupAllList.stream().filter(group -> groupIds.contains(group.getId())).toList();
        // 그룹과 연관된 스팟만 보여주기
        List<Spot> spotAllList = groups == null || groups.isEmpty() ? groupAllList.stream().flatMap(group -> group.getSpots().stream()).toList() : groups.stream().flatMap(group -> group.getSpots().stream()).toList();
        List<Spot> spots = (spotIds == null) ? null : spotAllList.stream().filter(spot -> spotIds.contains(spot.getId())).toList();

        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findByDailyFoodAndOrderStatus(startDate, endDate, groups, spots);

        if (orderItemDailyFoods.isEmpty()) return DeliveryVo.create(groupAllList, null, spotAllList);
        return DeliveryVo.create(groupAllList, deliveryMapper.getDeliveryInfoListByOrderItemDailyFood(orderItemDailyFoods), spotAllList);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryVo getDeliverySchedule(SecurityUser driver, String start, String end, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll) {
            String driverCode = UserUtil.getCode(driver) == null ? null : driver.getUsername().equals("admin") ? null : UserUtil.getCode(driver);

        LocalDate startDate = (start == null) ? null : DateUtils.stringToDate(start);
        LocalDate endDate = (end == null) ? null : DateUtils.stringToDate(end);

        List<DailyFoodDelivery> dailyFoodDeliveries = qDailyFoodDeliveryRepository.findAllFilterGroupAndSpot(startDate, endDate, groupIds, spotIds, driverCode);
        Set<DeliveryInstance> deliveryInstanceList = dailyFoodDeliveries.stream().map(DailyFoodDelivery::getDeliveryInstance).collect(Collectors.toSet());

        Set<Spot> spotAllList = deliveryInstanceList.stream().map(DeliveryInstance::getSpot).collect(Collectors.toSet());
        Set<Group> groupAllList = spotAllList.stream().map(Spot::getGroup).collect(Collectors.toSet());

        if (deliveryInstanceList.isEmpty()) return DeliveryVo.create(groupAllList, null, spotAllList);
        return DeliveryVo.create(groupAllList, deliveryMapper.getDeliveryInfoList(deliveryInstanceList, scheduledTasks), spotAllList);
    }

    @Override
    @Transactional
    public List<DeliveryVo.DeliveryManifest> getDeliveryManifest(Map<String, Object> parameters) {
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
    public Integer requestDeliveryComplete(SecurityUser securityUser, DeliveryStatusVo deliveryStatusVo) {
        final int cancelableTime = 60 * 1000;
        if (securityUser == null || securityUser.getUsername().equals("admin")) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        List<DeliveryInstance> deliveryInstances = qDeliveryInstanceRepository.findAllWaitDeliveryBySpotAndTimeAndDriver(deliveryStatusVo.getSpotId(), DateUtils.stringToLocalTime(deliveryStatusVo.getDeliveryTime()), securityUser.getUsername());
        for (DeliveryInstance deliveryInstance : deliveryInstances) {
            if (deliveryInstance.getDeliveryStatus().equals(DeliveryStatus.WAIT_DELIVERY)) {
                deliveryInstance.updateDeliveryStatus(DeliveryStatus.REQUEST_DELIVERED);

                ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(() -> finalizeDelivery(deliveryInstance.getId()), new Date(System.currentTimeMillis() + cancelableTime));
                scheduledTasks.put(deliveryInstance.getId(), scheduledFuture);
            }
        }
        return cancelableTime;
    }

    @Transactional
    public void finalizeDelivery(BigInteger deliveryInstanceId) {
        DeliveryInstance deliveryInstance = deliveryInstanceRepository.findById(deliveryInstanceId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        if (deliveryInstance.getDeliveryStatus().equals(DeliveryStatus.REQUEST_DELIVERED)) {
            deliveryInstance.updateDeliveryStatus(DeliveryStatus.DELIVERED);
            deliveryInstanceRepository.save(deliveryInstance);
            List<OrderItemDailyFood> orderItemDailyFoods = deliveryInstance.getOrderItemDailyFoods();
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                orderItemDailyFood = (OrderItemDailyFood) Hibernate.unproxy(orderItemDailyFood);
                orderItemDailyFood.updateOrderStatus(OrderStatus.DELIVERED);
            }
            orderItemDailyFoodRepository.saveAll(orderItemDailyFoods);
        }
    }

    @Override
    @Transactional
    public void cancelDeliveryComplete(SecurityUser securityUser, DeliveryStatusVo deliveryStatusVo) {
        if (securityUser == null || securityUser.getUsername().equals("admin")) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        List<DeliveryInstance> deliveryInstances = qDeliveryInstanceRepository.findAllRequestDeliveredBySpotAndTimeAndDriver(deliveryStatusVo.getSpotId(), DateUtils.stringToLocalTime(deliveryStatusVo.getDeliveryTime()), securityUser.getUsername());

        for (DeliveryInstance deliveryInstance : deliveryInstances) {
            if (deliveryInstance.getDeliveryStatus().equals(DeliveryStatus.REQUEST_DELIVERED)) {
                deliveryInstance.updateDeliveryStatus(DeliveryStatus.WAIT_DELIVERY);
            }

            ScheduledFuture<?> scheduledFuture = scheduledTasks.get(deliveryInstance.getId());
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
                scheduledTasks.remove(deliveryInstance.getId());
            }
        }

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
