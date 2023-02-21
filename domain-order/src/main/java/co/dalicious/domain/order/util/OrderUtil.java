package co.dalicious.domain.order.util;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.dto.OrderDailyFoodDetailDto;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.mapper.PaymentCancleHistoryMapper;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.converter.RefundPriceDto;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.GenerateRandomNumber;
import co.dalicious.system.util.PriceUtils;
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
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderUtil {
    private final TossUtil tossUtil;
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
        return PriceUtils.roundToOneDigit(price.multiply(BigDecimal.valueOf((100.0 - discountRate) / 100)));
    }

    public static BigDecimal discountPriceByRate(BigDecimal price, Integer discountRate) {
        return PriceUtils.roundToOneDigit(price.multiply(BigDecimal.valueOf(discountRate / 100.0)));
    }

    public static Boolean isMembership(User user, Group group) {
        if (user.getIsMembership()) {
            return true;
        }
        if (group instanceof Corporation corporation) {
            return corporation.getIsMembershipSupport();
        }
        return false;
    }

    public static DiscountDto checkMembershipAndGetDiscountDto(User user, Group group, Food food) {
        group = (Group) Hibernate.unproxy(group);
        return (isMembership(user, group)) ? DiscountDto.getDiscount(food) : DiscountDto.getDiscountWithoutMembership(food);
    }

    public static BigDecimal getPaidPriceGroupByOrderItemDailyFoodGroup(OrderItemDailyFoodGroup orderItemDailyFoodGroup) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodGroup.getOrderDailyFoods()) {
            if (orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                totalPrice = totalPrice.add(orderItemDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(orderItemDailyFood.getCount())));
            }
        }
        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFoodGroup.getUserSupportPriceHistories());
        totalPrice = totalPrice.subtract(usedSupportPrice);
        totalPrice = totalPrice.add(orderItemDailyFoodGroup.getDeliveryFee());

        // 포인트 사용으로 인해 식사 일정별 환불 가능 금액이 주문 전체 금액이 더 작을 경우
        Order order = orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder();
        if (order.getTotalPrice().compareTo(totalPrice) < 0) {
            return order.getTotalPrice();
        }

        return totalPrice;
    }

    public static BigDecimal getItemPriceGroupByOrderItemDailyFoodGroup(OrderItemDailyFoodGroup orderItemDailyFoodGroup) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodGroup.getOrderDailyFoods()) {
            if (orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                totalPrice = totalPrice.add(orderItemDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(orderItemDailyFood.getCount())));
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

    // OrderItemDailyFoodGroup당 환불 금액
    public static RefundPriceDto getRefundPrice(OrderItemDailyFood orderItemDailyFood, List<PaymentCancelHistory> paymentCancelHistories, BigDecimal usingPoint) {
        OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFood.getOrderItemDailyFoodGroup();
        // 환불 가능 금액 (일정 모든 아이템 금액 - 지원금)
        BigDecimal refundablePrice = getPaidPriceGroupByOrderItemDailyFoodGroup(orderItemDailyFoodGroup);
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
                    if(tossRefundablePrice.add(usingPoint.subtract(refundPoint)).compareTo(requestRefundPrice) < 0) {
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

    public static BigDecimal getRefundSupportPrice(List<UserSupportPriceHistory> userSupportPriceHistories) {
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
                    .sorted(Comparator.comparing(UserSupportPriceHistory::getCreatedDateTime).reversed()).toList();
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
        JSONObject response = tossUtil.cardCancelOne(paymentKey, cancelReason, refundPrice.intValue());
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
}
