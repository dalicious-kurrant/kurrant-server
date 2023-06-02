package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.alarm.entity.enums.AlarmType;
import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.PushAlarms;
import co.dalicious.client.alarm.repository.QPushAlarmsRepository;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.data.redis.entity.PushAlarmHash;
import co.dalicious.data.redis.repository.PushAlarmHashRepository;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.ApartmentRepository;
import co.dalicious.domain.client.repository.CorporationRepository;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QSpotRepository;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.dto.ServiceDiningDto;
import co.dalicious.domain.order.dto.ExtraOrderDto;
import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.mapper.DailyFoodSupportPriceMapper;
import co.dalicious.domain.order.mapper.ExtraOrderMapper;
import co.dalicious.domain.order.mapper.OrderDailyFoodByMakersMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.order.util.OrderMembershipUtil;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.user.converter.RefundPriceDto;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.*;
import co.dalicious.domain.user.repository.QMembershipRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.dalicious.domain.order.dto.OrderDto;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.MakersMapper;
import co.dalicious.domain.order.mapper.OrderMapper;
import co.kurrant.app.admin_api.service.OrderDailyFoodService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    private final PushAlarmHashRepository pushAlarmHashRepository;
    private final GroupRepository groupRepository;
    private final ApartmentRepository apartmentRepository;
    private final CorporationRepository corporationRepository;
    private final GroupMapper groupMapper;
    private final OrderMapper orderMapper;
    private final MakersMapper makersMapper;
    private final UserGroupRepository userGroupRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
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

    @Override
    @Transactional
    public List<OrderDto.OrderItemDailyFoodGroupList> retrieveOrder(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        BigInteger groupId = !parameters.containsKey("group") || parameters.get("group").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("group")));
        List<BigInteger> spotIds = !parameters.containsKey("spots") || parameters.get("spots").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("spots"));
        Integer diningTypeCode = !parameters.containsKey("diningType") || parameters.get("diningType").equals("") ? null : Integer.parseInt((String) parameters.get("diningType"));
        Long status = !parameters.containsKey("status") || parameters.get("status").equals("") ? null : Long.parseLong((String) parameters.get("status"));
        BigInteger userId = !parameters.containsKey("userId") || parameters.get("userId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("userId")));
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("makersId")));

        Group group = (groupId != null) ? groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND)) : null;
        Makers makers = (makersId != null) ? makersRepository.findById(makersId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS)) : null;
        OrderStatus orderStatus = status == null ? null : OrderStatus.ofCode(status);

        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllByGroupFilter(startDate, endDate, group, spotIds, diningTypeCode, userId, makers, orderStatus);
        List<Membership> memberships = qMembershipRepository.findAllByFilter(startDate, endDate, group, userId);

        return orderMapper.toOrderItemDailyFoodGroupList(orderItemDailyFoods, memberships);
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

        List<OrderItemDailyFood> orderItemDailyFoodList = qOrderDailyFoodRepository.findAllByMakersFilter(startDate, endDate, makers, diningTypes);

        return orderDailyFoodByMakersMapper.toDto(orderItemDailyFoodList);
    }

    @Override
    @Transactional
    public OrderDto.OrderDailyFoodDetail getOrderDetail(String orderCode) {
        Order order = orderRepository.findOneByCode(orderCode).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);
        return orderMapper.orderToDetailDto((OrderDailyFood) order, paymentCancelHistories);
    }

    @Override
    public List<GroupDto.Group> getGroup(Integer clientType) {
        List<? extends Group> groups = new ArrayList<>();
        if (clientType == null) {
            groups = groupRepository.findAll();
        } else if (ClientType.ofCode(clientType) == ClientType.MY_SPOT) {
            groups = apartmentRepository.findAll();
        } else if (ClientType.ofCode(clientType) == ClientType.CORPORATION) {
            groups = corporationRepository.findAll();
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
        List<UserGroup> userGroups = userGroupRepository.findAllByGroupAndClientStatus(group, ClientStatus.BELONG);
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
    public void changeOrderStatus(OrderDto.StatusAndIdList statusAndIdList) throws IOException, ParseException {
        OrderStatus orderStatus = OrderStatus.ofCode(statusAndIdList.getStatus());
        if (!OrderStatus.completePayment().contains(orderStatus)) {
            throw new ApiException(ExceptionEnum.CANNOT_CHANGE_STATUS);
        }
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllByIds(statusAndIdList.getIdList());
        Set<String> userPhoneNumber = new HashSet<>();
        List<PushRequestDtoByUser> pushRequestDtoByUsers = new ArrayList<>();
        List<PushAlarmHash> pushAlarmHashes = new ArrayList<>();

        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            if (!OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus())) {
                throw new ApiException(ExceptionEnum.CANNOT_CHANGE_STATUS);
            }
            orderItemDailyFood.updateOrderStatus(orderStatus);
            Optional<User> optionalUser = userRepository.findById(orderItemDailyFood.getOrder().getUser().getId());
            optionalUser.ifPresent(user -> userPhoneNumber.add(user.getPhone()));

            // 배송완료 푸시 알림 전송 및 멤버십 추가
            if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.DELIVERED) && OrderStatus.DELIVERED.getCode().equals(statusAndIdList.getStatus())) {
                // 멤버십 추가
                User user = orderItemDailyFood.getOrder().getUser();
                Group group = (Group) Hibernate.unproxy(orderItemDailyFood.getDailyFood().getGroup());
                if (user.getRole().equals(Role.USER) && group instanceof Corporation corporation && OrderUtil.isMembership(user, group) && !user.getIsMembership()) {
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
            }
        }
        pushService.sendToPush(pushRequestDtoByUsers);
        pushAlarmHashRepository.saveAll(pushAlarmHashes);

        /*
        String content = "안녕하세요!\n" +
                "조식 서비스를 운영 중인 커런트입니다.\n" +
                "\n" +
                "회원 가입 시, 동호수가 입력되지 않았습니다.\n" +
                "커런트 어플 내 왼쪽 상단바 (실선 3개) - 개인정보 - 이름(동호수) 정보 변경 부탁드립니다.\n" +
                "\n" +
                "동호수 미기입 시에는 배송이 누락될 수 있습니다.\n" +
                "\n" +
                "감사합니다.";
        for (String phone : userPhoneNumber){
            kaKaoUtil.sendAlimTalk(phone, content, "50074");
            System.out.println(phone + " phoneNumber");
        }
        */
    }

    @Override
    public void cancelOrderNice(BigInteger orderId) throws IOException, ParseException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ApiException(ExceptionEnum.ORDER_NOT_FOUND));
        User user = order.getUser();

        if (order instanceof OrderDailyFood orderDailyFood) {
            orderService.cancelOrderDailyFoodNice(orderDailyFood, user);
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public String cancelOrderItemsNice(List<BigInteger> orderItemList) throws IOException, ParseException {
        StringBuilder failMessage = new StringBuilder();
        List<OrderItem> orderItems = orderItemRepository.findAllByIds(orderItemList);
        for (OrderItem orderItem : orderItems) {
            User user = (User) Hibernate.unproxy(orderItem.getOrder().getUser());
            try {
                if (orderItem instanceof OrderItemDailyFood orderItemDailyFood) {
                    orderService.adminCancelOrderItemDailyFood(orderItemDailyFood, user);
                }
            } catch (Exception e) {
                // Log the exception or handle it as needed
                failMessage.append(user.getName()).append("님의 ").append(((OrderItemDailyFood) orderItem).getName()).append(" 상품이 취소되지 않았습니다. \n");
                log.info("Failed to cancel OrderItem ID: " + orderItem.getId() + ". Error: " + e.getMessage());
            }
        }
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

        Set<ServiceDiningDto> serviceDiningDtos = new HashSet<>();
        MultiValueMap<BigInteger, ExtraOrderDto.Request> requestMap = new LinkedMultiValueMap<>();
        for (ExtraOrderDto.Request orderDto : orderDtos) {
            requestMap.add(orderDto.getSpotId(), orderDto);

            LocalDate serviceDate = DateUtils.stringToDate(orderDto.getServiceDate());
            DiningType diningType = DiningType.ofString(orderDto.getDiningType());
            ServiceDiningDto serviceDiningDto = new ServiceDiningDto(serviceDate, diningType);
            serviceDiningDtos.add(serviceDiningDto);
        }
        PeriodDto periodDto = UserSupportPriceUtil.getEarliestAndLatestServiceDate(serviceDiningDtos);
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
            MultiValueMap<ServiceDiningDto, ExtraOrderDto.Request> orderDailyFoodGroupMap = new LinkedMultiValueMap<>();
            for (ExtraOrderDto.Request request : requestsBySpot) {
                ServiceDiningDto serviceDiningDto = new ServiceDiningDto(DateUtils.stringToDate(request.getServiceDate()), DiningType.ofString(request.getDiningType()));
                orderDailyFoodGroupMap.add(serviceDiningDto, request);
            }

            for (ServiceDiningDto serviceDiningDto : orderDailyFoodGroupMap.keySet()) {
                // 7. OrderItemDailyFoodGroup 저장
                OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFoodGroupRepository.save(orderMapper.toOrderItemDailyFoodGroup(serviceDiningDto));

                List<ExtraOrderDto.Request> requests = orderDailyFoodGroupMap.get(serviceDiningDto);
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

                    // 멤버십이 가입된 기업은 할인된 가격으로 적용하기
                    DiscountDto discountDto;
                    if (Hibernate.unproxy(spot.getGroup()) instanceof Corporation corporation && corporation.getIsMembershipSupport()) {
                        discountDto = DiscountDto.getDiscount(dailyFood.getFood());
                    } else {
                        discountDto = DiscountDto.getDiscountWithoutMembership(dailyFood.getFood());
                    }

                    // 8. 주문 상품(OrderItemDailyFood) 저장
                    OrderItemDailyFood orderItemDailyFood = orderItemDailyFoodRepository.save(orderMapper.toExtraOrderItemEntity(order, dailyFood, request, discountDto, orderItemDailyFoodGroup));
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
        List<OrderItemDailyFood> orderDailyFoods = qOrderDailyFoodRepository.findExtraOrdersByManagerId(userIds, startDate, endDate, group);

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
    }


    @Override
    @Transactional
    public void cancelOrderToss(BigInteger orderId) throws IOException, ParseException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ApiException(ExceptionEnum.ORDER_NOT_FOUND));
        User user = order.getUser();

        if (order instanceof OrderDailyFood orderDailyFood) {
            orderService.cancelOrderDailyFood(orderDailyFood, user);
        }
    }

    @Override
    @Transactional
    public void cancelOrderItemsToss(List<BigInteger> orderItemList) {
        List<OrderItem> orderItems = orderItemRepository.findAllByIds(orderItemList);

        for (OrderItem orderItem : orderItems) {
            try {
                User user = orderItem.getOrder().getUser();
                if (orderItem instanceof OrderItemDailyFood orderItemDailyFood) {
                    orderService.cancelOrderItemDailyFood(orderItemDailyFood, user);
                }
            } catch (Exception e) {
                // Log the exception or handle it as needed
                log.info("Failed to cancel OrderItem ID: " + orderItem.getId() + ". Error: " + e.getMessage());
            }
        }
    }
}
