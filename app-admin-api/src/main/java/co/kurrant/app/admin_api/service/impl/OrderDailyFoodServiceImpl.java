package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.PushAlarms;
import co.dalicious.client.alarm.entity.enums.AlarmType;
import co.dalicious.client.alarm.repository.QPushAlarmsRepository;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.data.redis.dto.SseReceiverDto;
import co.dalicious.data.redis.entity.PushAlarmHash;
import co.dalicious.data.redis.event.ReloadEvent;
import co.dalicious.data.redis.repository.PushAlarmHashRepository;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.client.repository.QSpotRepository;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.mappper.DeliveryInstanceMapper;
import co.dalicious.domain.delivery.repository.QDeliveryInstanceRepository;
import co.dalicious.domain.delivery.utils.DeliveryUtils;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.FoodCapacity;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.food.repository.QFoodCapacityRepository;
import co.dalicious.domain.order.dto.*;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.mapper.DailyFoodSupportPriceMapper;
import co.dalicious.domain.order.mapper.ExtraOrderMapper;
import co.dalicious.domain.order.mapper.OrderDailyFoodByMakersMapper;
import co.dalicious.domain.order.mapper.OrderMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.order.util.OrderMembershipUtil;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.user.converter.RefundPriceDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.domain.user.entity.enums.Role;
import co.dalicious.domain.user.entity.enums.UserStatus;
import co.dalicious.domain.user.repository.QMembershipRepository;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DiningTypesUtils;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.MakersMapper;
import co.kurrant.app.admin_api.service.OrderDailyFoodService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    private final PushAlarmHashRepository pushAlarmHashRepository;
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final OrderMapper orderMapper;
    private final MakersMapper makersMapper;
    private final QOrderItemDailyFoodRepository qOrderItemDailyFoodRepository;
    private final MakersRepository makersRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;
    private final OrderDailyFoodByMakersMapper orderDailyFoodByMakersMapper;
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;
    private final UserRepository userRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final ExtraOrderMapper extraOrderMapper;
    private final QSpotRepository qSpotRepository;
    private final OrderDailyFoodRepository orderDailyFoodRepository;
    private final OrderItemDailyFoodGroupRepository orderItemDailyFoodGroupRepository;
    private final OrderItemDailyFoodRepository orderItemDailyFoodRepository;
    private final DailyFoodSupportPriceRepository dailyFoodSupportPriceRepository;
    private final DailyFoodSupportPriceMapper dailyFoodSupportPriceMapper;
    private final QUserRepository qUserRepository;
    private final OrderDailyFoodUtil orderDailyFoodUtil;
    private final QPushAlarmsRepository qPushAlarmsRepository;
    private final PushUtil pushUtil;
    private final OrderMembershipUtil orderMembershipUtil;
    private final PushService pushService;
    private final QMembershipRepository qMembershipRepository;
    private final QGroupRepository qGroupRepository;
    private final DeliveryUtils deliveryUtils;
    private final QDeliveryInstanceRepository qDeliveryInstanceRepository;
    private final DeliveryInstanceMapper deliveryInstanceMapper;
    private final QUserGroupRepository qUserGroupRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final QFoodCapacityRepository qFoodCapacityRepository;

    @Override
    @Transactional
    public List<OrderDto.OrderItemDailyFoodGroupList> retrieveOrder(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        BigInteger groupId = !parameters.containsKey("group") || parameters.get("group").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("group")));
        List<BigInteger> spotIds = !parameters.containsKey("spots") || parameters.get("spots").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("spots"));
        Integer diningTypeCode = !parameters.containsKey("diningType") || parameters.get("diningType").equals("") ? null : Integer.parseInt((String) parameters.get("diningType"));
        Integer spotType = !parameters.containsKey("spotType") || parameters.get("spotType").equals("") ? null : Integer.parseInt((String) parameters.get("spotType"));
        Long status = !parameters.containsKey("status") || parameters.get("status").equals("") ? null : Long.parseLong((String) parameters.get("status"));
        BigInteger userId = !parameters.containsKey("userId") || parameters.get("userId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("userId")));
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("makersId")));

        Group group = (groupId != null) ? groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND)) : null;
        Makers makers = (makersId != null) ? makersRepository.findById(makersId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS)) : null;
        OrderStatus orderStatus = status == null ? null : OrderStatus.ofCode(status);

        List<SelectOrderDailyFoodDto> selectOrderDailyFoodDtos = qOrderItemDailyFoodRepository.findSelectDtoByGroupFilter(startDate, endDate, spotType, group, spotIds, diningTypeCode, userId, makers, orderStatus);
        List<BigInteger> memberships = qMembershipRepository.findAllUserIdByFilter(startDate, endDate, group, userId);

        return orderMapper.toOrderItemDailyFoodGroupLists(selectOrderDailyFoodDtos, memberships);
    }

    @Override
    @Transactional
    public OrderDailyFoodByMakersDto.ByPeriod retrieveOrderByMakers(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<Integer> diningTypes = !parameters.containsKey("diningTypes") || parameters.get("diningTypes").equals("") ? null : StringUtils.parseIntegerList((String) parameters.get("diningTypes"));
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("makersId")));

        assert makersId != null;
        Makers makers = makersRepository.findById(makersId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));
        List<FoodCapacity> foodCapacities = qFoodCapacityRepository.getFoodCapacitiesByMakers(makers);

        // FIXME: 기존 로직
        List<OrderItemDailyFood> orderItemDailyFoodList = qOrderItemDailyFoodRepository.findAllByMakersFilter(startDate, endDate, makers, diningTypes);
        return orderDailyFoodByMakersMapper.toDto(orderItemDailyFoodList, foodCapacities);
    }

    @Override
    @Transactional
    public OrderDailyFoodByMakersDto.ByPeriod retrieveOrderCountByMakersAndDelivery(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<Integer> diningTypes = !parameters.containsKey("diningTypes") || parameters.get("diningTypes").equals("") ? null : StringUtils.parseIntegerList((String) parameters.get("diningTypes"));
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("makersId")));

        assert makersId != null;
        Makers makers = makersRepository.findById(makersId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));
        List<FoodCapacity> foodCapacities = qFoodCapacityRepository.getFoodCapacitiesByMakers(makers);

        // FIXME: 배송 도메인 추가 로직
        List<DeliveryInstance> deliveryInstances = qDeliveryInstanceRepository.findByFilter(startDate, endDate, DiningTypesUtils.codesToDiningTypes(diningTypes), makers);
        return deliveryInstanceMapper.toDto(deliveryInstances, foodCapacities);
    }

    @Override
    @Transactional
    public OrderDto.OrderDailyFoodDetail getOrderDetail(String orderCode) {
        Order order = orderRepository.findOneByCode(orderCode).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);
        return orderMapper.orderToDetailDto((OrderDailyFood) order, paymentCancelHistories);
    }

    @Override
    public List<GroupDto.Group> getGroup(Integer spotType) {
        List<? extends Group> groups = new ArrayList<>();
        if (spotType == null) {
            groups = groupRepository.findAll();
        } else {
            groups = qGroupRepository.findGroupByType(GroupDataType.ofCode(spotType));
        }
        return groupMapper.groupsToDtos(groups);
    }

    @Override
    @Transactional
    public GroupDto getGroupInfo(BigInteger groupId) {
        if (groupId == null) {
            List<User> users = userRepository.findAllByUserStatus(UserStatus.ACTIVE);
            return groupMapper.groupToGroupDto(null, users);
        }

        List<User> users = new ArrayList<>();
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND)
        );
        List<UserGroup> userGroups =  qUserGroupRepository.findAllByGroupAndClientStatus(groupId);
        for (UserGroup userGroup : userGroups) {
            users.add(userGroup.getUser());
        }
        return groupMapper.groupToGroupDto(group, users);
    }

    @Override
    public List<MakersDto.Makers> getMakers() {
        return makersMapper.makersToDtos(makersRepository.findAll());
    }

    @Override
    @Transactional
    public void changeOrderStatus(OrderDto.StatusAndIdList statusAndIdList) {
        OrderStatus orderStatus = OrderStatus.ofCode(statusAndIdList.getStatus());
        if (!OrderStatus.completePayment().contains(orderStatus)) {
            throw new ApiException(ExceptionEnum.CANNOT_CHANGE_STATUS);
        }
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderItemDailyFoodRepository.findAllByIds(statusAndIdList.getIdList());
        Set<String> userPhoneNumber = new HashSet<>();
        List<PushRequestDtoByUser> pushRequestDtoByUsers = new ArrayList<>();
        List<PushAlarmHash> pushAlarmHashes = new ArrayList<>();

        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            if (!OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus())) {
                throw new ApiException(ExceptionEnum.CANNOT_CHANGE_STATUS);
            }
            OrderStatus defaultOrderStatus = orderItemDailyFood.getOrderStatus();
            orderItemDailyFood.updateOrderStatus(orderStatus);
            Optional<User> optionalUser = userRepository.findById(orderItemDailyFood.getOrder().getUser().getId());
            optionalUser.ifPresent(user -> userPhoneNumber.add(user.getPhone()));

            // 배송완료 푸시 알림 전송 및 멤버십 추가
            if (!defaultOrderStatus.equals(OrderStatus.DELIVERED) && OrderStatus.DELIVERED.getCode().equals(statusAndIdList.getStatus())) {
                // 멤버십 추가
                User user = orderItemDailyFood.getOrder().getUser();
                Group group = (Group) Hibernate.unproxy(orderItemDailyFood.getDailyFood().getGroup());
                if (user.getRole().equals(Role.USER) && group instanceof Corporation corporation && OrderUtil.isCorporationMembership(user, group) && !user.getIsMembership()) {
                    orderMembershipUtil.joinCorporationMembership(user, corporation);
                }

                // 배송 완료 푸시알림 전송
                PushAlarms pushAlarms = qPushAlarmsRepository.findByPushCondition(PushCondition.DELIVERED_ORDER_ITEM);
                String userName = user.getName();
                String foodName = orderItemDailyFood.getName();
                String spotName = orderItemDailyFood.getDailyFood().getGroup().getName();
                String message = PushUtil.getContextDeliveredOrderItem(pushAlarms.getMessage(), userName, foodName, spotName);
                PushRequestDtoByUser pushRequestDtoByUser = pushUtil.getPushRequest(user, PushCondition.DELIVERED_ORDER_ITEM, message);
                if (pushRequestDtoByUser != null) {
                    pushRequestDtoByUsers.add(pushRequestDtoByUser);
                }
                PushAlarmHash pushAlarmHash = PushAlarmHash.builder()
                        .title(PushCondition.DELIVERED_ORDER_ITEM.getTitle())
                        .isRead(false)
                        .message(message)
                        .userId(user.getId())
                        .type(AlarmType.ORDER_STATUS.getAlarmType())
                        .build();
                pushAlarmHashes.add(pushAlarmHash);
                applicationEventPublisher.publishEvent(SseReceiverDto.builder().receiver(user.getId()).type(6).build());
            }
        }
        pushService.sendToPush(pushRequestDtoByUsers);
        pushAlarmHashRepository.saveAll(pushAlarmHashes);
    }

    @Override
    public void cancelOrder(BigInteger orderId) throws IOException, ParseException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ApiException(ExceptionEnum.ORDER_NOT_FOUND));
        User user = order.getUser();
        if (order instanceof OrderDailyFood orderDailyFood) {
            Set<BigInteger> makersIds = orderService.cancelOrderDailyFood(orderDailyFood, user);
            applicationEventPublisher.publishEvent(new ReloadEvent(makersIds));
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String cancelOrderItems(List<BigInteger> orderItemList) {
        StringBuilder failMessage = new StringBuilder();
        List<OrderItem> orderItems = orderItemRepository.findAllByIds(orderItemList);
        Set<BigInteger> makersIds = new HashSet<>();
        for (OrderItem orderItem : orderItems) {
            User user = (User) Hibernate.unproxy(orderItem.getOrder().getUser());
            try {
                if (orderItem instanceof OrderItemDailyFood orderItemDailyFood) {
                    orderService.adminCancelOrderItemDailyFood(orderItemDailyFood, user);
                    makersIds.add(orderItemDailyFood.getDailyFood().getFood().getMakers().getId());
                }
            } catch (Exception e) {
                failMessage.append(user.getName()).append("님의 ").append(((OrderItemDailyFood) orderItem).getName()).append(" 상품이 취소되지 않았습니다. \n");
                log.info("Failed to cancel OrderItem ID: " + orderItem.getId() + ". Error: " + e.getMessage());
            }
        }
        applicationEventPublisher.publishEvent(new ReloadEvent(makersIds));
        return failMessage.toString();
    }

    @Override
    @Transactional
    public List<ExtraOrderDto.DailyFoodList> getExtraDailyFoods(LocalDate startDate, LocalDate endDate, BigInteger groupId) {
        Group group = groupId == null ? null : groupRepository.findById(groupId).orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

        List<DailyFood> dailyFoods = qDailyFoodRepository.getDailyFoodsBetweenServiceDate(startDate, endDate, group);
        Map<DailyFood, Integer> remainFoodCount = orderDailyFoodUtil.getRemainFoodsCount(dailyFoods);
        return extraOrderMapper.toDailyFoodList(dailyFoods, remainFoodCount);
    }

    @Override
    @Transactional
    public void postExtraOrderItems(List<ExtraOrderDto.Request> orderDtos) {
        // 1. 식단 추가하는 달리셔스 매니저 가져오기
        User user = userRepository.findOneByRole(Role.ADMIN)
                .orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));
        // 2. 추가하는 식단 가져오기
        Set<BigInteger> foodIds = orderDtos.stream()
                .map(ExtraOrderDto.Request::getFoodId)
                .collect(Collectors.toSet());
        Set<BigInteger> makersIds = new HashSet<>();

        Set<ServiceDiningVo> serviceDiningVos = new HashSet<>();
        MultiValueMap<BigInteger, ExtraOrderDto.Request> requestMap = new LinkedMultiValueMap<>();
        for (ExtraOrderDto.Request orderDto : orderDtos) {
            requestMap.add(orderDto.getSpotId(), orderDto);

            LocalDate serviceDate = DateUtils.stringToDate(orderDto.getServiceDate());
            DiningType diningType = DiningType.ofString(orderDto.getDiningType());
            ServiceDiningVo serviceDiningVo = new ServiceDiningVo(serviceDate, diningType);
            serviceDiningVos.add(serviceDiningVo);
        }
        PeriodDto periodDto = UserSupportPriceUtil.getEarliestAndLatestServiceDate(serviceDiningVos);
        List<DailyFood> dailyFoods = qDailyFoodRepository.findAllByFoodsBetweenServiceDate(periodDto.getStartDate(), periodDto.getEndDate(), foodIds);

        // 3. 음식을 추가하는 스팟 가져오기
        Set<BigInteger> spotIds = orderDtos.stream()
                .map(ExtraOrderDto.Request::getSpotId)
                .collect(Collectors.toSet());
        List<Spot> spots = qSpotRepository.findAllByIds(spotIds);

        // 4. 식단을 스팟별로 정렬
        for (BigInteger bigInteger : requestMap.keySet()) {
            Spot spot = spots.stream()
                    .filter(v -> v.getId().equals(bigInteger))
                    .findAny()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));

            List<ExtraOrderDto.Request> requestsBySpot = requestMap.get(bigInteger);

            // 5. 주문서 저장
            String code = OrderUtil.generateOrderCode(OrderType.DAILYFOOD, user.getId());
            OrderDailyFood order = orderDailyFoodRepository.save(orderMapper.toExtraOrderEntity(user, spot, code));

            BigDecimal defaultPrice = BigDecimal.ZERO;

            // 6. 식사일정별로 DailyFood 묶기 (OrderItemDailyFoodGroup)
            MultiValueMap<ServiceDiningVo, ExtraOrderDto.Request> orderDailyFoodGroupMap = new LinkedMultiValueMap<>();
            for (ExtraOrderDto.Request request : requestsBySpot) {
                ServiceDiningVo serviceDiningVo = new ServiceDiningVo(DateUtils.stringToDate(request.getServiceDate()), DiningType.ofString(request.getDiningType()));
                orderDailyFoodGroupMap.add(serviceDiningVo, request);
            }

            for (ServiceDiningVo serviceDiningVo : orderDailyFoodGroupMap.keySet()) {
                // 7. OrderItemDailyFoodGroup 저장
                OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFoodGroupRepository.save(orderMapper.toOrderItemDailyFoodGroup(serviceDiningVo));

                List<ExtraOrderDto.Request> requests = orderDailyFoodGroupMap.get(serviceDiningVo);
                List<OrderItemDailyFood> orderItemDailyFoods = new ArrayList<>();
                assert requests != null;
                BigDecimal supportPrice = BigDecimal.ZERO;
                for (ExtraOrderDto.Request request : requests) {
                    DailyFood dailyFood = dailyFoods.stream()
                            .filter(v -> v.getServiceDate().equals(DateUtils.stringToDate(request.getServiceDate())) &&
                                    v.getDiningType().equals(DiningType.ofString(request.getDiningType())) &&
                                    v.getFood().getId().equals(request.getFoodId()) &&
                                    v.getGroup().getId().equals(request.getGroupId()))
                            .findAny()
                            .orElse(null);
                    assert dailyFood != null;
                    makersIds.add(dailyFood.getFood().getMakers().getId());

                    // 멤버십이 가입된 기업은 할인된 가격으로 적용하기
                    DiscountDto discountDto;
                    if (Hibernate.unproxy(spot.getGroup()) instanceof Corporation corporation && corporation.getIsMembershipSupport()) {
                        discountDto = DiscountDto.getDiscount(dailyFood.getFood());
                    } else {
                        discountDto = DiscountDto.getDiscountWithoutMembership(dailyFood.getFood());
                    }

                    // 8. 주문 상품(OrderItemDailyFood) 저장
                    LocalTime tempDeliveryTime = spot.getMealInfo(dailyFood.getDiningType()).getDeliveryTimes().get(0);
                    OrderItemDailyFood orderItemDailyFood = orderItemDailyFoodRepository.save(orderMapper.toExtraOrderItemEntity(order, dailyFood, request, discountDto, orderItemDailyFoodGroup, tempDeliveryTime));
                    // 배송정보 입력
                    // TODO: 배송시간 추가
                    deliveryUtils.saveDeliveryInstance(orderItemDailyFood, spot, user, dailyFood, tempDeliveryTime);
                    orderItemDailyFoods.add(orderItemDailyFood);
                    defaultPrice = defaultPrice.add(dailyFood.getFood().getPrice().multiply(BigDecimal.valueOf(request.getCount())));
                    supportPrice = supportPrice.add(orderItemDailyFood.getOrderItemTotalPrice());
                }
                DailyFoodSupportPrice dailyFoodSupportPrice = dailyFoodSupportPriceMapper.toEntity(orderItemDailyFoods.get(0), supportPrice);
                // 9. 사용 지원금(DailyFoodSupportPrice) 저장
                dailyFoodSupportPriceRepository.save(dailyFoodSupportPrice);
            }

            order.updateDefaultPrice(defaultPrice);
            order.updateTotalPrice(BigDecimal.ZERO);
            order.updateTotalDeliveryFee(BigDecimal.ZERO);
            order.updatePoint(BigDecimal.ZERO);
        }
        applicationEventPublisher.publishEvent(new ReloadEvent(makersIds));
    }

    @Override
    @Transactional
    public List<ExtraOrderDto.Response> getExtraOrders(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        Group group = !parameters.containsKey("groupId") || parameters.get("groupId") == null ? null :
                groupRepository.findById(BigInteger.valueOf(Integer.parseInt(String.valueOf(parameters.get("groupId"))))).orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

        List<User> users = qUserRepository.findAllManager();
        List<BigInteger> userIds = users.stream()
                .map(User::getId)
                .toList();
        List<OrderItemDailyFood> orderDailyFoods = qOrderItemDailyFoodRepository.findExtraOrdersByManagerId(userIds, startDate, endDate, group);

        return extraOrderMapper.toExtraOrderDtos(orderDailyFoods);
    }

    @Override
    @Transactional
    public void refundExtraOrderItems(BigInteger id) {
        OrderItemDailyFood orderItemDailyFood = orderItemDailyFoodRepository.findById(id)
                .orElseThrow(() -> new ApiException(ExceptionEnum.ORDER_NOT_FOUND));

        RefundPriceDto refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, null, null);

        BigDecimal usedSupportPrice = orderItemDailyFood.getOrderItemDailyFoodGroup().getUsingSupportPrice();

        if (!refundPriceDto.isSameSupportPrice(usedSupportPrice)) {
            List<DailyFoodSupportPrice> userSupportPriceHistories = orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories();
            for (DailyFoodSupportPrice dailyFoodSupportPrice : userSupportPriceHistories) {
                dailyFoodSupportPrice.updateMonetaryStatus(MonetaryStatus.REFUND);
            }
            DailyFoodSupportPrice dailyFoodSupportPrice = dailyFoodSupportPriceMapper.toEntity(orderItemDailyFood, refundPriceDto.getRenewSupportPrice());
            if (dailyFoodSupportPrice.getUsingSupportPrice().compareTo(BigDecimal.ZERO) != 0) {
                dailyFoodSupportPriceRepository.save(dailyFoodSupportPrice);
            }
        }

        orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);
        if (refundPriceDto.getIsLastItemOfGroup()) {
            orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
        }
        applicationEventPublisher.publishEvent(new ReloadEvent(Collections.singletonList(orderItemDailyFood.getDailyFood().getFood().getMakers().getId())));
    }
}
