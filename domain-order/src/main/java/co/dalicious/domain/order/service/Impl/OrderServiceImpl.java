package co.dalicious.domain.order.service.Impl;

import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.mapper.OrderMembershipMapper;
import co.dalicious.domain.order.mapper.OrderUserInfoMapper;
import co.dalicious.domain.order.mapper.UserSupportPriceHistoryReqMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.service.DiscountPolicy;
import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.repository.CreditCardInfoRepository;
import co.dalicious.domain.payment.util.CreditCardValidator;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.converter.RefundPriceDto;
import co.dalicious.domain.user.entity.Founders;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.MembershipDiscountPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.mapper.FoundersMapper;
import co.dalicious.domain.user.repository.MembershipDiscountPolicyRepository;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.util.FoundersUtil;
import co.dalicious.system.enums.DiscountType;
import co.dalicious.system.util.PeriodDto;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;
    private final UserSupportPriceHistoryRepository userSupportPriceHistoryRepository;
    private final UserSupportPriceHistoryReqMapper userSupportPriceHistoryReqMapper;
    private final MembershipRepository membershipRepository;
    private final OrderMembershipMapper orderMembershipMapper;
    private final OrderMembershipRepository orderMembershipRepository;
    private final OrderItemMembershipRepository orderItemMembershipRepository;
    private final MembershipDiscountPolicyRepository membershipDiscountPolicyRepository;
    private final OrderUserInfoMapper orderUserInfoMapper;
    private final OrderUtil orderUtil;
    private final TossUtil tossUtil;
    private final FoundersUtil foundersUtil;
    private final FoundersMapper foundersMapper;
    private final DiscountPolicy discountPolicy;
    private final CreditCardInfoRepository creditCardInfoRepository;

    @Override
    @Transactional
    public void cancelOrderDailyFood(OrderDailyFood order, User user) throws IOException, ParseException {
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal deliveryFee = BigDecimal.ZERO;
        BigDecimal point = BigDecimal.ZERO;

        // 이전에 환불을 진행한 경우
        List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

        for (OrderItem orderItem : order.getOrderItems()) {
            OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) Hibernate.unproxy(orderItem);
            // 상태값이 이미 7L(취소)인지 확인
            if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
                throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
            }

            if(orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.STOP_SALE) || orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.PASS_LAST_ORDER_TIME)) {
                throw new ApiException(ExceptionEnum.LAST_ORDER_TIME_PASSED);
            }

            BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories());

            RefundPriceDto refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());
            price = price.add(refundPriceDto.getPrice());
            deliveryFee = deliveryFee.add(refundPriceDto.getDeliveryFee());
            point = point.add(refundPriceDto.getPoint());

            if (!refundPriceDto.isSameSupportPrice(usedSupportPrice)) {
                List<UserSupportPriceHistory> userSupportPriceHistories = orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories();
                for (UserSupportPriceHistory userSupportPriceHistory : userSupportPriceHistories) {
                    userSupportPriceHistory.updateMonetaryStatus(MonetaryStatus.REFUND);
                }
                UserSupportPriceHistory userSupportPriceHistory = userSupportPriceHistoryReqMapper.toEntity(orderItemDailyFood, refundPriceDto.getRenewSupportPrice());
                if (userSupportPriceHistory.getUsingSupportPrice().compareTo(BigDecimal.ZERO) != 0) {
                    userSupportPriceHistoryRepository.save(userSupportPriceHistory);
                }
            }

            // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
            if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0 || refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
                PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemDailyFood(orderItemDailyFood, refundPriceDto, paymentCancelHistories);
                paymentCancelHistories.add(paymentCancelHistoryRepository.save(paymentCancelHistory));
            }
            orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);

            if (refundPriceDto.getIsLastItemOfGroup()) {
                orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
            }
        }
        user.updatePoint(user.getPoint().add(point));

        // 결제 환불 금액이 0일 경우 토스페이를 거치지 않고 환불
        if(price.compareTo(BigDecimal.ZERO) != 0) {
            tossUtil.cardCancelOne(order.getPaymentKey(), "전체 주문 취소", price.intValue());
        }
    }

    @Override
    @Transactional
    public void cancelOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException {
        Order order = orderItemDailyFood.getOrder();

        if(!order.getUser().equals(user)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        // 상태값이 이미 7L(취소)인지 확인
        if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
            throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
        }

        if(orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.STOP_SALE) || orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.PASS_LAST_ORDER_TIME)) {
            throw new ApiException(ExceptionEnum.LAST_ORDER_TIME_PASSED);
        }

        // 이전에 환불을 진행한 경우
        List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

        BigDecimal usedSupportPrice = orderItemDailyFood.getOrderItemDailyFoodGroup().getUsingSupportPrice();

        RefundPriceDto refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());


        if (!refundPriceDto.isSameSupportPrice(usedSupportPrice)) {
            List<UserSupportPriceHistory> userSupportPriceHistories = orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories();
            for (UserSupportPriceHistory userSupportPriceHistory : userSupportPriceHistories) {
                userSupportPriceHistory.updateMonetaryStatus(MonetaryStatus.REFUND);
            }
            UserSupportPriceHistory userSupportPriceHistory = userSupportPriceHistoryReqMapper.toEntity(orderItemDailyFood, refundPriceDto.getRenewSupportPrice());
            if (userSupportPriceHistory.getUsingSupportPrice().compareTo(BigDecimal.ZERO) != 0) {
                userSupportPriceHistoryRepository.save(userSupportPriceHistory);
            }
        }

        // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
        if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0) {
            PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemDailyFood(order.getPaymentKey(), "주문 마감 전 주문 취소", orderItemDailyFood, refundPriceDto);
            paymentCancelHistoryRepository.save(paymentCancelHistory);
        }

        user.updatePoint(user.getPoint().add(refundPriceDto.getPoint()));
        orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);

        if (refundPriceDto.getIsLastItemOfGroup()) {
            orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
        }
    }

    @Override
    @Transactional
    public void payMembership(User user, MembershipSubscriptionType membershipSubscriptionType, PeriodDto periodDto, PaymentType paymentType) {
        BigDecimal defaultPrice = membershipSubscriptionType.getPrice();
        BigDecimal yearDescriptionDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, membershipSubscriptionType.getDiscountRate());
        // TODO: 기간할인 추가시 기간할인 조회 로직 추가 필요
        BigDecimal periodDiscountPrice = BigDecimal.ZERO;
        BigDecimal totalPrice = defaultPrice.subtract(yearDescriptionDiscountPrice).subtract(periodDiscountPrice);

        // 멤버십 등록
        Membership membership = membershipRepository.save(orderMembershipMapper.toMembership(membershipSubscriptionType, user, periodDto));

        // 연간 구독 구매자라면, 할인 정책 저장.
        if(membershipSubscriptionType.equals(MembershipSubscriptionType.YEAR)) {
            MembershipDiscountPolicy yearDescriptionDiscountPolicy = orderMembershipMapper.toMembershipDiscountPolicy(membership, DiscountType.YEAR_DESCRIPTION_DISCOUNT);
            membershipDiscountPolicyRepository.save(yearDescriptionDiscountPolicy);
        }


        /* TODO: 할인 혜택을 가지고 있는 유저인지 확인 후 할인 정책 저장.
        MembershipDiscountPolicy periodDiscountPolicy = MembershipDiscountPolicy.builder()
                .membership(membership)
                .discountRate(membershipSubscriptionType.getDiscountRate())
                .discountType(DiscountType.PERIOD_DISCOUNT)
                .build();
        membershipDiscountPolicyRepository.save(periodDiscountPolicy);
         */

        //카드정보 가져오기
        Optional<CreditCardInfo> creditCardInfo = creditCardInfoRepository.findOneByUserAndDefaultType(user, 2);
        if(creditCardInfo.isEmpty()) {
            throw new ApiException(ExceptionEnum.CARD_NOT_FOUND);
        }
        CreditCardValidator.isValidCreditCard(creditCardInfo.get(), user);

        // 멤버십 결제 요청
        OrderUserInfoDto orderUserInfoDto = orderUserInfoMapper.toDto(user);
        OrderMembership order = orderMembershipRepository.save(orderMembershipMapper.toOrderMembership(orderUserInfoDto, creditCardInfo.get(), membershipSubscriptionType, BigDecimal.ZERO, totalPrice, paymentType, membership));

        // 멤버십 결제 내역 등록(진행중 상태)
        OrderItemMembership orderItemMembership = orderItemMembershipRepository.save(orderMembershipMapper.toOrderItemMembership(order, membership));

        // 파운더스 확인
        if(!foundersUtil.isFounders(user) &&!foundersUtil.isOverFoundersLimit()) {
            Founders founders =  foundersMapper.toEntity(user, membership, foundersUtil.getMaxFoundersNumber()+1);
            foundersUtil.saveFounders(founders);
        }

        // 결제 진행. 실패시 오류 날림
        BigDecimal price = discountPolicy.orderItemTotalPrice(orderItemMembership);

        String customerKey = creditCardInfo.get().getCustomerKey();
        String billingKey = creditCardInfo.get().getBillingKey();

        try {
            JSONObject payResult = tossUtil.payToCard(customerKey, price.intValue(), orderItemMembership.getOrder().getCode(), orderItemMembership.getMembershipSubscriptionType(), billingKey);

            // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
            if (payResult.get("status").equals("DONE")) {
                order.updateDefaultPrice(defaultPrice);
                order.updateTotalPrice(price);
                orderItemMembership.updateDiscountPrice(membership.getMembershipSubscriptionType().getPrice().subtract(price));
                orderItemMembership.updateOrderStatus(OrderStatus.COMPLETED);

                //Order 테이블에 paymentKey와 receiptUrl 업데이트
                JSONObject receipt = (JSONObject) payResult.get("receipt");
                String receiptUrl = receipt.get("url").toString();

                String paymentKey = (String) payResult.get("paymentKey");
                order.updatePaymentKey(paymentKey);
                order.updateReceiptUrl(receiptUrl);

                order.updateOrderMembershipAfterPayment(receiptUrl, paymentKey, orderItemMembership.getOrder().getCode(), creditCardInfo.get());
            }
            // 결제 실패시 orderMembership의 상태값을 결제 실패 상태(3)로 변경
            else {
                orderItemMembership.updateOrderStatus(OrderStatus.FAILED);
                throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
            }
        } catch (ApiException e) {
            orderItemMembership.updateOrderStatus(OrderStatus.FAILED);
            throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        } catch (IOException | InterruptedException | ParseException e) {
            throw new RuntimeException(e);
        }
        user.changeMembershipStatus(true);
    }
}
