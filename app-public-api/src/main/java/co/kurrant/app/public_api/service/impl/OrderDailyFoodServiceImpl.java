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
import co.dalicious.domain.order.mapper.*;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.repository.QCreditCardInfoRepository;
import co.dalicious.domain.payment.util.TossUtil;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
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
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    private final UserUtil userUtil;
    private final TossUtil tossUtil;
    private final SpotRepository spotRepository;
    private final QCartDailyFoodRepository qCartDailyFoodRepository;
    private final UserSupportPriceUtil userSupportPriceUtil;
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

    @Override
    @Transactional
    public void orderDailyFoods(SecurityUser securityUser, OrderItemDailyFoodReqDto orderItemDailyFoodReqDto, BigInteger spotId) {
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
        PeriodDto periodDto = userSupportPriceUtil.getEarliestAndLatestServiceDate(diningTypeServiceDateDtos);

        List<CartDailyFood> cartDailyFoods = qCartDailyFoodRepository.findAllByFoodIds(cartDailyFoodIds);

        // 1. 주문서 저장하기
        OrderDailyFood orderDailyFood = orderDailyFoodRepository.save(orderDailyFoodMapper.toEntity(user, spot));

        for (CartDailyFoodDto cartDailyFoodDto : cartDailyFoodDtoList) {
            // 2. 유저 사용 지원금 가져오기
            List<UserSupportPriceHistory> userSupportPriceHistories = qUserSupportPriceHistoryRepository.findAllUserSupportPriceHistoryBetweenServiceDate(user, periodDto.getStartDate(), periodDto.getEndDate());

            BigDecimal supportPrice = BigDecimal.ZERO;
            BigDecimal orderItemGroupTotalPrice = BigDecimal.ZERO;
            if (spot instanceof CorporationSpot) {
                supportPrice = userSupportPriceUtil.getGroupSupportPriceByDiningType(spot, DiningType.ofString(cartDailyFoodDto.getDiningType()));
                // 기존에 사용한 지원금이 있다면 차감
                BigDecimal usedSupportPrice = userSupportPriceUtil.getUsedSupportPrice(userSupportPriceHistories, DateUtils.stringToDate(cartDailyFoodDto.getServiceDate()), DiningType.ofString(cartDailyFoodDto.getDiningType()));
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
        String orderName = makeOrderName(cartDailyFoods);


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
            }
            throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        } catch (IOException e) {
            throw new ApiException(ExceptionEnum.BAD_REQUEST);
        } catch (InterruptedException | ParseException e) {
            throw new RuntimeException(e);
        }

        qCartDailyFoodRepository.deleteByCartDailyFoodList(cartDailyFoods);
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
        List<OrderDailyFoodDetailDto.OrderItem> orderItemList = new ArrayList<>();
        List<OrderItem> orderItems = orderDailyFood.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItemList.add(orderDailyFoodDetailMapper.orderItemDailyFoodToDto((OrderItemDailyFood) orderItem));
        }
        return orderDailyFoodDetailMapper.orderToDto(orderDailyFood, orderItemList);
    }

    //orderName생성
    private String makeOrderName(List<CartDailyFood> cartDailyFoods){
        //장바구니에 담긴 아이템이 1개라면 상품명을 그대로 리턴
        if (cartDailyFoods.size() == 1){
            return cartDailyFoods.get(0).getDailyFood().getFood().getName();
        }
        //장바구니에 담긴 아이템이 2개 이상이라면 "상품명 외 size-1 건"
        String firstFoodName = cartDailyFoods.get(0).getDailyFood().getFood().getName();
        Integer foodSize = cartDailyFoods.size() - 1;
        return firstFoodName + "외" + foodSize + "건";
    }
}

