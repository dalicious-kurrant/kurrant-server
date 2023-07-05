package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.sse.SseService;
import co.dalicious.data.redis.entity.NotificationHash;
import co.dalicious.data.redis.repository.NotificationHashRepository;
import co.dalicious.domain.client.entity.CorporationSpot;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.mappper.DeliveryInstanceMapper;
import co.dalicious.domain.delivery.repository.DailyFoodDeliveryRepository;
import co.dalicious.domain.delivery.repository.DeliveryInstanceRepository;
import co.dalicious.domain.delivery.repository.QDeliveryInstanceRepository;
import co.dalicious.domain.delivery.utils.DeliveryUtils;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.dto.*;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.mapper.*;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.entity.enums.PaymentCompany;
import co.dalicious.domain.payment.repository.CreditCardInfoRepository;
import co.dalicious.domain.payment.util.NiceUtil;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.entity.enums.PointStatus;
import co.dalicious.domain.user.mapper.FoundersMapper;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.repository.QFoundersRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.util.FoundersUtil;
import co.dalicious.domain.user.util.PointUtil;
import co.dalicious.domain.user.util.UserGroupUtil;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.public_api.dto.order.OrderByServiceDateNotyDto;
import co.kurrant.app.public_api.dto.order.OrderCardQuotaDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.OrderDailyFoodService;
import co.kurrant.app.public_api.service.UserUtil;
import exception.ApiException;
import exception.CustomException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    private final UserUtil userUtil;
    private final TossUtil tossUtil;
    private final NiceUtil niceUtil;
    private final SpotRepository spotRepository;
    private final QCartDailyFoodRepository qCartDailyFoodRepository;
    private final DeliveryFeePolicy deliveryFeePolicy;
    private final OrderMapper orderMapper;
    private final OrderDailyFoodItemMapper orderDailyFoodItemMapper;
    private final OrderDailyFoodRepository orderDailyFoodRepository;
    private final QOrderRepository qOrderRepository;
    private final OrderItemDailyFoodRepository orderItemDailyFoodRepository;
    private final DailyFoodSupportPriceMapper dailyFoodSupportPriceMapper;
    private final DailyFoodSupportPriceRepository dailyFoodSupportPriceRepository;
    private final QDailyFoodSupportPriceRepository qDailyFoodSupportPriceRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final OrderItemDailyFoodListMapper orderItemDailyFoodListMapper;
    private final OrderDailyFoodHistoryMapper orderDailyFoodHistoryMapper;
    private final OrderDailyFoodDetailMapper orderDailyFoodDetailMapper;
    private final OrderItemDailyFoodGroupRepository orderItemDailyFoodGroupRepository;
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;
    private final OrderUtil orderUtil;
    private final OrderRepository orderRepository;
    private final SseService sseService;
    private final NotificationHashRepository notificationHashRepository;
    private final OrderDailyFoodUtil orderDailyFoodUtil;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final CreditCardInfoRepository creditCardInfoRepository;
    private final OrderService orderService;
    private final PointUtil pointUtil;
    private final QFoundersRepository qFoundersRepository;
    private final CartDailyFoodRepository cartDailyFoodRepository;
    private final QUserRepository qUserRepository;
    private final DeliveryUtils deliveryUtils;
    private final ConcurrentHashMap<User, Object> userLocks = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public BigInteger orderDailyFoodsNice(SecurityUser securityUser, OrderItemDailyFoodByNiceReqDto orderItemDailyFoodReqDto) throws IOException, ParseException {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);
        synchronized (userLocks.computeIfAbsent(user, u -> new Object())) {
            // 그룹/스팟 정보 가져오기
            Spot spot = spotRepository.findById(orderItemDailyFoodReqDto.getOrderItems().getSpotId()).orElseThrow(
                    () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
            );
            Group group = spot.getGroup();
            // 유저가 그 그룹의 스팟에 포함되는지 확인.
            UserGroupUtil.isUserIncludedInGroup(user, group);

            // 사용 요청 포인트가 유저가 현재 가지고 있는 포인트보다 적은지 검증
            if (orderItemDailyFoodReqDto.getOrderItems().getUserPoint().compareTo(user.getPoint()) > 0) {
                throw new ApiException(ExceptionEnum.HAS_LESS_POINT_THAN_REQUEST);
            }

            Set<ServiceDiningDto> serviceDiningDtos = new HashSet<>();
            List<OrderItemDailyFood> orderItemDailyFoods = new ArrayList<>();
            List<BigInteger> cartDailyFoodIds = new ArrayList<>();
            BigDecimal defaultPrice = BigDecimal.ZERO;
            BigDecimal totalDeliveryFee = BigDecimal.ZERO;
            BigDecimal totalSupportPrice = BigDecimal.ZERO;
            BigDecimal totalDailyFoodPrice = BigDecimal.ZERO;

            // 식사타입(DiningType)과 날짜별(serviceDate) 식사들 가져오기
            List<CartDailyFoodDto> cartDailyFoodDtoList = orderItemDailyFoodReqDto.getOrderItems().getCartDailyFoodDtoList();
            // 프론트에서 제공한 정보와 실제 정보가 일치하는지 확인
            for (CartDailyFoodDto cartDailyFoodDto : cartDailyFoodDtoList) {
                // 배송비 일치 점증 및 배송비 계산
                if (cartDailyFoodDto.getDeliveryFee().compareTo(deliveryFeePolicy.getGroupDeliveryFee(user, group)) != 0) {
                    throw new ApiException(ExceptionEnum.NOT_MATCHED_DELIVERY_FEE);
                }
                totalDeliveryFee = totalDeliveryFee.add(cartDailyFoodDto.getDeliveryFee());

                serviceDiningDtos.add(new ServiceDiningDto(DateUtils.stringToDate(cartDailyFoodDto.getServiceDate()), DiningType.ofString(cartDailyFoodDto.getDiningType())));

                for (CartDailyFoodDto.DailyFood dailyFood : cartDailyFoodDto.getCartDailyFoods()) {
                    cartDailyFoodIds.add(dailyFood.getId());
                }
            }

            // 1. 주문서 저장하기
            OrderDailyFood orderDailyFood = orderDailyFoodRepository.save(orderMapper.toEntity(user, spot, orderItemDailyFoodReqDto.getOrderId(), orderItemDailyFoodReqDto.getPhone(), orderItemDailyFoodReqDto.getMemo()));

            // ServiceDate의 가장 빠른 날짜와 늦은 날짜 구하기
            PeriodDto periodDto = UserSupportPriceUtil.getEarliestAndLatestServiceDate(serviceDiningDtos);

            List<CartDailyFood> cartDailyFoods = qCartDailyFoodRepository.findAllByFoodIds(cartDailyFoodIds);

            for (CartDailyFoodDto cartDailyFoodDto : cartDailyFoodDtoList) {
                // 3. 배송일과 지원금 저장하기
                OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFoodGroupRepository.save(orderDailyFoodItemMapper.dtoToOrderItemDailyFoodGroup(cartDailyFoodDto));
                OrderItemDailyFood orderItemDailyFood = null;
                BigDecimal orderItemGroupTotalPrice = BigDecimal.ZERO;
                BigDecimal supportPrice = getSupportPrice(user, spot, cartDailyFoodDto, periodDto);
                // 4. 주문 음식 가격이 일치하는지 검증 및 주문 저장
                for (CartDailyFoodDto.DailyFood cartDailyFood : cartDailyFoodDto.getCartDailyFoods()) {
                    CartDailyFood selectedCartDailyFood = cartDailyFoods.stream().filter(v -> v.getId().equals(cartDailyFood.getId()))
                            .findAny()
                            .orElseThrow(() -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND));
                    checkPriceValidation(user, group, spot, cartDailyFood, selectedCartDailyFood);
                    orderItemDailyFood = orderDailyFoodItemMapper.dtoToOrderItemDailyFood(cartDailyFood, selectedCartDailyFood, orderDailyFood, orderItemDailyFoodGroup);
                    orderItemDailyFoods.add(orderItemDailyFoodRepository.save(orderItemDailyFood));

                    // 배송정보 입력
                    deliveryUtils.saveDeliveryInstance(orderItemDailyFood, spot, user, selectedCartDailyFood.getDailyFood(), selectedCartDailyFood.getDeliveryTime());

                    defaultPrice = defaultPrice.add(selectedCartDailyFood.getDailyFood().getDefaultPrice().multiply(BigDecimal.valueOf(cartDailyFood.getCount())));
                    BigDecimal dailyFoodPrice = cartDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(cartDailyFood.getCount()));
                    orderItemGroupTotalPrice = orderItemGroupTotalPrice.add(dailyFoodPrice);
                    totalDailyFoodPrice = totalDailyFoodPrice.add(dailyFoodPrice);

                    // 주문 가능 수량이 일치하는지 확인
                    FoodCountDto foodCountDto = orderDailyFoodUtil.getRemainFoodCount(selectedCartDailyFood.getDailyFood());
                    checkFoodCount(foodCountDto, cartDailyFood, selectedCartDailyFood);
                }

                // 5. 지원금 사용 저장
                if (spot instanceof CorporationSpot) {
                    BigDecimal usableSupportPrice = UserSupportPriceUtil.getUsableSupportPrice(orderItemGroupTotalPrice, supportPrice);
                    if (usableSupportPrice.compareTo(BigDecimal.ZERO) != 0) {
                        DailyFoodSupportPrice dailyFoodSupportPrice;
                        if (spot.getGroup().getName().contains("메드트로닉")) {
                            dailyFoodSupportPrice = dailyFoodSupportPriceMapper.toMedTronicSupportPrice(orderItemDailyFood, orderItemGroupTotalPrice);
                        } else {
                            dailyFoodSupportPrice = dailyFoodSupportPriceMapper.toEntity(orderItemDailyFood, usableSupportPrice);
                        }
                        dailyFoodSupportPriceRepository.save(dailyFoodSupportPrice);
                        totalSupportPrice = totalSupportPrice.add(dailyFoodSupportPrice.getUsingSupportPrice());
                    }
                }
            }

            // 결제 금액 (배송비 + 할인된 상품 가격의 합) - (회사 지원금 - 포인트 사용)
            BigDecimal payPrice = totalDailyFoodPrice.add(totalDeliveryFee).subtract(totalSupportPrice).subtract(orderItemDailyFoodReqDto.getOrderItems().getUserPoint());

            if (payPrice.compareTo(orderItemDailyFoodReqDto.getOrderItems().getTotalPrice()) != 0 || totalSupportPrice.compareTo(orderItemDailyFoodReqDto.getOrderItems().getSupportPrice()) != 0) {
                throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
            }

            // 결제 금액이 0이 아닐 경우, 나이스페이를 통해 결제
            if (orderItemDailyFoodReqDto.getAmount() != 0) {
                CreditCardInfo creditCardInfo = creditCardInfoRepository.findById(orderItemDailyFoodReqDto.getCardId()).orElseThrow(() -> new ApiException(ExceptionEnum.CARD_NOT_FOUND));
                JSONObject jsonObject = orderService.payDailyFood(user, creditCardInfo, orderItemDailyFoodReqDto.getAmount(), orderItemDailyFoodReqDto.getOrderId(), orderItemDailyFoodReqDto.getOrderName());
                // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
                Long code = (Long) jsonObject.get("code");
                JSONObject response = (JSONObject) jsonObject.get("response");
                String status = response.get("status").toString();

                if (code == 0 && !status.equals("failed")) {
                    // 주문서 내용 업데이트 및 사용 포인트 차감
                    updateOrderDailyFood(orderDailyFood, orderItemDailyFoods, defaultPrice, orderItemDailyFoodReqDto.getOrderItems().getUserPoint(), payPrice, totalDeliveryFee);
                    pointUtil.updateUserPoint(user, orderDailyFood.getId(), orderItemDailyFoodReqDto.getOrderItems().getUserPoint());

                    //Order 테이블에 paymentKey와 receiptUrl 업데이트
                    String receiptUrl = response.get("receipt_url").toString();
                    String impUid = (String) response.get("imp_uid");
                    String paymentCompanyCode = response.get("card_name").toString();
                    PaymentCompany paymentCompany = PaymentCompany.ofValue(paymentCompanyCode);
                    orderDailyFood.updateOrderDailyFoodAfterPayment(receiptUrl, impUid, orderItemDailyFoodReqDto.getOrderId(), paymentCompany);
                }
                // 결제 실패시 orderMembership의 상태값을 결제 실패 상태(4)로 변경
                else {
                    throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
                }

            } else {
                // 주문서 내용 업데이트 및 사용 포인트 차감
                updateOrderDailyFood(orderDailyFood, orderItemDailyFoods, defaultPrice, orderItemDailyFoodReqDto.getOrderItems().getUserPoint(), payPrice, totalDeliveryFee);
                pointUtil.updateUserPoint(user, orderDailyFood.getId(), orderItemDailyFoodReqDto.getOrderItems().getUserPoint());
            }

            cartDailyFoodRepository.deleteAll(cartDailyFoods);

            return orderDailyFood.getId();
        }
    }

    @Override
    public void cancelOrderDailyFoodNice(SecurityUser securityUser, BigInteger orderId) throws IOException, ParseException {
        User user = userUtil.getUser(securityUser);

        Order order = orderRepository.findOneByIdAndUser(orderId, user).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND)
        );

        orderService.cancelOrderDailyFoodNice((OrderDailyFood) order, user);
    }

    @Override
    @Transactional
    public void cancelOrderItemDailyFoodNice(SecurityUser securityUser, BigInteger orderItemId) throws IOException, ParseException {
        User user = userUtil.getUser(securityUser);

        OrderItemDailyFood orderItemDailyFood = orderItemDailyFoodRepository.findById(orderItemId).orElseThrow(
                () -> new ApiException(ExceptionEnum.ORDER_ITEM_NOT_FOUND)
        );

        orderService.cancelOrderItemDailyFoodNice(orderItemDailyFood, user);
    }


    @Override
    @Transactional
    public List<OrderDetailDto> findOrderByServiceDate(SecurityUser securityUser, BigInteger spotId, LocalDate startDate, LocalDate endDate) {
        // 유저정보 가져오기
        User user = userUtil.getUser(securityUser);
        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
        MultiValueMap<OrderDetailDto.OrderDetail, OrderItemDailyFood> multiValueMap = new LinkedMultiValueMap<>();

        Spot spot = (spotId == null) ? null : spotRepository.findById(spotId).orElse(null);
        Group group = (spot == null) ? null : spot.getGroup();

        List<OrderItemDailyFood> orderItemList = qOrderDailyFoodRepository.findByUserAndGroupAndServiceDateBetween(user, group, startDate, endDate);
        // group by orderItemDailyFoodGroup
        for (OrderItemDailyFood orderItemDailyFood : orderItemList) {
            OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFood.getOrderItemDailyFoodGroup();
            OrderDetailDto.OrderDetail orderDetail = new OrderDetailDto.OrderDetail(orderItemDailyFoodGroup.getServiceDate(), orderItemDailyFoodGroup.getDiningType());
            multiValueMap.add(orderDetail, orderItemDailyFood);
        }

        //make dto
        for (OrderDetailDto.OrderDetail orderDetail : multiValueMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoods = multiValueMap.get(orderDetail);
            if (orderItemDailyFoods == null || orderItemDailyFoods.isEmpty()) continue;

            List<OrderItemDto> orderItemDtoList = new ArrayList<>();
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                OrderItemDto orderItemDto = orderItemDailyFoodListMapper.toDto(orderItemDailyFood);
                orderItemDtoList.add(orderItemDto);
            }

            orderItemDtoList = orderItemDtoList.stream()
                    .sorted(Comparator.comparing((OrderItemDto orderItemDto) -> orderItemDto.getOrderStatus().equals(14))
                            .thenComparing(OrderItemDto::getOrderStatus, Comparator.reverseOrder()))
                    .toList();

            OrderDetailDto orderDetailDto = orderItemDailyFoodListMapper.toOrderDetailDto(orderDetail, orderItemDtoList);
            orderDetailDtos.add(orderDetailDto);
        }

        orderDetailDtos = orderDetailDtos.stream()
                .sorted(Comparator.comparing((OrderDetailDto v) -> DateUtils.stringToDate(v.getServiceDate()))
                        .thenComparing(v -> DiningType.ofString(v.getDiningType()))
                )
                .collect(Collectors.toList());

        findOrderByServiceDateNotification(user);

        return orderDetailDtos;
    }

    @Override
    @Transactional
    public List<OrderHistoryDto> findUserOrderDailyFoodHistory(SecurityUser securityUser, LocalDate startDate, LocalDate endDate, Integer orderType) {
        User user = userUtil.getUser(securityUser);

        List<OrderHistoryDto> orderHistoryDtos = new ArrayList<>();

        List<Order> orders = qOrderRepository.findAllOrderByUserFilterByOrderTypeAndPeriod(user, orderType, startDate, endDate);
        for (Order order : orders) {
            // TODO: 마켓, 케이터링 구현시 instanceOf로 조건 나누기
            if (Hibernate.unproxy(order) instanceof OrderDailyFood) {
                List<OrderHistoryDto.OrderItem> orderItems = new ArrayList<>();
                List<OrderItem> orderItemList = order.getOrderItems();
                for (OrderItem orderItem : orderItemList) {
                    orderItems.add(orderDailyFoodHistoryMapper.orderItemDailyFoodToDto((OrderItemDailyFood) orderItem));
                }
                orderHistoryDtos.add(orderDailyFoodHistoryMapper.orderToDto(order, orderItems));
            }
        }
        return orderHistoryDtos;
    }

    @Override
    @Transactional
    public OrderDailyFoodDetailDto getOrderDailyFoodDetail(SecurityUser securityUser, BigInteger orderId) {
        User user = userUtil.getUser(securityUser);

        OrderDailyFood orderDailyFood = orderDailyFoodRepository.findById(orderId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND)
        );

        if (!orderDailyFood.getUser().equals(user)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        List<OrderDailyFoodDetailDto.OrderItem> orderItemList = new ArrayList<>();
        List<OrderItem> refundItems = new ArrayList<>();
        List<OrderItem> orderItems = orderDailyFood.getOrderItems();
        OrderDailyFoodDetailDto.RefundDto refundDto = null;
        // TODO: 마켓/케이터링 구현시 instanceOf
        for (OrderItem orderItem : orderItems) {
            orderItemList.add(orderDailyFoodDetailMapper.orderItemDailyFoodToDto((OrderItemDailyFood) orderItem));
            if (orderItem.getOrderStatus().equals(OrderStatus.CANCELED)) {
                refundItems.add(orderItem);
            }
        }
        // 환불 내역이 존재한다면
        if (!refundItems.isEmpty()) {
            List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderItems(refundItems);
            refundDto = orderUtil.getRefundReceipt(refundItems, paymentCancelHistories);
        }

        return orderDailyFoodDetailMapper.orderToDto(orderDailyFood, orderItemList, refundDto);

    }

    @Override
    @Transactional
    public void cancelOrderItemDailyFood(SecurityUser securityUser, BigInteger orderItemId) throws IOException, ParseException {
        User user = userUtil.getUser(securityUser);

        OrderItemDailyFood orderItemDailyFood = orderItemDailyFoodRepository.findById(orderItemId).orElseThrow(
                () -> new ApiException(ExceptionEnum.ORDER_ITEM_NOT_FOUND)
        );

        orderService.cancelOrderItemDailyFood(orderItemDailyFood, user);
    }

    private void findOrderByServiceDateNotification(User user) {
        //오늘이 무슨 요일인지 체크
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        String dayOfWeek = now.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

        // Get registered spots
        List<UserSpot> userSpots = user.getUserSpots();
        if (userSpots.isEmpty()) return;

        // Get the default set spot among the registered spots
        Spot defaultSpot = userSpots.stream()
                .filter(UserSpot::getIsDefault)
                .map(UserSpot::getSpot)
                .findFirst()
                .orElse(null);
        if (defaultSpot == null) return;

        // Check the meal type, serviceable days, and closing time of the default spot
        List<MealInfo> mealInfos = defaultSpot.getMealInfos();
        List<OrderByServiceDateNotyDto> notyDtos = mealInfos.stream().map(OrderByServiceDateNotyDto::createOrderByServiceDateNotyDto).collect(Collectors.toList());

        // 오늘 주문 여부 확인. 오늘 주문한 기록이 없으면
        List<OrderItemDailyFood> todayOrderFoods = qOrderDailyFoodRepository.findByServiceDate(now);
        if (todayOrderFoods.size() == 0) {
            lastOrderTimeNotification(user, dayOfWeek, notyDtos, now);
        }

        // 제공하는 dining type 중 하나라도 하지 않았다면
        HashSet<DiningType> mealInfoDiningType = new HashSet<>();
        HashSet<DiningType> todayOrderFoodDiningType = new HashSet<>();
        todayOrderFoods.forEach(order -> todayOrderFoodDiningType.add(order.getDailyFood().getDiningType()));
        notyDtos.forEach(info -> mealInfoDiningType.add(info.getType()));

        if (mealInfoDiningType.size() > todayOrderFoodDiningType.size()) {
            lastOrderTimeNotification(user, dayOfWeek, notyDtos, now);
        }

        // 다음주 주문이 없을 때
        // 하루에 한 번만 알림 보내기 - 알림을 읽었으면 그날 하루는 더 이상 보내지 않음.
        List<NotificationHash> todayAlreadySendNotys = notificationHashRepository.findAllByUserIdAndTypeAndIsRead(user.getId(), 5, true);
        if(todayAlreadySendNotys.stream().anyMatch(v -> v.getCreateDate().equals(now))) return;

        // 알림을 보낸적 없으면
        Map<String, LocalDate> weekOfDay = DateUtils.getWeekOfDay(now);
        LocalDate startDate = weekOfDay.get("startDate").plusDays(7);
        LocalDate endDate = weekOfDay.get("endDate").plusDays(7);
        List<OrderItemDailyFood> nextWeekOrderFoods = qOrderDailyFoodRepository.findByUserAndGroupAndServiceDateBetween(user, null, startDate, endDate);

        Set<String> nextWeekOrderFoodServiceDays = nextWeekOrderFoods.stream()
                .map(order -> order.getDailyFood().getServiceDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA))
                .collect(Collectors.toSet());
        Set<String> mealInfoServiceDays = notyDtos.stream()
                .flatMap(notyDto -> notyDto.getServiceDays().stream())
                .collect(Collectors.toSet());

        //다음주 주문 중 모든 서비스 날이 포함 되었는지 확인
        if (nextWeekOrderFoods.isEmpty() || nextWeekOrderFoodServiceDays.size() < mealInfoServiceDays.size()) {
            // 모든 서비스 날이 포함 되지 않았다면
            sseService.send(user.getId(), 5, "다음주 식사 구매하셨나요?");
        }

    }

    private void lastOrderTimeNotification(User user, String dayOfWeek, List<OrderByServiceDateNotyDto> notyDtos, LocalDate now) {
        //오늘 주문한게 없고,
        for (OrderByServiceDateNotyDto notyDto : notyDtos) {
            LocalTime curranTime = LocalTime.now(ZoneId.of("Asia/Seoul"));
            String isServiceDay = notyDto.getServiceDays().stream().filter(serviceDay -> serviceDay.equalsIgnoreCase(dayOfWeek)).findFirst().orElse(null);

            if (notyDto.getMembershipBenefitTime() != null) {
                // 오늘이 멤버십 할인 시간
                Integer day = notyDto.getMembershipBenefitTime().getDay();
                LocalTime time = notyDto.getMembershipBenefitTime().getTime();

                String membershipDayOfWeek = now.minusDays(day).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA);
                LocalTime membershipTime = time.minusHours(2);

                if (dayOfWeek.equalsIgnoreCase(membershipDayOfWeek) && curranTime.isAfter(membershipTime) && curranTime.isBefore(time)) {
                    String content = "내일 " + notyDto.getType() + "식사 주문은 오늘 " + DateUtils.timeToStringWithAMPM(time) + "까지 해야 멤버십 할인을 받을 수 있어요!";
                    sseService.send(user.getId(), 4, content);
                    return;
                }
            }
            if (notyDto.getLastOrderTime() != null) {
                Integer day = notyDto.getLastOrderTime().getDay();
                LocalTime time = notyDto.getLastOrderTime().getTime();

                String lastOrderDayOfWeek = now.minusDays(day).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA);
                LocalTime lastOrderNoticeTime = time.minusHours(2);
                // 서비스 가능일 이고, 오늘이 서비스 가능일이 아니면 나가기
                if (dayOfWeek.equalsIgnoreCase(lastOrderDayOfWeek) && curranTime.isAfter(lastOrderNoticeTime) && curranTime.isBefore(time)) {
                    String content = "내일 " + notyDto.getType() + "식사 주문은 오늘 " + DateUtils.timeToStringWithAMPM(time) + "에 마감이예요!";
                    sseService.send(user.getId(), 4, content);
                    return;
                }
            }
        }
    }

    @Override
    @Transactional
    public void changingOrderItemOrderStatus(SecurityUser securityUser, BigInteger orderItemId) {
        User user = userUtil.getUser(securityUser);

        OrderItemDailyFood orderItemDailyFood = qOrderDailyFoodRepository.findByUserAndId(user, orderItemId);
        if (orderItemDailyFood == null) throw new ApiException(ExceptionEnum.ORDER_NOT_FOUND);

        orderItemDailyFood.updateOrderStatus(OrderStatus.RECEIPT_COMPLETE);

        LocalDate serviceDate = orderItemDailyFood.getDailyFood().getServiceDate();
        // 유저가 파운더스이고 멤버십을 유지하고 있으며 오늘 수령확인을 처음 진행하는 거라면
        Founders foundersUser = qFoundersRepository.findFoundersByUser(user);
        if (user.getIsMembership() && foundersUser != null && serviceDate.equals(LocalDate.now())) {
            BigDecimal point = pointUtil.findFoundersPoint(user);
            if (point.compareTo(BigDecimal.ZERO) != 0) {
                pointUtil.createPointHistoryByOthers(user, null, PointStatus.FOUNDERS_REWARD, point);
                qUserRepository.updateUserPoint(user.getId(), point, PointStatus.FOUNDERS_REWARD);
            }
        }

        // sse
        sseService.send(user.getId(), 3, null);
    }

    @Override
    public Object orderCardQuota(SecurityUser securityUser, OrderCardQuotaDto orderCardQuotaDto) throws IOException, ParseException {

        User user = userUtil.getUser(securityUser);

        String token = niceUtil.getToken();
        JSONObject jsonObject = niceUtil.niceBillingCardQuota(orderCardQuotaDto.getBillingKey(), orderCardQuotaDto.getAmount(), orderCardQuotaDto.getOrderId(), token, orderCardQuotaDto.getOrderName(), orderCardQuotaDto.getCardQuota());
        System.out.println(jsonObject);

        return null;
    }
    /*
    @Override
    @Transactional
    public BigInteger orderDailyFoods(SecurityUser securityUser, OrderItemDailyFoodReqDto orderItemDailyFoodReqDto) {
        if (orderItemDailyFoodReqDto.getAmount() != 0) {
            throw new ApiException(ExceptionEnum.NEED_TO_UPDATE);
        }

        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        synchronized (userLocks.computeIfAbsent(user, u -> new Object())) {
            // 그룹/스팟 정보 가져오기
            Spot spot = spotRepository.findById(orderItemDailyFoodReqDto.getOrderItems().getSpotId()).orElseThrow(
                    () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
            );
            Group group = spot.getGroup();
            // 유저가 그 그룹의 스팟에 포함되는지 확인.
            List<UserGroup> userGroups = user.getGroups();
            userGroups.stream().filter(v -> v.getGroup().equals(group) && v.getClientStatus().equals(ClientStatus.BELONG))
                    .findAny()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));

            // 사용 요청 포인트가 유저가 현재 가지고 있는 포인트보다 적은지 검증
            if (orderItemDailyFoodReqDto.getOrderItems().getUserPoint().compareTo(user.getPoint()) > 0) {
                throw new ApiException(ExceptionEnum.HAS_LESS_POINT_THAN_REQUEST);
            }

            Set<ServiceDiningDto> serviceDiningDtos = new HashSet<>();
            List<OrderItemDailyFood> orderItemDailyFoods = new ArrayList<>();
            List<BigInteger> cartDailyFoodIds = new ArrayList<>();
            BigDecimal defaultPrice = BigDecimal.ZERO;
            BigDecimal totalDeliveryFee = BigDecimal.ZERO;
            BigDecimal totalSupportPrice = BigDecimal.ZERO;
            BigDecimal totalDailyFoodPrice = BigDecimal.ZERO;

            // 식사타입(DiningType)과 날짜별(serviceDate) 식사들 가져오기
            List<CartDailyFoodDto> cartDailyFoodDtoList = orderItemDailyFoodReqDto.getOrderItems().getCartDailyFoodDtoList();
            // 프론트에서 제공한 정보와 실제 정보가 일치하는지 확인
            for (CartDailyFoodDto cartDailyFoodDto : cartDailyFoodDtoList) {
                // 배송비 일치 점증 및 배송비 계산
                if (cartDailyFoodDto.getDeliveryFee().compareTo(deliveryFeePolicy.getGroupDeliveryFee(user, group)) != 0) {
                    throw new ApiException(ExceptionEnum.NOT_MATCHED_DELIVERY_FEE);
                }
                totalDeliveryFee = totalDeliveryFee.add(cartDailyFoodDto.getDeliveryFee());

                serviceDiningDtos.add(new ServiceDiningDto(DateUtils.stringToDate(cartDailyFoodDto.getServiceDate()), DiningType.ofString(cartDailyFoodDto.getDiningType())));

                for (CartDailyFoodDto.DailyFood dailyFood : cartDailyFoodDto.getCartDailyFoods()) {
                    cartDailyFoodIds.add(dailyFood.getId());
                }
            }

            // ServiceDate의 가장 빠른 날짜와 늦은 날짜 구하기
            PeriodDto periodDto = UserSupportPriceUtil.getEarliestAndLatestServiceDate(serviceDiningDtos);

            List<CartDailyFood> cartDailyFoods = qCartDailyFoodRepository.findAllByFoodIds(cartDailyFoodIds);

            // 1. 주문서 저장하기
            OrderDailyFood orderDailyFood = orderDailyFoodRepository.save(orderMapper.toEntity(user, spot, orderItemDailyFoodReqDto.getOrderId()));

            for (CartDailyFoodDto cartDailyFoodDto : cartDailyFoodDtoList) {
                // 2. 유저 사용 지원금 가져오기
                List<DailyFoodSupportPrice> userSupportPriceHistories = qDailyFoodSupportPriceRepository.findAllUserSupportPriceHistoryBetweenServiceDate(user, periodDto.getStartDate(), periodDto.getEndDate());

                BigDecimal supportPrice = BigDecimal.ZERO;
                BigDecimal orderItemGroupTotalPrice = BigDecimal.ZERO;
                if (spot instanceof CorporationSpot) {
                    supportPrice = UserSupportPriceUtil.getUsableSupportPrice(spot, userSupportPriceHistories, DateUtils.stringToDate(cartDailyFoodDto.getServiceDate()), DiningType.ofString(cartDailyFoodDto.getDiningType()));
                    if (!spot.getGroup().getName().contains("메드트로닉") && cartDailyFoodDto.getSupportPrice().compareTo(supportPrice) != 0) {
                        throw new ApiException(ExceptionEnum.NOT_MATCHED_SUPPORT_PRICE);
                    }
                }
                // 3. 배송일과 지원금 저장하기
                OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFoodGroupRepository.save(orderDailyFoodItemMapper.dtoToOrderItemDailyFoodGroup(cartDailyFoodDto));
                OrderItemDailyFood orderItemDailyFood = null;
                // 4. 주문 음식 가격이 일치하는지 검증 및 주문 저장
                for (CartDailyFoodDto.DailyFood cartDailyFood : cartDailyFoodDto.getCartDailyFoods()) {
                    CartDailyFood selectedCartDailyFood = cartDailyFoods.stream().filter(v -> v.getId().equals(cartDailyFood.getId()))
                            .findAny()
                            .orElseThrow(() -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND));
                    // 주문 수량이 일치하는지 확인
                    if (!selectedCartDailyFood.getCount().equals(cartDailyFood.getCount())) {
                        throw new ApiException(ExceptionEnum.NOT_MATCHED_ITEM_COUNT);
                    }
                    // 멤버십에 가입하지 않은 경우 멤버십 할인이 적용되지 않은 가격으로 보임
                    DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, group, spot, selectedCartDailyFood.getDailyFood());
                    // 금액 일치 확인
                    if (cartDailyFood.getDiscountedPrice().intValue() != discountDto.getDiscountedPrice().intValue()) {
                        throw new ApiException(ExceptionEnum.NOT_MATCHED_PRICE);
                    }
                    if (cartDailyFood.getPrice().intValue() != selectedCartDailyFood.getDailyFood().getFood().getPrice().intValue() ||
                            !cartDailyFood.getMakersDiscountRate().equals(discountDto.getMakersDiscountRate()) ||
                            !cartDailyFood.getMembershipDiscountRate().equals(discountDto.getMembershipDiscountRate()) ||
                            !cartDailyFood.getPeriodDiscountRate().equals(discountDto.getPeriodDiscountRate()) ||
                            cartDailyFood.getMakersDiscountPrice().intValue() != discountDto.getMakersDiscountPrice().intValue() ||
                            cartDailyFood.getMembershipDiscountPrice().intValue() != discountDto.getMembershipDiscountPrice().intValue() ||
                            cartDailyFood.getPeriodDiscountPrice().intValue() != discountDto.getPeriodDiscountPrice().intValue()
                    ) {
                        throw new ApiException(ExceptionEnum.NOT_MATCHED_PRICE);
                    }
                    System.out.println(selectedCartDailyFood.getDailyFood().getFood().getId() + "foodId");
                    orderItemDailyFood = orderDailyFoodItemMapper.dtoToOrderItemDailyFood(cartDailyFood, selectedCartDailyFood, orderDailyFood, orderItemDailyFoodGroup);
                    orderItemDailyFoods.add(orderItemDailyFoodRepository.save(orderItemDailyFood));

                    defaultPrice = defaultPrice.add(selectedCartDailyFood.getDailyFood().getFood().getPrice().multiply(BigDecimal.valueOf(cartDailyFood.getCount())));
                    BigDecimal dailyFoodPrice = cartDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(cartDailyFood.getCount()));
                    orderItemGroupTotalPrice = orderItemGroupTotalPrice.add(dailyFoodPrice);
                    totalDailyFoodPrice = totalDailyFoodPrice.add(dailyFoodPrice);

                    // 주문 가능 수량이 일치하는지 확인
                    FoodCountDto foodCountDto = orderDailyFoodUtil.getRemainFoodCount(selectedCartDailyFood.getDailyFood());
                    if (foodCountDto.getRemainCount() - cartDailyFood.getCount() < 0) {
                        throw new ApiException(ExceptionEnum.OVER_ITEM_CAPACITY);
                    }
                    if (foodCountDto.getRemainCount() - cartDailyFood.getCount() == 0) {
                        if (foodCountDto.getIsFollowingMakersCapacity()) {
                            List<DailyFood> dailyFoods = qDailyFoodRepository.findAllByMakersAndServiceDateAndDiningType(selectedCartDailyFood.getDailyFood().getFood().getMakers(),
                                    selectedCartDailyFood.getDailyFood().getServiceDate(), selectedCartDailyFood.getDailyFood().getDiningType());
                            for (DailyFood dailyFood : dailyFoods) {
                                dailyFood.updateFoodStatus(DailyFoodStatus.SOLD_OUT);
                            }
                        } else {
                            selectedCartDailyFood.getDailyFood().updateFoodStatus(DailyFoodStatus.SOLD_OUT);
                        }
                    }
                }

                // 5. 지원금 사용 저장
                if (spot instanceof CorporationSpot) {
                    BigDecimal usableSupportPrice = UserSupportPriceUtil.getUsableSupportPrice(orderItemGroupTotalPrice, supportPrice);
                    if (usableSupportPrice.compareTo(BigDecimal.ZERO) != 0) {
                        DailyFoodSupportPrice dailyFoodSupportPrice;
                        if (spot.getGroup().getName().contains("메드트로닉")) {
                            dailyFoodSupportPrice = dailyFoodSupportPriceMapper.toMedTronicSupportPrice(orderItemDailyFood, orderItemGroupTotalPrice);
                        } else {
                            dailyFoodSupportPrice = dailyFoodSupportPriceMapper.toEntity(orderItemDailyFood, usableSupportPrice);
                        }
                        dailyFoodSupportPriceRepository.save(dailyFoodSupportPrice);
                        totalSupportPrice = totalSupportPrice.add(dailyFoodSupportPrice.getUsingSupportPrice());
                    }
                }
            }

            // 주문하려는 상품의 가격 총 합이 포인트보다 큰지 검증
            if (totalDailyFoodPrice.subtract(totalSupportPrice).compareTo(orderItemDailyFoodReqDto.getOrderItems().getUserPoint()) < 0) {
                throw new ApiException(ExceptionEnum.HAS_LESS_POINT_THAN_REQUEST);
            }

            // 결제 금액 (배송비 + 할인된 상품 가격의 합) - (회사 지원금 - 포인트 사용)
            BigDecimal payPrice = totalDailyFoodPrice.add(totalDeliveryFee).subtract(totalSupportPrice).subtract(orderItemDailyFoodReqDto.getOrderItems().getUserPoint());

            // 결제 금액이 0일 경우
            if (payPrice.compareTo(BigDecimal.ZERO) == 0) {
                pointUtil.createPointHistoryByOthers(user, orderDailyFood.getId(), PointStatus.USED, orderItemDailyFoodReqDto.getOrderItems().getUserPoint());
            }

            if (payPrice.compareTo(orderItemDailyFoodReqDto.getOrderItems().getTotalPrice()) != 0 || totalSupportPrice.compareTo(orderItemDailyFoodReqDto.getOrderItems().getSupportPrice()) != 0) {
                throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
            }

            // 멤버십을 지원하는 기업의 식사를 주문하면서, 멤버십에 가입되지 않은 회원이라면 멤버십 가입.
            if (OrderUtil.isMembership(user, (Group) Hibernate.unproxy(group)) && !user.getIsMembership()) {
                LocalDate now = LocalDate.now();
                LocalDate membershipStartDate = LocalDate.of(now.getYear(), now.getMonth(), group.getContractStartDate().getDayOfMonth());
                PeriodDto membershipPeriod = new PeriodDto(membershipStartDate, membershipStartDate.plusMonths(1));

                // 멤버십 등록
                Membership membership = orderMembershipMapper.toMembership(MembershipSubscriptionType.MONTH, user, membershipPeriod);
                membershipRepository.save(membership);

                // 결제 내역 등록
                OrderUserInfoDto orderUserInfoDto = orderUserInfoMapper.toDto(user);
                OrderMembership order = orderMembershipMapper.toOrderMembership(orderUserInfoDto, null, MembershipSubscriptionType.MONTH, BigDecimal.ZERO, BigDecimal.ZERO, PaymentType.SUPPORT_PRICE, membership);
                orderMembershipRepository.save(order);

                // 멤버십 결제 내역 등록(진행중 상태)
                OrderItemMembership orderItemMembership = orderMembershipMapper.toOrderItemMembership(order, membership);
                orderItemMembershipRepository.save(orderItemMembership);

                // 지원금 사용 등록
                MembershipSupportPrice membershipSupportPrice = orderMembershipMapper.toMembershipSupportPrice(user, group, orderItemMembership);
                membershipSupportPriceRepository.save(membershipSupportPrice);

                // 파운더스 확인
                if (!foundersUtil.isFounders(user) && !foundersUtil.isOverFoundersLimit()) {
                    Founders founders = foundersMapper.toEntity(user, membership, foundersUtil.getMaxFoundersNumber() + 1);
                    foundersUtil.saveFounders(founders);
                }
                user.updateIsMembership(true);
            }

            // 결제 금액이 0이 아닐 경우, 토스페이를 통해 결제
            if (orderItemDailyFoodReqDto.getPaymentKey() != null && orderItemDailyFoodReqDto.getAmount() != 0) {
                try {
                    JSONObject jsonObject = tossUtil.paymentConfirm(orderItemDailyFoodReqDto.getPaymentKey(), orderItemDailyFoodReqDto.getAmount(), orderItemDailyFoodReqDto.getOrderId());
                    System.out.println(jsonObject + "결제 Response값");

                    String status = (String) jsonObject.get("status");
                    System.out.println(status);

                    // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
                    if (status.equals("DONE")) {
                        // 주문서 내용 업데이트 및 사용 포인트 차감
                        orderDailyFood.updateDefaultPrice(defaultPrice);
                        orderDailyFood.updatePoint(orderItemDailyFoodReqDto.getOrderItems().getUserPoint());
                        orderDailyFood.updateTotalPrice(BigDecimal.valueOf(orderItemDailyFoodReqDto.getAmount()));
                        orderDailyFood.updateTotalDeliveryFee(totalDeliveryFee);
                        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                            orderItemDailyFood.updateOrderStatus(OrderStatus.COMPLETED);
                        }
                        pointUtil.createPointHistoryByOthers(user, orderDailyFood.getId(), PointStatus.USED, orderItemDailyFoodReqDto.getOrderItems().getUserPoint());
                        user.updatePoint(user.getPoint().subtract(orderItemDailyFoodReqDto.getOrderItems().getUserPoint()));

                        //Order 테이블에 paymentKey와 receiptUrl 업데이트
                        JSONObject receipt = (JSONObject) jsonObject.get("receipt");
                        String receiptUrl = receipt.get("url").toString();

                        String paymentKey = (String) jsonObject.get("paymentKey");
                        JSONObject card = (JSONObject) jsonObject.get("card");
                        String paymentCompanyCode;
                        if (card == null) {
                            JSONObject easyPay = (JSONObject) jsonObject.get("easyPay");
                            if (easyPay == null) {
                                throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
                            }
                            paymentCompanyCode = (String) easyPay.get("provider");
                        } else {
                            paymentCompanyCode = (String) card.get("issuerCode");
                        }
                        System.out.println("jsonObject = " + jsonObject);
                        PaymentCompany paymentCompany = PaymentCompany.ofCode(paymentCompanyCode);
                        orderDailyFood.updateOrderDailyFoodAfterPayment(receiptUrl, paymentKey, orderItemDailyFoodReqDto.getOrderId(), paymentCompany);
                    }
                    // 결제 실패시 orderMembership의 상태값을 결제 실패 상태(4)로 변경
                    else {
                        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                            orderItemDailyFood.updateOrderStatus(OrderStatus.FAILED);
                        }
                        throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
                    }
                } catch (ApiException e) {
                    for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                        orderItemDailyFood.updateOrderStatus(OrderStatus.FAILED);
                        if (payPrice.compareTo(BigDecimal.ZERO) > 0) {
                            throw new ApiException(ExceptionEnum.CARD_NOT_FOUND);
                        }
                    }
                    throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
                } catch (IOException e) {
                    throw new ApiException(ExceptionEnum.BAD_REQUEST);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // 주문서 내용 업데이트 및 사용 포인트 차감
                orderDailyFood.updateDefaultPrice(defaultPrice);
                orderDailyFood.updatePoint(orderItemDailyFoodReqDto.getOrderItems().getUserPoint());
                orderDailyFood.updateTotalPrice(payPrice);
                orderDailyFood.updateTotalDeliveryFee(totalDeliveryFee);
                for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                    orderItemDailyFood.updateOrderStatus(OrderStatus.COMPLETED);
                }
                user.updatePoint(user.getPoint().subtract(orderItemDailyFoodReqDto.getOrderItems().getUserPoint()));
            }

            cartDailyFoodRepository.deleteAll(cartDailyFoods);


            return orderDailyFood.getId();
        }
    }
    */

    @Override
    @Transactional
    public void cancelOrderDailyFood(SecurityUser securityUser, BigInteger orderId) throws IOException, ParseException {
        User user = userUtil.getUser(securityUser);

        Order order = orderRepository.findOneByIdAndUser(orderId, user).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND)
        );

        orderService.cancelOrderDailyFood((OrderDailyFood) order, user);
    }

    private BigDecimal getSupportPrice(User user, Spot spot, CartDailyFoodDto cartDailyFoodDto, PeriodDto periodDto) {
        // 2. 유저 사용 지원금 가져오기
        List<DailyFoodSupportPrice> userSupportPriceHistories = qDailyFoodSupportPriceRepository.findAllUserSupportPriceHistoryBetweenServiceDate(user, periodDto.getStartDate(), periodDto.getEndDate());

        BigDecimal supportPrice = BigDecimal.ZERO;
        if (spot instanceof CorporationSpot) {
            supportPrice = UserSupportPriceUtil.getUsableSupportPrice(spot, userSupportPriceHistories, DateUtils.stringToDate(cartDailyFoodDto.getServiceDate()), DiningType.ofString(cartDailyFoodDto.getDiningType()));
            if (!spot.getGroup().getName().contains("메드트로닉") && cartDailyFoodDto.getSupportPrice().compareTo(supportPrice) != 0) {
                throw new ApiException(ExceptionEnum.NOT_MATCHED_SUPPORT_PRICE);
            }
        }
        return supportPrice;
    }

    private void checkPriceValidation(User user, Group group, Spot spot, CartDailyFoodDto.DailyFood cartDailyFood, CartDailyFood selectedCartDailyFood) {

        if (selectedCartDailyFood != null && !selectedCartDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.SALES)) {
            throw new CustomException(HttpStatus.NOT_FOUND, "CE4000002", "주문 불가한 상품입니다.");
        }
        // 일치하는 상품을 찾을 수 없을 경우
        if (selectedCartDailyFood == null) {
            throw new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND);
        }
        // 주문 수량이 일치하는지 확인
        if (!selectedCartDailyFood.getCount().equals(cartDailyFood.getCount())) {
            throw new ApiException(ExceptionEnum.NOT_MATCHED_ITEM_COUNT);
        }
        // 멤버십에 가입하지 않은 경우 멤버십 할인이 적용되지 않은 가격으로 보임
        DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, group, spot, selectedCartDailyFood.getDailyFood());
        // 금액 일치 확인 TODO: 금액 일치 오류 확인(NOT_MATCHED_PRICE) -> 반올림 문제
        if (cartDailyFood.getDiscountedPrice().intValue() != discountDto.getDiscountedPrice().intValue()) {
            System.out.println(discountDto.getDiscountedPrice() + " discountPrice");
            throw new ApiException(ExceptionEnum.NOT_MATCHED_PRICE);
        }
        if (cartDailyFood.getPrice().intValue() != selectedCartDailyFood.getDailyFood().getFood().getPrice().intValue() ||
                !cartDailyFood.getMakersDiscountRate().equals(discountDto.getMakersDiscountRate()) ||
                !cartDailyFood.getMembershipDiscountRate().equals(discountDto.getMembershipDiscountRate()) ||
                !cartDailyFood.getPeriodDiscountRate().equals(discountDto.getPeriodDiscountRate()) ||
                cartDailyFood.getMakersDiscountPrice().intValue() != discountDto.getMakersDiscountPrice().intValue() ||
                cartDailyFood.getMembershipDiscountPrice().intValue() != discountDto.getMembershipDiscountPrice().intValue() ||
                cartDailyFood.getPeriodDiscountPrice().intValue() != discountDto.getPeriodDiscountPrice().intValue()
        ) {
            throw new ApiException(ExceptionEnum.NOT_MATCHED_PRICE);
        }
        System.out.println(selectedCartDailyFood.getDailyFood().getFood().getId() + "foodId");
    }

    private void checkFoodCount(FoodCountDto foodCountDto, CartDailyFoodDto.DailyFood cartDailyFood, CartDailyFood selectedCartDailyFood) {
        if (foodCountDto.getRemainCount() - cartDailyFood.getCount() < 0) {
            throw new ApiException(ExceptionEnum.OVER_ITEM_CAPACITY);
        }
        if (foodCountDto.getRemainCount() - cartDailyFood.getCount() == 0) {
            if (foodCountDto.getIsFollowingMakersCapacity()) {
                List<DailyFood> dailyFoods = qDailyFoodRepository.findAllByMakersAndServiceDateAndDiningType(selectedCartDailyFood.getDailyFood().getFood().getMakers(),
                        selectedCartDailyFood.getDailyFood().getServiceDate(), selectedCartDailyFood.getDailyFood().getDiningType());
                for (DailyFood dailyFood : dailyFoods) {
                    dailyFood.updateFoodStatus(DailyFoodStatus.SOLD_OUT);
                }
            } else {
                selectedCartDailyFood.getDailyFood().updateFoodStatus(DailyFoodStatus.SOLD_OUT);
            }
        }
    }

    private void updateOrderDailyFood(OrderDailyFood orderDailyFood, List<OrderItemDailyFood> orderItemDailyFoods, BigDecimal defaultPrice, BigDecimal point, BigDecimal payPrice, BigDecimal totalDeliveryFee) {
        orderDailyFood.updateDefaultPrice(defaultPrice);
        orderDailyFood.updatePoint(point);
        orderDailyFood.updateTotalPrice(payPrice);
        orderDailyFood.updateTotalDeliveryFee(totalDeliveryFee);

        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            orderItemDailyFood.updateOrderStatus(OrderStatus.COMPLETED);
            orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.COMPLETED);
        }
    }
}

