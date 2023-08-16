package co.dalicious.domain.order.util;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.dto.OrderCount;
import co.dalicious.domain.order.dto.OrderDailyFoodDetailDto;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.mapper.PaymentCancleHistoryMapper;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.util.NiceUtil;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.converter.RefundPriceDto;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.system.util.GenerateRandomNumber;
import co.dalicious.system.util.NumberUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderUtil {
    private final TossUtil tossUtil;
    private final NiceUtil niceUtil;
    private final PaymentCancleHistoryMapper paymentCancleHistoryMapper;

    // 주문 코드 생성
    public static String generateOrderCode(OrderType orderType, BigInteger userId) {
        String code = switch (orderType) {
            case DAILYFOOD -> "S";
            case PRODUCT -> "P";
            case MEMBERSHIP -> "M";
            case CATERING -> "C";
        };
        LocalDate now = LocalDate.now();
        code += now.toString().replace("-", "");
        code += GenerateRandomNumber.idToString(userId.intValue());
        code += GenerateRandomNumber.create4DigitKey();
        return code;
    }

    public static void orderMembershipStatusUpdate(User user, OrderItemMembership orderItemMembership) {
        if (orderItemMembership.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            orderItemMembership.getMembership().changeMembershipStatus(MembershipStatus.PROCESSING);
            orderItemMembership.getMembership().changeAutoPaymentStatus(true);
            user.changeMembershipStatus(true);
        } else if (orderItemMembership.getOrderStatus().equals(OrderStatus.AUTO_REFUND)) {
            orderItemMembership.getMembership().changeMembershipStatus(MembershipStatus.AUTO_REFUND);
            orderItemMembership.getMembership().changeAutoPaymentStatus(false);
            user.changeMembershipStatus(false);
        } else if (orderItemMembership.getOrderStatus().equals(OrderStatus.MANUAL_REFUNDED)) {
            orderItemMembership.getMembership().changeMembershipStatus(MembershipStatus.BACK_OFFICE_REFUND);
            orderItemMembership.getMembership().changeAutoPaymentStatus(false);
            user.changeMembershipStatus(false);
        }
    }

    public static BigDecimal discountedPriceByRate(BigDecimal price, Integer discountRate) {
        return price.multiply(BigDecimal.valueOf((100.0 - discountRate) / 100));
    }

    public static BigDecimal discountPriceByRate(BigDecimal price, Integer discountRate) {
        return price.multiply(BigDecimal.valueOf(discountRate / 100.0));
    }

    public static Boolean isMembership(User user, Group group) {
        if (group instanceof Corporation corporation) {
            return user.getIsMembership() || (corporation.getIsMembershipSupport() &&
                    (corporation.getMembershipEndDate() == null || !corporation.getMembershipEndDate().isBefore(LocalDate.now())));
        }
        return user.getIsMembership();
    }

    public static Boolean isCorporationMembership(User user, Group group) {
        if (group instanceof Corporation corporation) {
            return (corporation.getIsMembershipSupport() &&
                    (corporation.getMembershipEndDate() == null || !corporation.getMembershipEndDate().isBefore(LocalDate.now())));
        }
        return user.getIsMembership();
    }

    // TODO: 식단에 가격 업데이트 적용이 되는 시점부터 주석 해제
    public static DiscountDto checkMembershipAndGetDiscountDto(User user, Group group, Spot spot, DailyFood dailyFood) {
        group = (Group) Hibernate.unproxy(group);

        if (isMembership(user, group)) {
            // 멤버십 혜택 마감 시간 (서비스 날짜 전일 + 마감시간)
            LocalDateTime membershipBenefitTime = LocalDateTime.of(dailyFood.getServiceDate().minusDays(spot.getMembershipBenefitTime(dailyFood.getDiningType()).getDay()), spot.getMembershipBenefitTime(dailyFood.getDiningType()).getTime());
            if (spot.getDeliveryTime(dailyFood.getDiningType()) == null || LocalDateTime.now().isBefore(membershipBenefitTime)) {

                return DiscountDto.getDiscount(dailyFood);
            }
        }
        return DiscountDto.getDiscountWithoutMembership(dailyFood);
    }

    public static BigDecimal getPaidPriceGroupByOrderItemDailyFoodGroup(OrderItemDailyFoodGroup orderItemDailyFoodGroup) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        // 1. 배송비 추가
        totalPrice = totalPrice.add(orderItemDailyFoodGroup.getDeliveryFee());

        // 2. 할인된 상품 가격 추가
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodGroup.getOrderDailyFoods()) {
            if (orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || (OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus()) && orderItemDailyFood.getOrder().getPaymentType().equals(PaymentType.SUPPORT_PRICE))) {
                totalPrice = totalPrice.add(orderItemDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(orderItemDailyFood.getCount())));
            }
        }
        // 3. 지원금 사용 가격 제외
        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFoodGroup.getUserSupportPriceHistories());
        totalPrice = totalPrice.subtract(usedSupportPrice);

        // 예외. 포인트 사용으로 인해 식사 일정별 환불 가능 금액이 주문 전체 금액이 더 작을 경우
        Order order = orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder();
        if (order.getTotalPrice().compareTo(totalPrice) < 0) {
            return order.getTotalPrice();
        }

        return totalPrice;
    }

    public static BigDecimal getPaidPriceGroupByOrderItemDailyFoodGroupAdmin(OrderItemDailyFoodGroup orderItemDailyFoodGroup) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        // 1. 배송비 추가
        totalPrice = totalPrice.add(orderItemDailyFoodGroup.getDeliveryFee());

        // 2. 할인된 상품 가격 추가
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodGroup.getOrderDailyFoods()) {
            if (OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus())) {
                totalPrice = totalPrice.add(orderItemDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(orderItemDailyFood.getCount())));
            }
        }
        // 3. 지원금 사용 가격 제외
        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFoodGroup.getUserSupportPriceHistories());
        totalPrice = totalPrice.subtract(usedSupportPrice);

        // 예외. 포인트 사용으로 인해 식사 일정별 환불 가능 금액이 주문 전체 금액이 더 작을 경우
        Order order = orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder();
        if (order.getTotalPrice().compareTo(totalPrice) < 0) {
            return order.getTotalPrice();
        }

        return totalPrice;
    }

    public static BigDecimal getItemPriceGroupByOrderItemDailyFoodGroup(OrderItemDailyFoodGroup orderItemDailyFoodGroup) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodGroup.getOrderDailyFoods()) {
            // 주문이 결제 완료(5)인 경우와, 백오피스에서 추가 주문을 취소하기 위한 조건
            if (orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED)  || (OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus()) && orderItemDailyFood.getOrder().getPaymentType().equals(PaymentType.SUPPORT_PRICE))) {
                totalPrice = totalPrice.add(orderItemDailyFood.getOrderItemTotalPrice());
            }
        }
        return totalPrice;
    }

    public static BigDecimal getItemPriceGroupByOrderItemDailyFoodGroupAdmin(OrderItemDailyFoodGroup orderItemDailyFoodGroup) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodGroup.getOrderDailyFoods()) {
            // 주문이 결제 완료(5)인 경우와, 백오피스에서 추가 주문을 취소하기 위한 조건
            if (OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus()) || (OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus()) && orderItemDailyFood.getOrder().getPaymentType().equals(PaymentType.SUPPORT_PRICE))) {
                totalPrice = totalPrice.add(orderItemDailyFood.getOrderItemTotalPrice());
            }
        }
        return totalPrice;
    }

    public static Boolean isLastOrderItemOfGroup(OrderItemDailyFood orderItemDailyFood) {
        OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFood.getOrderItemDailyFoodGroup();
        List<OrderItemDailyFood> orderItemDailyFoods = new ArrayList<>();
        for (OrderItemDailyFood itemDailyFood : orderItemDailyFoodGroup.getOrderDailyFoods()) {
            if (itemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                orderItemDailyFoods.add(itemDailyFood);
            }
        }
        if (orderItemDailyFoods.size() == 1 && orderItemDailyFoods.get(0).equals(orderItemDailyFood)) {
            return true;
        }
        return false;
    }

    public static Boolean isLastOrderItemOfGroupAdmin(OrderItemDailyFood orderItemDailyFood) {
        OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFood.getOrderItemDailyFoodGroup();
        List<OrderItemDailyFood> orderItemDailyFoods = new ArrayList<>();
        for (OrderItemDailyFood itemDailyFood : orderItemDailyFoodGroup.getOrderDailyFoods()) {
            if (OrderStatus.completePayment().contains(itemDailyFood.getOrderStatus())) {
                orderItemDailyFoods.add(itemDailyFood);
            }
        }
        if (orderItemDailyFoods.size() == 1 && orderItemDailyFoods.get(0).equals(orderItemDailyFood)) {
            return true;
        }
        return false;
    }

    // OrderItemDailyFoodGroup당 환불 금액
    public static RefundPriceDto getRefundPrice(OrderItemDailyFood orderItemDailyFood, List<PaymentCancelHistory> paymentCancelHistories, BigDecimal usingPoint) {
        OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFood.getOrderItemDailyFoodGroup();
        // 환불 가능 금액 (일정 모든 아이템 금액 - 지원금)
        BigDecimal refundablePrice = getPaidPriceGroupByOrderItemDailyFoodGroupAdmin(orderItemDailyFoodGroup);
        if (refundablePrice.compareTo(orderItemDailyFood.getOrder().getTotalPrice()) > 0) {
            refundablePrice = orderItemDailyFood.getOrder().getTotalPrice();
        }
        // 식사 일정에 따른 아이템 금액
        BigDecimal itemsPrice = getItemPriceGroupByOrderItemDailyFoodGroup(orderItemDailyFoodGroup);
        // 사용한 지원금
        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFoodGroup.getUserSupportPriceHistories());
        // 환불 요청 금액
        BigDecimal requestRefundPrice = orderItemDailyFood.getOrderItemTotalPrice();
        // 배송비
        BigDecimal deliveryFee = BigDecimal.ZERO;
        // 업데이트 되어야할 지원금
        BigDecimal renewSupportPrice = usedSupportPrice;
        Boolean isLastOrderItemOfGroup = isLastOrderItemOfGroup(orderItemDailyFood);
        // 배송비를 돌려줘야 하는 상황인지 확인.
        if (isLastOrderItemOfGroup(orderItemDailyFood) && orderItemDailyFoodGroup.getDeliveryFee().compareTo(BigDecimal.ZERO) > 0) {
            deliveryFee = orderItemDailyFoodGroup.getDeliveryFee();
            requestRefundPrice = requestRefundPrice.add(orderItemDailyFoodGroup.getDeliveryFee());
        }

        // 1. 지원금을 업데이트 시켜야하는 상황인지 확인(식사 일정 별 구매한 모든 정기 식사 가격 - 환불 요청 아이템 가격 < 사용한 회사 지원금)
        Boolean needToUpdateSupportPrice = itemsPrice.subtract(requestRefundPrice).compareTo(usedSupportPrice) < 0;

        // 2. 지원금 업데이트가 필요한 경우
        if (needToUpdateSupportPrice) {
            // 배송비 환불이 필요할 경우에는, 지원금만 계산하기 위해 요청환불 금액에서 배송비는 제외하기
            renewSupportPrice = itemsPrice.subtract(requestRefundPrice).add(deliveryFee);
            requestRefundPrice = requestRefundPrice.subtract(getDeductedSupportPrice(usedSupportPrice, renewSupportPrice));
            if (requestRefundPrice.compareTo(BigDecimal.ZERO) == 0) {
                return new RefundPriceDto(BigDecimal.ZERO, renewSupportPrice, BigDecimal.ZERO, deliveryFee, isLastOrderItemOfGroup);
            }
        }

        // 3. 환불 가능 금액 > 환불 요청 금액
        if (refundablePrice.compareTo(requestRefundPrice) >= 0) {
            if (!paymentCancelHistories.isEmpty()) {
                paymentCancelHistories = paymentCancelHistories.stream().sorted(Comparator.comparing(PaymentCancelHistory::getCancelDateTime).reversed()).collect(Collectors.toList());
                BigDecimal refundPoint = BigDecimal.ZERO;
                Optional<PaymentCancelHistory> tempPaymentCancelHistory = paymentCancelHistories.stream().filter(v -> v.getRefundPointPrice().compareTo(BigDecimal.ZERO) > 0).findAny();
                if (tempPaymentCancelHistory.isPresent()) {
                    refundPoint = refundPoint.add(tempPaymentCancelHistory.get().getRefundPointPrice());
                }
                PaymentCancelHistory paymentCancelHistory = paymentCancelHistories.get(0);
                BigDecimal tossRefundablePrice = paymentCancelHistory.getRefundablePrice();
                if (tossRefundablePrice.compareTo(requestRefundPrice) < 0) {
                    if (tossRefundablePrice.add(usingPoint.subtract(refundPoint)).compareTo(requestRefundPrice) < 0) {
                        throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
                    }
                    return new RefundPriceDto(tossRefundablePrice, renewSupportPrice, requestRefundPrice.subtract(tossRefundablePrice), deliveryFee, isLastOrderItemOfGroup);
                }
            }
            return new RefundPriceDto(requestRefundPrice, renewSupportPrice, BigDecimal.ZERO, deliveryFee, isLastOrderItemOfGroup);
        }

        // 4. 환불 가능 금액 < 환불 요청 금액
        if (refundablePrice.compareTo(requestRefundPrice) < 0) {
            if (requestRefundPrice.subtract(refundablePrice).compareTo(usingPoint) > 0) {
                throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
            }
            // 사용한 포인트가 (환불 요청 금액 - 환불 가능 금액) 보다 크거나 같으면
            if(!paymentCancelHistories.isEmpty()){
                PaymentCancelHistory paymentCancelHistory = paymentCancelHistories.stream().sorted(Comparator.comparing(PaymentCancelHistory::getCancelDateTime).reversed()).toList().get(0);
                refundablePrice = paymentCancelHistory.getRefundablePrice();
            }
            return new RefundPriceDto(refundablePrice, renewSupportPrice, requestRefundPrice.subtract(refundablePrice), deliveryFee, isLastOrderItemOfGroup);
        }
        throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
    }

    public static RefundPriceDto getRefundPriceAdmin(OrderItemDailyFood orderItemDailyFood, List<PaymentCancelHistory> paymentCancelHistories, BigDecimal usingPoint) {
        OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFood.getOrderItemDailyFoodGroup();
        // 환불 가능 금액 (일정 모든 아이템 금액 - 지원금)
        BigDecimal refundablePrice = getPaidPriceGroupByOrderItemDailyFoodGroupAdmin(orderItemDailyFoodGroup);
        if (refundablePrice.compareTo(orderItemDailyFood.getOrder().getTotalPrice()) > 0) {
            refundablePrice = orderItemDailyFood.getOrder().getTotalPrice();
        }
        // 식사 일정에 따른 아이템 금액
        BigDecimal itemsPrice = getItemPriceGroupByOrderItemDailyFoodGroupAdmin(orderItemDailyFoodGroup);
        // 사용한 지원금
        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFoodGroup.getUserSupportPriceHistories());
        // 환불 요청 금액
        BigDecimal requestRefundPrice = orderItemDailyFood.getOrderItemTotalPrice();
        // 배송비
        BigDecimal deliveryFee = BigDecimal.ZERO;
        // 업데이트 되어야할 지원금
        BigDecimal renewSupportPrice = usedSupportPrice;
        Boolean isLastOrderItemOfGroup = isLastOrderItemOfGroupAdmin(orderItemDailyFood);
        // 배송비를 돌려줘야 하는 상황인지 확인.
        if (isLastOrderItemOfGroup && orderItemDailyFoodGroup.getDeliveryFee().compareTo(BigDecimal.ZERO) > 0) {
            deliveryFee = orderItemDailyFoodGroup.getDeliveryFee();
            requestRefundPrice = requestRefundPrice.add(orderItemDailyFoodGroup.getDeliveryFee());
        }

        // 1. 지원금을 업데이트 시켜야하는 상황인지 확인(식사 일정 별 구매한 모든 정기 식사 가격 - 환불 요청 아이템 가격 < 사용한 회사 지원금)
        Boolean needToUpdateSupportPrice = itemsPrice.subtract(requestRefundPrice).compareTo(usedSupportPrice) < 0;

        // 2. 지원금 업데이트가 필요한 경우
        if (needToUpdateSupportPrice) {
            // 배송비 환불이 필요할 경우에는, 지원금만 계산하기 위해 요청환불 금액에서 배송비는 제외하기
            renewSupportPrice = itemsPrice.subtract(requestRefundPrice).add(deliveryFee);
            requestRefundPrice = requestRefundPrice.subtract(getDeductedSupportPrice(usedSupportPrice, renewSupportPrice));
            if (requestRefundPrice.compareTo(BigDecimal.ZERO) == 0) {
                return new RefundPriceDto(BigDecimal.ZERO, renewSupportPrice, BigDecimal.ZERO, deliveryFee, isLastOrderItemOfGroup);
            }
        }

        // 3. 환불 가능 금액 > 환불 요청 금액
        if (refundablePrice.compareTo(requestRefundPrice) >= 0) {
            if (!paymentCancelHistories.isEmpty()) {
                paymentCancelHistories = paymentCancelHistories.stream().sorted(Comparator.comparing(PaymentCancelHistory::getCancelDateTime).reversed()).collect(Collectors.toList());
                BigDecimal refundPoint = BigDecimal.ZERO;
                Optional<PaymentCancelHistory> tempPaymentCancelHistory = paymentCancelHistories.stream().filter(v -> v.getRefundPointPrice().compareTo(BigDecimal.ZERO) > 0).findAny();
                if (tempPaymentCancelHistory.isPresent()) {
                    refundPoint = refundPoint.add(tempPaymentCancelHistory.get().getRefundPointPrice());
                }
                PaymentCancelHistory paymentCancelHistory = paymentCancelHistories.get(0);
                BigDecimal tossRefundablePrice = paymentCancelHistory.getRefundablePrice();
                if (tossRefundablePrice.compareTo(requestRefundPrice) < 0) {
                    if (tossRefundablePrice.add(usingPoint.subtract(refundPoint)).compareTo(requestRefundPrice) < 0) {
                        throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
                    }
                    return new RefundPriceDto(tossRefundablePrice, renewSupportPrice, requestRefundPrice.subtract(tossRefundablePrice), deliveryFee, isLastOrderItemOfGroup);
                }
            }
            return new RefundPriceDto(requestRefundPrice, renewSupportPrice, BigDecimal.ZERO, deliveryFee, isLastOrderItemOfGroup);
        }

        // 4. 환불 가능 금액 < 환불 요청 금액
        if (refundablePrice.compareTo(requestRefundPrice) < 0) {
            if (requestRefundPrice.subtract(refundablePrice).compareTo(usingPoint) > 0) {
                throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
            }
            // 사용한 포인트가 (환불 요청 금액 - 환불 가능 금액) 보다 크거나 같으면
            if(!paymentCancelHistories.isEmpty()){
                PaymentCancelHistory paymentCancelHistory = paymentCancelHistories.stream().sorted(Comparator.comparing(PaymentCancelHistory::getCancelDateTime).reversed()).toList().get(0);
                refundablePrice = paymentCancelHistory.getRefundablePrice();
            }
            return new RefundPriceDto(refundablePrice, renewSupportPrice, requestRefundPrice.subtract(refundablePrice), deliveryFee, isLastOrderItemOfGroup);
        }
        throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
    }

    public static RefundPriceDto getMedtronicRefundPrice(OrderItemDailyFood orderItemDailyFood, List<PaymentCancelHistory> paymentCancelHistories, BigDecimal usingPoint) {
        OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFood.getOrderItemDailyFoodGroup();
        // 환불 가능 금액 (일정 모든 아이템 금액 - 지원금)
        BigDecimal refundablePrice = getPaidPriceGroupByOrderItemDailyFoodGroup(orderItemDailyFoodGroup);
        // 식사 일정에 따른 총 결제 금액
        BigDecimal itemsPrice = getItemPriceGroupByOrderItemDailyFoodGroup(orderItemDailyFoodGroup);
        // 사용한 지원금
        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFoodGroup.getUserSupportPriceHistories());
        // 환불 요청 금액
        BigDecimal requestRefundPrice = NumberUtils.floorToOneDigit(orderItemDailyFood.getOrderItemTotalPrice().multiply(BigDecimal.valueOf(0.5)));
        // 배송비
        BigDecimal deliveryFee = BigDecimal.ZERO;
        // 업데이트 되어야할 지원금
        BigDecimal renewSupportPrice = usedSupportPrice;
        Boolean isLastOrderItemOfGroup = isLastOrderItemOfGroup(orderItemDailyFood);


        // 배송비 환불이 필요할 경우에는, 지원금만 계산하기 위해 요청환불 금액에서 배송비는 제외하기
        renewSupportPrice = itemsPrice.multiply(BigDecimal.valueOf(0.5)).subtract(requestRefundPrice).add(deliveryFee);

        // 3. 환불 가능 금액 > 환불 요청 금액
        if (refundablePrice.compareTo(requestRefundPrice) >= 0) {
            if (!paymentCancelHistories.isEmpty()) {
                paymentCancelHistories = paymentCancelHistories.stream().sorted(Comparator.comparing(PaymentCancelHistory::getCancelDateTime).reversed()).collect(Collectors.toList());
                BigDecimal refundPoint = BigDecimal.ZERO;
                Optional<PaymentCancelHistory> tempPaymentCancelHistory = paymentCancelHistories.stream().filter(v -> v.getRefundPointPrice().compareTo(BigDecimal.ZERO) > 0).findAny();
                if (tempPaymentCancelHistory.isPresent()) {
                    refundPoint = refundPoint.add(tempPaymentCancelHistory.get().getRefundPointPrice());
                }
                PaymentCancelHistory paymentCancelHistory = paymentCancelHistories.get(0);
                BigDecimal tossRefundablePrice = paymentCancelHistory.getRefundablePrice();
                if (tossRefundablePrice.compareTo(requestRefundPrice) < 0) {
                    if (tossRefundablePrice.add(usingPoint.subtract(refundPoint)).compareTo(requestRefundPrice) < 0) {
                        throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
                    }
                    return new RefundPriceDto(tossRefundablePrice, renewSupportPrice, requestRefundPrice.subtract(tossRefundablePrice), deliveryFee, isLastOrderItemOfGroup);
                }
            }
            return new RefundPriceDto(requestRefundPrice, renewSupportPrice, BigDecimal.ZERO, deliveryFee, isLastOrderItemOfGroup);
        }
        // 4. 환불 가능 금액 < 환불 요청 금액
        if (refundablePrice.compareTo(requestRefundPrice) < 0) {
            if (requestRefundPrice.subtract(refundablePrice).compareTo(usingPoint) > 0) {
                throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
            }
            return new RefundPriceDto(refundablePrice, renewSupportPrice, requestRefundPrice.subtract(refundablePrice), deliveryFee, isLastOrderItemOfGroup);
        }
        throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
    }

    public static RefundPriceDto getMedtronicRefundPriceAdmin(OrderItemDailyFood orderItemDailyFood, List<PaymentCancelHistory> paymentCancelHistories, BigDecimal usingPoint) {
        OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFood.getOrderItemDailyFoodGroup();
        // 환불 가능 금액 (일정 모든 아이템 금액 - 지원금)
        BigDecimal refundablePrice = getPaidPriceGroupByOrderItemDailyFoodGroupAdmin(orderItemDailyFoodGroup);
        // 식사 일정에 따른 총 결제 금액
        BigDecimal itemsPrice = getItemPriceGroupByOrderItemDailyFoodGroupAdmin(orderItemDailyFoodGroup);
        // 사용한 지원금
        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFoodGroup.getUserSupportPriceHistories());
        // 환불 요청 금액
        BigDecimal requestRefundPrice = NumberUtils.floorToOneDigit(orderItemDailyFood.getOrderItemTotalPrice().multiply(BigDecimal.valueOf(0.5)));
        // 배송비
        BigDecimal deliveryFee = BigDecimal.ZERO;
        // 업데이트 되어야할 지원금
        BigDecimal renewSupportPrice = usedSupportPrice;
        Boolean isLastOrderItemOfGroup = isLastOrderItemOfGroup(orderItemDailyFood);


        // 배송비 환불이 필요할 경우에는, 지원금만 계산하기 위해 요청환불 금액에서 배송비는 제외하기
        renewSupportPrice = itemsPrice.multiply(BigDecimal.valueOf(0.5)).subtract(requestRefundPrice).add(deliveryFee);

        // 3. 환불 가능 금액 > 환불 요청 금액
        if (refundablePrice.compareTo(requestRefundPrice) >= 0) {
            if (!paymentCancelHistories.isEmpty()) {
                paymentCancelHistories = paymentCancelHistories.stream().sorted(Comparator.comparing(PaymentCancelHistory::getCancelDateTime).reversed()).collect(Collectors.toList());
                BigDecimal refundPoint = BigDecimal.ZERO;
                Optional<PaymentCancelHistory> tempPaymentCancelHistory = paymentCancelHistories.stream().filter(v -> v.getRefundPointPrice().compareTo(BigDecimal.ZERO) > 0).findAny();
                if (tempPaymentCancelHistory.isPresent()) {
                    refundPoint = refundPoint.add(tempPaymentCancelHistory.get().getRefundPointPrice());
                }
                PaymentCancelHistory paymentCancelHistory = paymentCancelHistories.get(0);
                BigDecimal tossRefundablePrice = paymentCancelHistory.getRefundablePrice();
                if (tossRefundablePrice.compareTo(requestRefundPrice) < 0) {
                    if (tossRefundablePrice.add(usingPoint.subtract(refundPoint)).compareTo(requestRefundPrice) < 0) {
                        throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
                    }
                    return new RefundPriceDto(tossRefundablePrice, renewSupportPrice, requestRefundPrice.subtract(tossRefundablePrice), deliveryFee, isLastOrderItemOfGroup);
                }
            }
            return new RefundPriceDto(requestRefundPrice, renewSupportPrice, BigDecimal.ZERO, deliveryFee, isLastOrderItemOfGroup);
        }
        // 4. 환불 가능 금액 < 환불 요청 금액
        if (refundablePrice.compareTo(requestRefundPrice) < 0) {
            if (requestRefundPrice.subtract(refundablePrice).compareTo(usingPoint) > 0) {
                throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
            }
            return new RefundPriceDto(refundablePrice, renewSupportPrice, requestRefundPrice.subtract(refundablePrice), deliveryFee, isLastOrderItemOfGroup);
        }
        throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
    }


    public OrderDailyFoodDetailDto.RefundDto getRefundReceipt(List<OrderItem> refundItems, List<PaymentCancelHistory> paymentCancelHistories) {
        // TODO: 백오피스 환불 차감 금액 추후 계산
        Set<OrderItemDailyFoodGroup> orderItemDailyFoodGroups = new HashSet<>();
        BigDecimal refundPayPrice = BigDecimal.ZERO;
        BigDecimal refundItemPrice = BigDecimal.ZERO;
        BigDecimal refundSupportPrice = BigDecimal.ZERO;
        BigDecimal refundDeliveryFee = BigDecimal.ZERO;
        BigDecimal refundDeduction = BigDecimal.ZERO;
        BigDecimal refundTotalPrice = BigDecimal.ZERO;
        BigDecimal refundCardPrice = BigDecimal.ZERO;
        BigDecimal refundTotalPoint = BigDecimal.ZERO;
        for (PaymentCancelHistory paymentCancelHistory : paymentCancelHistories) {
            refundCardPrice = refundCardPrice.add(paymentCancelHistory.getCancelPrice());
            refundDeliveryFee = refundDeliveryFee.add(paymentCancelHistory.getRefundDeliveryFee());
            refundTotalPoint = refundTotalPoint.add(paymentCancelHistory.getRefundPointPrice());
        }
        for (OrderItem refundItem : refundItems) {
            refundItemPrice = refundItemPrice.add(((OrderItemDailyFood) refundItem).getDiscountedPrice().multiply(BigDecimal.valueOf(((OrderItemDailyFood) refundItem).getCount())));
            orderItemDailyFoodGroups.add(((OrderItemDailyFood) refundItem).getOrderItemDailyFoodGroup());
        }
        for (OrderItemDailyFoodGroup orderItemDailyFoodGroup : orderItemDailyFoodGroups) {
            orderItemDailyFoodGroup = (OrderItemDailyFoodGroup) Hibernate.unproxy(orderItemDailyFoodGroup);
            refundSupportPrice = refundSupportPrice.add(getRefundSupportPrice(orderItemDailyFoodGroup.getUserSupportPriceHistories()));
        }
        refundPayPrice = refundItemPrice.subtract(refundSupportPrice);
        refundTotalPrice = refundCardPrice.add(refundTotalPoint);
        return new OrderDailyFoodDetailDto.RefundDto(refundPayPrice, refundItemPrice, refundSupportPrice, refundDeliveryFee, refundDeduction, refundTotalPrice, refundCardPrice, refundTotalPoint);
    }

    public static BigDecimal getRefundSupportPrice(List<DailyFoodSupportPrice> userSupportPriceHistories) {
        // 환불된 지원금이 존재하지 않을 때.
        if (userSupportPriceHistories.stream().allMatch(v -> v.getMonetaryStatus().equals(MonetaryStatus.DEDUCTION))) {
            return BigDecimal.ZERO;
        }
        // 지원금 환불 내역이 하나일 경우
        if (userSupportPriceHistories.size() == 1 && userSupportPriceHistories.get(0).getMonetaryStatus().equals(MonetaryStatus.REFUND)) {
            return userSupportPriceHistories.get(0).getUsingSupportPrice();
        }
        // 지원금 환불이 여러개 일 경우
        if (userSupportPriceHistories.size() > 1) {
            userSupportPriceHistories = userSupportPriceHistories.stream()
                    .sorted(Comparator.comparing(DailyFoodSupportPrice::getCreatedDateTime).reversed()).toList();
            return OrderUtil.getDeductedSupportPrice(userSupportPriceHistories.get(userSupportPriceHistories.size() - 1).getUsingSupportPrice(), userSupportPriceHistories.get(0).getUsingSupportPrice());
        }
        throw new ApiException(ExceptionEnum.NOT_MATCHED_SUPPORT_PRICE);
    }

    //orderName생성
    public static String makeOrderName(List<CartDailyFood> cartDailyFoods) {
        //장바구니에 담긴 아이템이 1개라면 상품명을 그대로 리턴
        if (cartDailyFoods.size() == 1) {
            return cartDailyFoods.get(0).getDailyFood().getFood().getName();
        }
        //장바구니에 담긴 아이템이 2개 이상이라면 "상품명 외 size-1 건"
        String firstFoodName = cartDailyFoods.get(0).getDailyFood().getFood().getName();
        Integer foodSize = cartDailyFoods.size() - 1;
        return firstFoodName + "외" + foodSize + "건";
    }

    public static BigDecimal getDeductedSupportPrice(BigDecimal oldPrice, BigDecimal renewSupportPrice) {
        return oldPrice.subtract(renewSupportPrice);
    }

    public PaymentCancelHistory cancelOrderItemMembership(String paymentKey, CreditCardInfo creditCardInfo, String cancelReason, OrderItemMembership orderItem, BigDecimal refundPrice) throws IOException, ParseException {
        //결제 취소 요청
        JSONObject response = tossUtil.billingCardCancelOne(paymentKey, cancelReason, refundPrice.intValue());
        System.out.println(response);

        String orderCode = response.get("orderId").toString();

        JSONObject checkout = (JSONObject) response.get("checkout");
        String checkOutUrl = checkout.get("url").toString();
        JSONArray cancels = (JSONArray) response.get("cancels");
        Integer refundablePrice = null;

        if (cancels.size() != 0 && cancels.size() != 1) {
            for (Object cancel : cancels) {
                JSONObject cancel1 = (JSONObject) cancel;
                refundablePrice = Integer.valueOf(cancel1.get("refundableAmount").toString());
                System.out.println(refundablePrice + " = refundablePrice");
            }
        }
        JSONObject cancel = (JSONObject) cancels.get(0);
        refundablePrice = Integer.valueOf(cancel.get("refundableAmount").toString());

        JSONObject card = (JSONObject) response.get("card");
        String paymentCardNumber = card.get("number").toString();

        //결제 취소 후 기록을 저장한다.
        return paymentCancleHistoryMapper.orderItemMembershipToEntity(cancelReason, refundPrice, orderItem, checkOutUrl, orderCode, BigDecimal.valueOf(refundablePrice), creditCardInfo);

    }

    public PaymentCancelHistory cancelOrderItemDailyFood(String paymentKey, String cancelReason, OrderItemDailyFood orderItem, RefundPriceDto refundPriceDto) throws IOException, ParseException {
        //결제 취소 요청
        JSONObject response = tossUtil.cardCancelOne(paymentKey, cancelReason, refundPriceDto.getPrice().intValue());
        System.out.println(response);

        String orderCode = response.get("orderId").toString();

        JSONObject checkout = (JSONObject) response.get("checkout");
        String checkOutUrl = checkout.get("url").toString();
        JSONArray cancels = (JSONArray) response.get("cancels");
        Integer refundablePrice = null;

        if (cancels.size() != 0 && cancels.size() != 1) {
            for (Object cancel : cancels) {
                JSONObject cancel1 = (JSONObject) cancel;
                refundablePrice = Integer.valueOf(cancel1.get("refundableAmount").toString());
                System.out.println(refundablePrice + " = refundablePrice");
            }
        }
        JSONObject cancel = (JSONObject) cancels.get(0);
        refundablePrice = Integer.valueOf(cancel.get("refundableAmount").toString());

        JSONObject card = (JSONObject) response.get("card");
        String paymentCardNumber = card.get("number").toString();

        //결제 취소 후 기록을 저장한다.
        return paymentCancleHistoryMapper.orderDailyItemFoodToEntity(cancelReason, refundPriceDto, orderItem, checkOutUrl, orderCode, BigDecimal.valueOf(refundablePrice));
    }

    public PaymentCancelHistory cancelOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, RefundPriceDto refundPriceDto, List<PaymentCancelHistory> paymentCancelHistories) {
        Order order = orderItemDailyFood.getOrder();
        // 남은 환불 가능 금액 = 총 상품 금액 - 남은 환불 금액
        BigDecimal refundablePrice = order.getTotalPrice().subtract(refundPriceDto.getPrice());
        if (!paymentCancelHistories.isEmpty()) {
            paymentCancelHistories = paymentCancelHistories.stream().sorted(Comparator.comparing(PaymentCancelHistory::getCancelDateTime).reversed()).collect(Collectors.toList());
            refundablePrice = paymentCancelHistories.get(0).getRefundablePrice().subtract(refundPriceDto.getPrice());
        }

        return paymentCancleHistoryMapper.orderDailyItemFoodToEntity("주문 전체 취소", refundPriceDto, orderItemDailyFood, null, order.getCode(), refundablePrice);
    }

    public PaymentCancelHistory cancelPointPaidOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, RefundPriceDto refundPriceDto) {
        Order order = orderItemDailyFood.getOrder();

        return paymentCancleHistoryMapper.orderDailyItemFoodToEntity("주문 마감 전 주문 취소", refundPriceDto, orderItemDailyFood, null, order.getCode(), refundPriceDto.getPrice());
    }

    public PaymentCancelHistory cancelOrderItemDailyFoodNice(String impUid, String cancelReason, OrderItemDailyFood orderItem, RefundPriceDto refundPriceDto) throws IOException, ParseException {
        //결제 취소 요청
        String token = niceUtil.getToken();

        JSONObject response = niceUtil.cardCancelOne(impUid, cancelReason, refundPriceDto.getPrice().intValue(), token);

        String orderCode = response.get("merchant_uid").toString();

        JSONArray checkout = (JSONArray) response.get("cancel_receipt_urls");
        String checkOutUrl = (String) checkout.get(0);
        long refundablePrice = (long) response.get("amount") - (long) response.get("cancel_amount");

        //결제 취소 후 기록을 저장한다.
        return paymentCancleHistoryMapper.orderDailyItemFoodToEntity(cancelReason, refundPriceDto, orderItem, checkOutUrl, orderCode, BigDecimal.valueOf(refundablePrice));

    }


    public PaymentCancelHistory cancelOrderItemMembershipNice(String paymentKey, CreditCardInfo creditCardInfo, String cancelReason, OrderItemMembership orderItem, BigDecimal refundPrice) throws IOException, ParseException {
        //결제 취소 요청
        String token = niceUtil.getToken();
        JSONObject response = niceUtil.cardCancelOne(paymentKey, cancelReason, refundPrice.intValue(), token);
        System.out.println(response);

        String orderCode = response.get("merchant_uid").toString();

        JSONArray checkout = (JSONArray) response.get("cancel_receipt_urls");
        String checkOutUrl = (String) checkout.get(0);
        long refundablePrice = (Long) response.get("amount") - (Long) response.get("cancel_amount");

        //결제 취소 후 기록을 저장한다.
        return paymentCancleHistoryMapper.orderItemMembershipToEntity(cancelReason, refundPrice, orderItem, checkOutUrl, orderCode, BigDecimal.valueOf(refundablePrice), creditCardInfo);

    }

    public static List<OrderCount> getTotalOrderCount(List<OrderItemDailyFood> orderItemDailyFoods) {
        Map<Group, List<OrderItemDailyFood>> groupOrderItems = orderItemDailyFoods.stream()
                .collect(Collectors.groupingBy(v -> v.getDailyFood().getGroup()));

        List<OrderCount> result = new ArrayList<>();

        for (Group group : groupOrderItems.keySet()) {
            OrderCount orderCount = new OrderCount();
            orderCount.setGroup(group);

            List<OrderCount.Count> counts = groupOrderItems.get(group).stream()
                    .collect(Collectors.groupingBy(v -> new AbstractMap.SimpleEntry<>(v.getDailyFood().getServiceDate(), v.getDailyFood().getDiningType()),
                            Collectors.summingInt(OrderItemDailyFood::getCount)))
                    .entrySet().stream()
                    .map(entry -> {
                        OrderCount.Count count = new OrderCount.Count();
                        count.setServiceDate(entry.getKey().getKey());
                        count.setDiningType(entry.getKey().getValue());
                        count.setCount(entry.getValue());
                        return count;
                    })
                    .collect(Collectors.toList());

            orderCount.setCounts(counts);
            result.add(orderCount);
        }

        return result;
    }
}
