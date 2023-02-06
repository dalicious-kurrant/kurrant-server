package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.entity.CorporationSpot;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.util.FoodUtil;
import co.dalicious.domain.order.dto.*;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.mapper.OrderDailyFoodItemMapper;
import co.dalicious.domain.order.mapper.OrderDailyFoodMapper;
import co.dalicious.domain.order.mapper.OrderItemDailyFoodListMapper;
import co.dalicious.domain.order.mapper.UserSupportPriceHistoryReqMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.util.enums.DiningType;
import co.dalicious.system.util.enums.FoodStatus;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.OrderDailyFoodService;
import co.kurrant.app.public_api.service.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    private final UserUtil userUtil;
    private final TossUtil tossUtil;
    private final SpotRepository spotRepository;
    private final QCartDailyFoodRepository qCartDailyFoodRepository;
    private final DeliveryFeePolicy deliveryFeePolicy;
    private final OrderDailyFoodMapper orderDailyFoodMapper;
    private final OrderDailyFoodItemMapper orderDailyFoodItemMapper;
    private final OrderDailyFoodRepository orderDailyFoodRepository;
    private final QOrderRepository qOrderRepository;
    private final QCreditCardInfoRepository qCreditCardInfoRepository;
    private final OrderItemDailyFoodRepository orderItemDailyFoodRepository;
    private final UserSupportPriceHistoryReqMapper userSupportPriceHistoryReqMapper;
    private final UserSupportPriceHistoryRepository userSupportPriceHistoryRepository;
    private final QUserSupportPriceHistoryRepository qUserSupportPriceHistoryRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final OrderItemDailyFoodListMapper orderItemDailyFoodListMapper;
    private final OrderDailyFoodHistoryMapper orderDailyFoodHistoryMapper;
    private final OrderDailyFoodDetailMapper orderDailyFoodDetailMapper;
    private final OrderItemDailyFoodGroupRepository orderItemDailyFoodGroupRepository;
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;
    private final OrderUtil orderUtil;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public BigInteger orderDailyFoods(SecurityUser securityUser, OrderItemDailyFoodReqDto orderItemDailyFoodReqDto, BigInteger spotId) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        // 그룹/스팟 정보 가져오기
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        Group group = spot.getGroup();
        // 유저가 그 그룹의 스팟에 포함되는지 확인.
        List<UserGroup> userGroups = user.getGroups();
        userGroups.stream().filter(v -> v.getGroup().equals(group) && v.getClientStatus().equals(ClientStatus.BELONG))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));

        // 사용 요청 포인트가 유저가 현재 가지고 있는 포인트보다 적은지 검증
        if (orderItemDailyFoodReqDto.getUserPoint().compareTo(user.getPoint()) > 0) {
            throw new ApiException(ExceptionEnum.HAS_LESS_POINT_THAN_REQUEST);
        }

        // 카드 정보 검증하기
        CreditCardInfo creditCardInfo = null;
        if(orderItemDailyFoodReqDto.getCardId() != null) {
            List<CreditCardInfo> creditCardInfos = qCreditCardInfoRepository.findAllByUserId(user.getId());

            creditCardInfo = creditCardInfos.stream().filter(v -> v.getId().equals(orderItemDailyFoodReqDto.getCardId()))
                    .findAny()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.CARD_NOT_FOUND));
        }

        Set<DiningTypeServiceDateDto> diningTypeServiceDateDtos = new HashSet<>();
        List<OrderItemDailyFood> orderItemDailyFoods = new ArrayList<>();
        List<BigInteger> cartDailyFoodIds = new ArrayList<>();
        BigDecimal defaultPrice = BigDecimal.ZERO;
        BigDecimal totalDeliveryFee = BigDecimal.ZERO;
        BigDecimal totalSupportPrice = BigDecimal.ZERO;
        BigDecimal totalDailyFoodPrice = BigDecimal.ZERO;

        // 식사타입(DiningType)과 날짜별(serviceDate) 식사들 가져오기
        List<CartDailyFoodDto> cartDailyFoodDtoList = orderItemDailyFoodReqDto.getCartDailyFoodDtoList();

        // 프론트에서 제공한 정보와 실제 정보가 일치하는지 확인
        for (CartDailyFoodDto cartDailyFoodDto : cartDailyFoodDtoList) {
            // 배송비 일치 점증 및 배송비 계산
            if (cartDailyFoodDto.getDeliveryFee().compareTo(deliveryFeePolicy.getGroupDeliveryFee(user, group)) != 0) {
                throw new ApiException(ExceptionEnum.NOT_MATCHED_DELIVERY_FEE);
            }
            totalDeliveryFee = totalDeliveryFee.add(cartDailyFoodDto.getDeliveryFee());

            diningTypeServiceDateDtos.add(new DiningTypeServiceDateDto(DateUtils.stringToDate(cartDailyFoodDto.getServiceDate()), DiningType.ofString(cartDailyFoodDto.getDiningType())));

            for (CartDailyFoodDto.DailyFood dailyFood : cartDailyFoodDto.getCartDailyFoods()) {
                cartDailyFoodIds.add(dailyFood.getId());
            }
        }

        // ServiceDate의 가장 빠른 날짜와 늦은 날짜 구하기
        PeriodDto periodDto = UserSupportPriceUtil.getEarliestAndLatestServiceDate(diningTypeServiceDateDtos);

        List<CartDailyFood> cartDailyFoods = qCartDailyFoodRepository.findAllByFoodIds(cartDailyFoodIds);

        // 1. 주문서 저장하기
        OrderDailyFood orderDailyFood = orderDailyFoodRepository.save(orderDailyFoodMapper.toEntity(user, spot, creditCardInfo));

        for (CartDailyFoodDto cartDailyFoodDto : cartDailyFoodDtoList) {
            // 2. 유저 사용 지원금 가져오기
            List<UserSupportPriceHistory> userSupportPriceHistories = qUserSupportPriceHistoryRepository.findAllUserSupportPriceHistoryBetweenServiceDate(user, periodDto.getStartDate(), periodDto.getEndDate());

            BigDecimal supportPrice = BigDecimal.ZERO;
            BigDecimal orderItemGroupTotalPrice = BigDecimal.ZERO;
            if (spot instanceof CorporationSpot) {
                supportPrice = UserSupportPriceUtil.getGroupSupportPriceByDiningType(spot, DiningType.ofString(cartDailyFoodDto.getDiningType()));
                // 기존에 사용한 지원금이 있다면 차감
                BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(userSupportPriceHistories, DateUtils.stringToDate(cartDailyFoodDto.getServiceDate()), DiningType.ofString(cartDailyFoodDto.getDiningType()));
                supportPrice = supportPrice.subtract(usedSupportPrice);
                if (cartDailyFoodDto.getSupportPrice().compareTo(supportPrice) != 0) {
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
                DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, group, selectedCartDailyFood.getDailyFood().getFood());
                // 금액 일치 확인
                if (cartDailyFood.getDiscountedPrice().compareTo(FoodUtil.getFoodTotalDiscountedPrice(selectedCartDailyFood.getDailyFood().getFood(), discountDto)) != 0) {
                    throw new ApiException(ExceptionEnum.NOT_MATCHED_PRICE);
                }
                if (cartDailyFood.getPrice().compareTo(selectedCartDailyFood.getDailyFood().getFood().getPrice()) != 0 ||
                        cartDailyFood.getMakersDiscountRate().compareTo(discountDto.getMakersDiscountRate()) != 0 ||
                        cartDailyFood.getMakersDiscountPrice().compareTo(discountDto.getMakersDiscountPrice()) != 0 ||
                        cartDailyFood.getMembershipDiscountRate().compareTo(discountDto.getMembershipDiscountRate()) != 0 ||
                        cartDailyFood.getMembershipDiscountPrice().compareTo(discountDto.getMembershipDiscountPrice()) != 0 ||
                        cartDailyFood.getPeriodDiscountRate().compareTo(discountDto.getPeriodDiscountRate()) != 0 ||
                        cartDailyFood.getPeriodDiscountPrice().compareTo(discountDto.getPeriodDiscountPrice()) != 0
                ) {
                    throw new ApiException(ExceptionEnum.NOT_MATCHED_PRICE);
                }
                orderItemDailyFood = orderDailyFoodItemMapper.dtoToOrderItemDailyFood(cartDailyFood, selectedCartDailyFood, orderDailyFood, orderItemDailyFoodGroup);
                orderItemDailyFoods.add(orderItemDailyFoodRepository.save(orderItemDailyFood));

                defaultPrice = defaultPrice.add(selectedCartDailyFood.getDailyFood().getFood().getPrice().multiply(BigDecimal.valueOf(cartDailyFood.getCount())));
                BigDecimal dailyFoodPrice = cartDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(cartDailyFood.getCount()));
                orderItemGroupTotalPrice = orderItemGroupTotalPrice.add(dailyFoodPrice);
                totalDailyFoodPrice = totalDailyFoodPrice.add(dailyFoodPrice);

                // 주문 개수 차감
                Integer capacity = selectedCartDailyFood.getDailyFood().subtractCapacity(cartDailyFood.getCount());
                if (capacity < 0) {
                    throw new ApiException(ExceptionEnum.OVER_ITEM_CAPACITY);
                }
                if (capacity == 0) {
                    selectedCartDailyFood.getDailyFood().updateFoodStatus(FoodStatus.SOLD_OUT);
                }
            }

            // 5. 지원금 사용 저장
            if (spot instanceof CorporationSpot) {
                BigDecimal usableSupportPrice = UserSupportPriceUtil.getUsableSupportPrice(orderItemGroupTotalPrice, supportPrice);
                if (usableSupportPrice.compareTo(BigDecimal.ZERO) != 0) {
                    UserSupportPriceHistory userSupportPriceHistory = userSupportPriceHistoryReqMapper.toEntity(orderItemDailyFood, usableSupportPrice);
                    userSupportPriceHistoryRepository.save(userSupportPriceHistory);
                    totalSupportPrice = totalSupportPrice.add(usableSupportPrice);
                }
            }
        }

        // 결제 금액 (배송비 + 할인된 상품 가격의 합) - (회사 지원금 - 포인트 사용)
        BigDecimal payPrice = totalDailyFoodPrice.add(totalDeliveryFee).subtract(totalSupportPrice).subtract(orderItemDailyFoodReqDto.getUserPoint());
        if (payPrice.compareTo(orderItemDailyFoodReqDto.getTotalPrice()) != 0 || totalSupportPrice.compareTo(orderItemDailyFoodReqDto.getSupportPrice()) != 0) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }

        // cardId로 customerKey를 가져오기
        CreditCardInfo creditCard = qCreditCardInfoRepository.findCustomerKeyByCardId(orderItemDailyFoodReqDto.getCardId());
        //orderName 생성
        String orderName = OrderUtil.makeOrderName(cartDailyFoods);


        try {
            JSONObject jsonObject = tossUtil.payToCard(creditCard.getCustomerKey(), payPrice.intValue(), orderDailyFood.getCode(), orderName, creditCard.getBillingKey());
            System.out.println(jsonObject + "결제 Response값");

            String status = (String) jsonObject.get("status");
            System.out.println(status);

            // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
            if (status.equals("DONE")) {
                // 주문서 내용 업데이트 및 사용 포인트 차감
                orderDailyFood.updateDefaultPrice(defaultPrice);
                orderDailyFood.updatePoint(orderItemDailyFoodReqDto.getUserPoint());
                orderDailyFood.updateTotalPrice(payPrice);
                orderDailyFood.updateTotalDeliveryFee(totalDeliveryFee);
                for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                    orderItemDailyFood.updateOrderStatus(OrderStatus.COMPLETED);
                }
                user.updatePoint(user.getPoint().subtract(orderItemDailyFoodReqDto.getUserPoint()));

                //Order 테이블에 paymentKey와 receiptUrl 업데이트
                JSONObject receipt = (JSONObject) jsonObject.get("receipt");
                String receiptUrl = receipt.get("url").toString();

                String paymentKey = (String) jsonObject.get("paymentKey");
                qOrderRepository.afterPaymentUpdate(receiptUrl, paymentKey, orderDailyFood.getId());
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
                if(payPrice.compareTo(BigDecimal.ZERO) > 0 && creditCardInfo == null) {
                    throw new ApiException(ExceptionEnum.CARD_NOT_FOUND);
                }
            }
            throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        } catch (IOException e) {
            throw new ApiException(ExceptionEnum.BAD_REQUEST);
        } catch (InterruptedException | ParseException e) {
            throw new RuntimeException(e);
        }

        qCartDailyFoodRepository.deleteByCartDailyFoodList(cartDailyFoods);

        return orderDailyFood.getId();
    }

    @Override
    @Transactional
    public List<OrderDetailDto> findOrderByServiceDate(SecurityUser securityUser, LocalDate startDate, LocalDate endDate) {
        // 유저정보 가져오기
        User user = userUtil.getUser(securityUser);
        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
        Set<OrderItemDailyFoodGroup> orderItemDailyFoodGroups = new HashSet<>();
        MultiValueMap<OrderItemDailyFoodGroup, OrderItemDto> multiValueMap = new LinkedMultiValueMap<>();

        List<OrderItemDailyFood> orderItemList = qOrderDailyFoodRepository.findByUserAndServiceDateBetween(user ,startDate, endDate);
        for (OrderItemDailyFood orderItemDailyFood : orderItemList) {
            orderItemDailyFoodGroups.add(orderItemDailyFood.getOrderItemDailyFoodGroup());
            OrderItemDto orderItemDto = orderItemDailyFoodListMapper.toDto(orderItemDailyFood);
            // 상속 비교를 하기 위해 프록시 해제
            OrderDailyFood orderDailyFood = (OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder());
            orderItemDto.setGroupName(orderDailyFood.getGroupName());
            orderItemDto.setSpotName(orderDailyFood.getSpotName());
            multiValueMap.add(orderItemDailyFood.getOrderItemDailyFoodGroup(), orderItemDto);
        }

        for (OrderItemDailyFoodGroup OrderItemDailyFoodGroup : orderItemDailyFoodGroups) {
            OrderDetailDto orderDetailDto = OrderDetailDto.builder()
                    .serviceDate(DateUtils.format(OrderItemDailyFoodGroup.getServiceDate(), "yyyy-MM-dd"))
                    .diningType(OrderItemDailyFoodGroup.getDiningType().getDiningType())
                    .orderItemDtoList(multiValueMap.get(OrderItemDailyFoodGroup))
                    .build();
            orderDetailDtos.add(orderDetailDto);
        }

        orderDetailDtos = orderDetailDtos.stream()
                .sorted(Comparator.comparing((OrderDetailDto v) -> DateUtils.stringToDate(v.getServiceDate()))
                        .thenComparing(v -> DiningType.ofString(v.getDiningType()))
                )
                .collect(Collectors.toList());

        findOrderByServiceDateNoty(securityUser, startDate, endDate);

        return orderDetailDtos;
    }

    @Override
    @Transactional
    public List<OrderHistoryDto> findUserOrderDailyFoodHistory(SecurityUser securityUser, LocalDate startDate, LocalDate endDate, Integer orderType) {
        User user = userUtil.getUser(securityUser);

        List<OrderHistoryDto> orderHistoryDtos = new ArrayList<>();

        List<Order> orders = qOrderRepository.findAllOrderByUserFilterByOrderTypeAndPeriod(user, orderType, startDate, endDate);
        for (Order order : orders) {
            List<OrderHistoryDto.OrderItem> orderItems = new ArrayList<>();
            List<OrderItem> orderItemList = order.getOrderItems();
            // TODO: 마켓, 케이터링 구현시 instanceOf로 조건 나누기
            for (OrderItem orderItem : orderItemList) {
                orderItems.add(orderDailyFoodHistoryMapper.orderItemDailyFoodToDto((OrderItemDailyFood) orderItem));
            }
            orderHistoryDtos.add(orderDailyFoodHistoryMapper.orderToDto(order, orderItems));
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

        if(!orderDailyFood.getUser().equals(user)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        List<OrderDailyFoodDetailDto.OrderItem> orderItemList = new ArrayList<>();
        List<OrderItem> refundItems = new ArrayList<>();
        List<OrderItem> orderItems = orderDailyFood.getOrderItems();
        OrderDailyFoodDetailDto.RefundDto refundDto = null;
        // TODO: 마켓/케이터링 구현시 instanceOf
        for (OrderItem orderItem : orderItems) {
            orderItemList.add(orderDailyFoodDetailMapper.orderItemDailyFoodToDto((OrderItemDailyFood) orderItem));
            if(orderItem.getOrderStatus().equals(OrderStatus.CANCELED)) {
                refundItems.add((OrderItemDailyFood) orderItem);
            }
        }
        // 환불 내역이 존재한다면
        if(!refundItems.isEmpty()) {
            List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderItems(refundItems);
            refundDto = orderUtil.getRefundReceipt(refundItems, paymentCancelHistories);
        }

        return orderDailyFoodDetailMapper.orderToDto(orderDailyFood, orderItemList, refundDto);
    }

    @Override
    @Transactional
    public void cancelOrderDailyFood(SecurityUser securityUser, BigInteger orderId) throws IOException, ParseException {
        User user = userUtil.getUser(securityUser);

        Order order = orderRepository.findOneByIdAndUser(orderId, user).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND)
        );

        BigDecimal price = BigDecimal.ZERO;
        BigDecimal deliveryFee = BigDecimal.ZERO;
        BigDecimal point = BigDecimal.ZERO;

        // 이전에 환불을 진행한 경우
        List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

        for(OrderItem orderItem : order.getOrderItems()) {
            OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) orderItem;
            // 상태값이 이미 7L(취소)인지 확인
            if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
                throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
            }

            BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories());

            RefundPriceDto refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());
            price = price.add(refundPriceDto.getPrice());
            deliveryFee = deliveryFee.add(refundPriceDto.getDeliveryFee());
            point = point.add(refundPriceDto.getPoint());

            if(!refundPriceDto.isSameSupportPrice(usedSupportPrice)) {
                List<UserSupportPriceHistory> userSupportPriceHistories = orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories();
                for (UserSupportPriceHistory userSupportPriceHistory : userSupportPriceHistories) {
                    userSupportPriceHistory.updateMonetaryStatus(MonetaryStatus.REFUND);
                }
                UserSupportPriceHistory userSupportPriceHistory = userSupportPriceHistoryReqMapper.toEntity(orderItemDailyFood, refundPriceDto.getRenewSupportPrice());
                if(userSupportPriceHistory.getUsingSupportPrice().compareTo(BigDecimal.ZERO) != 0) {
                    userSupportPriceHistoryRepository.save(userSupportPriceHistory);
                }
            }

            // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
            if(refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0) {
                PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemDailyFood(orderItemDailyFood, refundPriceDto, paymentCancelHistories);
                paymentCancelHistories.add(paymentCancelHistoryRepository.save(paymentCancelHistory));
            }
            orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);
            orderItemDailyFood.getDailyFood().addCapacity(orderItemDailyFood.getCount());

            if(refundPriceDto.getIsLastItemOfGroup()) {
                orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
            }
        }
        user.updatePoint(user.getPoint().add(point));
        tossUtil.cardCancelOne(order.getPaymentKey(), "전체 주문 취소", price.intValue());
    }

    @Override
    @Transactional
    public void cancelOrderItemDailyFood(SecurityUser securityUser, BigInteger orderItemId) throws IOException, ParseException {
        User user = userUtil.getUser(securityUser);

        OrderItemDailyFood orderItemDailyFood = orderItemDailyFoodRepository.findById(orderItemId).orElseThrow(
                () -> new ApiException(ExceptionEnum.ORDER_ITEM_NOT_FOUND)
        );

        Order order = orderItemDailyFood.getOrder();

        if(!order.getUser().equals(user)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        // 상태값이 이미 7L(취소)인지 확인
        if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)){
            throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
        }

        // 이전에 환불을 진행한 경우
        List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories());

        RefundPriceDto refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());

        CreditCardInfo creditCardInfo = order.getCreditCardInfo();

        if(!refundPriceDto.isSameSupportPrice(usedSupportPrice)) {
            List<UserSupportPriceHistory> userSupportPriceHistories = orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories();
            for (UserSupportPriceHistory userSupportPriceHistory : userSupportPriceHistories) {
                userSupportPriceHistory.updateMonetaryStatus(MonetaryStatus.REFUND);
            }
            UserSupportPriceHistory userSupportPriceHistory = userSupportPriceHistoryReqMapper.toEntity(orderItemDailyFood, refundPriceDto.getRenewSupportPrice());
            if(userSupportPriceHistory.getUsingSupportPrice().compareTo(BigDecimal.ZERO) != 0) {
                userSupportPriceHistoryRepository.save(userSupportPriceHistory);
            }
        }

        // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
        if(refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0) {
            PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemDailyFood(order.getPaymentKey(), creditCardInfo, "주문 마감 전 주문 취소", orderItemDailyFood, refundPriceDto);
            paymentCancelHistoryRepository.save(paymentCancelHistory);
        }

        user.updatePoint(user.getPoint().add(refundPriceDto.getPoint()));
        orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);
        orderItemDailyFood.getDailyFood().addCapacity(orderItemDailyFood.getCount());

        if(refundPriceDto.getIsLastItemOfGroup()) {
            orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
        }
    }

    private void findOrderByServiceDateNoty(SecurityUser securityUser, LocalDate startDate, LocalDate endDate) {
        User user = userUtil.getUser(securityUser);
        List<UserGroup> userGroups = user.getGroups();

        List<UserSpot> userSpots = user.getUserSpots();

        //등록한 스팟이 있는지 확인
        if(userSpots.size() == 0) { return; }

        // 등록된 스팟 중 default 설정 된 스팟을 찾기
        Spot defaultSpot = null;
        for(UserSpot userSpot : userSpots) {
            if(userSpot.getIsDefault()) {
                defaultSpot = userSpot.getSpot();
                break;
            }
        }

        //default 스팟의 서비스 일, 시간을 확인
        List<MealInfo> mealInfos = defaultSpot.getMealInfos();
        List<String> serviceDays = new ArrayList<>();

        //현재 시간
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        for(MealInfo mealInfo : mealInfos) {
            // 조식인 경우
            if(mealInfo.getDiningType() == DiningType.MORNING) {

            }
            // 중식인 경우
            // 석식인 경우
        }
    }
}
