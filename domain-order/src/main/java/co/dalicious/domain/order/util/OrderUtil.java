package co.dalicious.domain.order.util;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.mapper.PaymentCancleHistoryMapper;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.converter.RefundPriceDto;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.GenerateRandomNumber;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
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

    public static void refundOrderMembership(User user, OrderItemMembership orderItemMembership) {
        if(orderItemMembership.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            orderItemMembership.getMembership().changeMembershipStatus(MembershipStatus.PROCESSING);
            orderItemMembership.getMembership().changeAutoPaymentStatus(true);
            user.changeMembershipStatus(true);
        }
        else if(orderItemMembership.getOrderStatus().equals(OrderStatus.AUTO_REFUND)) {
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
        if(user.getIsMembership()) {
            return true;
        }
        if(group instanceof Corporation corporation) {
            return corporation.getIsMembershipSupport();
        }
        return false;
    }

    public static DiscountDto checkMembershipAndGetDiscountDto(User user, Group group, Food food) {
        return (isMembership(user, group)) ? DiscountDto.getDiscount(food) : DiscountDto.getDiscountWithNoMembership(food);
    }

    public static BigDecimal getPaidPriceGroupByOrderItemDailyFoodGroup(OrderItemDailyFoodGroup orderItemDailyFoodGroup) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodGroup.getOrderDailyFoods()) {
            if(orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                totalPrice = totalPrice.add(orderItemDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(orderItemDailyFood.getCount())));
            }
        }
        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFoodGroup.getUserSupportPriceHistories());
        totalPrice = totalPrice.subtract(usedSupportPrice);
        totalPrice = totalPrice.add(orderItemDailyFoodGroup.getDeliveryFee());
        return totalPrice;
    }

    public static BigDecimal getItemPriceGroupByOrderItemDailyFoodGroup(OrderItemDailyFoodGroup orderItemDailyFoodGroup) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodGroup.getOrderDailyFoods()) {
            if(orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                totalPrice = totalPrice.add(orderItemDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(orderItemDailyFood.getCount())));
            }
        }
        return totalPrice;
    }

    // OrderItemDailyFoodGroup당 환불 금액
    public static RefundPriceDto getRefundPrice(OrderItemDailyFood orderItemDailyFood, List<PaymentCancelHistory> paymentCancelHistories,  BigDecimal usingPoint) {
        // 환불 가능 금액 (일정 모든 아이템 금액 - 지원금)
        BigDecimal refundablePrice = getPaidPriceGroupByOrderItemDailyFoodGroup(orderItemDailyFood.getOrderItemDailyFoodGroup());
        // 식사 일정에 따른 아이템 금액
        BigDecimal itemsPrice = getItemPriceGroupByOrderItemDailyFoodGroup(orderItemDailyFood.getOrderItemDailyFoodGroup());
        // 사용한 지원금
        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories());
        // 환불 요청 금액
        BigDecimal requestRefundPrice = orderItemDailyFood.getOrderItemTotalPrice();
        // 업데이트 되어야할 지원금
        BigDecimal renewSupportPrice = usedSupportPrice;

        // 1. 지원금을 업데이트 시켜야하는 상황인지 확인(식사 일정 별 구매한 모든 정기 식사 가격 - 환불 요청 아이템 가격 < 사용한 회사 지원금)
        Boolean needToUpdateSupportPrice = itemsPrice.subtract(requestRefundPrice).compareTo(usedSupportPrice) < 0;

        // 2. 지원금 업데이트가 필요한 경우
        if(needToUpdateSupportPrice) {
            renewSupportPrice = itemsPrice.subtract(requestRefundPrice);
            requestRefundPrice = requestRefundPrice.subtract(getDeductedSupportPrice(usedSupportPrice, renewSupportPrice));
            if(requestRefundPrice.compareTo(BigDecimal.ZERO) == 0) {
                return new RefundPriceDto(BigDecimal.ZERO, renewSupportPrice, BigDecimal.ZERO);
            }
        }

        // 3. 환불 가능 금액 > 환불 요청 금액
        if (refundablePrice.compareTo(requestRefundPrice) >= 0) {
            if(!paymentCancelHistories.isEmpty()) {
                paymentCancelHistories = paymentCancelHistories.stream().sorted(Comparator.comparing(PaymentCancelHistory::getCancelDateTime).reversed()).collect(Collectors.toList());
                PaymentCancelHistory paymentCancelHistory = paymentCancelHistories.get(0);
                BigDecimal tossRefundablePrice = paymentCancelHistory.getRefundablePrice();
                if(tossRefundablePrice.compareTo(requestRefundPrice) < 0) {
                    throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
                }
            }
            return new RefundPriceDto(requestRefundPrice, renewSupportPrice, BigDecimal.ZERO);
        }

        // 4. 환불 가능 금액 < 환불 요청 금액
        if (refundablePrice.compareTo(requestRefundPrice) < 0) {
            if(requestRefundPrice.subtract(refundablePrice).compareTo(usingPoint) > 0) {
                throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
            }
            return new RefundPriceDto(refundablePrice, renewSupportPrice, requestRefundPrice.subtract(refundablePrice));
        }
        throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
    }

    //orderName생성
    public static String makeOrderName(List<CartDailyFood> cartDailyFoods){
        //장바구니에 담긴 아이템이 1개라면 상품명을 그대로 리턴
        if (cartDailyFoods.size() == 1){
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

    public PaymentCancelHistory cancelOrderItemDailyFood (String paymentKey, CreditCardInfo creditCardInfo, String cancelReason, OrderItemDailyFood orderItem, RefundPriceDto refundPriceDto) throws IOException, ParseException {
        //결제 취소 요청
        JSONObject response = tossUtil.cardCancelOne(paymentKey, cancelReason, refundPriceDto.getPrice().intValue());
        System.out.println(response);

        String orderCode = response.get("orderId").toString();

        JSONObject checkout = (JSONObject) response.get("checkout");
        String checkOutUrl = checkout.get("url").toString();
        JSONArray cancels = (JSONArray) response.get("cancels");
        Integer refundablePrice = null;

        if (cancels.size() != 0 && cancels.size() != 1){
            for (Object cancel : cancels){
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
        return paymentCancleHistoryMapper.toEntity(cancelReason, refundPriceDto, orderItem, orderCode, checkOutUrl, BigDecimal.valueOf(refundablePrice), creditCardInfo);

    }

}
