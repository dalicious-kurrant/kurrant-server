package co.dalicious.domain.order.service.Impl;

import co.dalicious.domain.event.MembershipDiscountEvent;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.mapper.OrderMembershipMapper;
import co.dalicious.domain.order.mapper.OrderUserInfoMapper;
import co.dalicious.domain.order.mapper.DailyFoodSupportPriceMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.service.DiscountPolicy;
import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.repository.QCreditCardInfoRepository;
import co.dalicious.domain.payment.util.CreditCardValidator;
import co.dalicious.domain.payment.util.NiceUtil;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.converter.RefundPriceDto;
import co.dalicious.domain.user.entity.Founders;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.MembershipDiscountPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.entity.enums.PointStatus;
import co.dalicious.domain.user.mapper.FoundersMapper;
import co.dalicious.domain.user.repository.MembershipDiscountPolicyRepository;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.domain.user.util.FoundersUtil;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.domain.user.util.PointUtil;
import co.dalicious.system.enums.DiscountType;
import co.dalicious.system.util.PeriodDto;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderItemDailyFoodGroupRepository orderItemDailyFoodGroupRepository;
    private final OrderItemDailyFoodRepository orderItemDailyFoodRepository;
    private final UserRepository userRepository;
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;
    private final DailyFoodSupportPriceRepository dailyFoodSupportPriceRepository;
    private final DailyFoodSupportPriceMapper dailyFoodSupportPriceMapper;
    private final MembershipRepository membershipRepository;
    private final OrderMembershipMapper orderMembershipMapper;
    private final OrderMembershipRepository orderMembershipRepository;
    private final OrderItemMembershipRepository orderItemMembershipRepository;
    private final MembershipDiscountPolicyRepository membershipDiscountPolicyRepository;
    private final OrderUserInfoMapper orderUserInfoMapper;
    private final OrderUtil orderUtil;
    private final TossUtil tossUtil;
    private final NiceUtil niceUtil;
    private final FoundersUtil foundersUtil;
    private final FoundersMapper foundersMapper;
    private final DiscountPolicy discountPolicy;
    private final MembershipDiscountEvent membershipDiscountEvent;
    private final QCreditCardInfoRepository qCreditCardInfoRepository;
    private final PointUtil pointUtil;
    private final EntityManager entityManager;
    private final ConcurrentHashMap<User, Object> tossItemsLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<User, Object> tossItemLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<User, Object> niceItemsLocks = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public void cancelOrderDailyFood(OrderDailyFood order, User user) throws IOException, ParseException {
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal deliveryFee = BigDecimal.ZERO;
        BigDecimal point = BigDecimal.ZERO;

        // 이전에 환불을 진행한 경우
        List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

        // 순차적으로 환불이 일어날 수 있도록, 유저의 중복 요청을 synchronized로 해결
        synchronized (tossItemsLocks.computeIfAbsent(user, u -> new Object())) {

            for (OrderItem orderItem : order.getOrderItems()) {
                OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) Hibernate.unproxy(orderItem);
                // 상태값이 이미 7L(취소)인지 확인
                if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
                    continue;
                }

                if (orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.STOP_SALE) || orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.PASS_LAST_ORDER_TIME)) {
                    throw new ApiException(ExceptionEnum.LAST_ORDER_TIME_PASSED);
                }

                BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories());

                RefundPriceDto refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());
                price = price.add(refundPriceDto.getPrice());
                deliveryFee = deliveryFee.add(refundPriceDto.getDeliveryFee());
                point = point.add(refundPriceDto.getPoint());

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

                // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
                if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0 || refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
                    PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemDailyFood(orderItemDailyFood, refundPriceDto, paymentCancelHistories);
                    paymentCancelHistories.add(paymentCancelHistoryRepository.save(paymentCancelHistory));
                    // 환불 포인트 내역 남기기
                    pointUtil.createPointHistoryByOthers(user, paymentCancelHistory.getId(), PointStatus.CANCEL, paymentCancelHistory.getRefundPointPrice());
                }
                orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);

                if (refundPriceDto.getIsLastItemOfGroup()) {
                    orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
                }
            }
            user.updatePoint(user.getPoint().add(point));

            // 결제 환불 금액이 0일 경우 토스페이를 거치지 않고 환불
            if (price.compareTo(BigDecimal.ZERO) != 0) {
                tossUtil.cardCancelOne(order.getPaymentKey(), "전체 주문 취소", price.intValue());
            }

        }
    }

    @Override
    @Transactional
    public void cancelOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException {
        Order order = orderItemDailyFood.getOrder();
        User orderUser = (User) Hibernate.unproxy(order.getUser());
        synchronized (tossItemLocks.computeIfAbsent(user, u -> new Object())) {
            if (!orderUser.equals(user)) {
                throw new ApiException(ExceptionEnum.UNAUTHORIZED);
            }

            // 상태값이 이미 7L(취소)인지 확인
            if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
                throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
            }

//        if (orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.STOP_SALE) || orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.PASS_LAST_ORDER_TIME)) {
//            throw new ApiException(ExceptionEnum.LAST_ORDER_TIME_PASSED);
//        }

            // 이전에 환불을 진행한 경우
            List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

            BigDecimal usedSupportPrice = orderItemDailyFood.getOrderItemDailyFoodGroup().getUsingSupportPrice();

            RefundPriceDto refundPriceDto = null;

            if (((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpot().getGroup().getName().equals("메드트로닉")) {
                refundPriceDto = OrderUtil.getMedtronicRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());
            } else {
                refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());
            }


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

            PaymentCancelHistory paymentCancelHistory = null;
            // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
            if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0) {
                paymentCancelHistory = orderUtil.cancelOrderItemDailyFood(order.getPaymentKey(), "주문 마감 전 주문 취소", orderItemDailyFood, refundPriceDto);
                paymentCancelHistoryRepository.save(paymentCancelHistory);
            }
            // 결제 정보가 없지만 포인트 내역이 있다면
            if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) == 0 && refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
                paymentCancelHistory = orderUtil.cancelPointPaidOrderItemDailyFood(orderItemDailyFood, refundPriceDto);
                paymentCancelHistoryRepository.save(paymentCancelHistory);
            }

            // 환불 포인트가 있으면
            if (paymentCancelHistory != null && refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
                // 환불 포인트 내역 남기기
                pointUtil.createPointHistoryByOthers(user, paymentCancelHistory.getId(), PointStatus.CANCEL, paymentCancelHistory.getRefundPointPrice());
            }

            user.updatePoint(user.getPoint().add(refundPriceDto.getPoint()));
            orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);

            if (refundPriceDto.getIsLastItemOfGroup()) {
                orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
            }
        }
    }

    @Override
    public void payMembership(User user, MembershipSubscriptionType membershipSubscriptionType, PeriodDto periodDto, PaymentType paymentType) {
        BigDecimal defaultPrice = membershipSubscriptionType.getPrice();
        BigDecimal yearDescriptionDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, membershipSubscriptionType.getDiscountRate());
        // 기간 할인 가격이 일치하는지 확인
        BigDecimal periodDiscountPrice = BigDecimal.ZERO;
        Integer periodDiscountRate = 0;
        // 베스핀글로벌 멤버십 첫 결제 할인
        if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) {
            periodDiscountRate = 50;
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, periodDiscountRate);
        } else if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.YEAR)) {
            periodDiscountRate = 30;
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, periodDiscountRate);
        }
        BigDecimal totalPrice = defaultPrice.subtract(yearDescriptionDiscountPrice).subtract(periodDiscountPrice);

        // 멤버십 등록
        Membership membership = membershipRepository.save(orderMembershipMapper.toMembership(membershipSubscriptionType, user, periodDto));

        // 연간 구독 구매자라면, 할인 정책 저장.
        if (membershipSubscriptionType.equals(MembershipSubscriptionType.YEAR)) {
            MembershipDiscountPolicy yearDescriptionDiscountPolicy = orderMembershipMapper.toMembershipDiscountPolicy(membership, DiscountType.YEAR_DESCRIPTION_DISCOUNT, MembershipSubscriptionType.YEAR.getDiscountRate());
            membershipDiscountPolicyRepository.save(yearDescriptionDiscountPolicy);
        }


        // TODO: 할인 혜택을 가지고 있는 유저인지 확인 후 할인 정책 저장.
        if (periodDiscountPrice.compareTo(BigDecimal.ZERO) > 0) {
            MembershipDiscountPolicy periodDiscountPolicy = orderMembershipMapper.toMembershipDiscountPolicy(membership, DiscountType.PERIOD_DISCOUNT, periodDiscountRate);
            membershipDiscountPolicyRepository.save(periodDiscountPolicy);
        }

        //카드정보 가져오기
        Optional<CreditCardInfo> creditCardInfo = qCreditCardInfoRepository.findOneByUser(user);
        if (creditCardInfo.isEmpty()) {
            throw new ApiException(ExceptionEnum.CARD_NOT_FOUND);
        }
        CreditCardValidator.isValidCreditCard(creditCardInfo.get(), user);

        // 멤버십 결제 요청
        OrderUserInfoDto orderUserInfoDto = orderUserInfoMapper.toDto(user);
        OrderMembership order = orderMembershipRepository.save(orderMembershipMapper.toOrderMembership(orderUserInfoDto, creditCardInfo.get(), membershipSubscriptionType, BigDecimal.ZERO, totalPrice, paymentType, membership));

        // 멤버십 결제 내역 등록(진행중 상태)
        OrderItemMembership orderItemMembership = orderItemMembershipRepository.save(orderMembershipMapper.toOrderItemMembership(order, membership, periodDiscountRate));

        // 파운더스 확인
        if (!foundersUtil.isFounders(user) && !foundersUtil.isOverFoundersLimit()) {
            Founders founders = foundersMapper.toEntity(user, membership, foundersUtil.getMaxFoundersNumber() + 1);
            foundersUtil.saveFounders(founders);
        }

        // 결제 진행. 실패시 오류 날림
        BigDecimal price = discountPolicy.orderItemTotalPrice(orderItemMembership);

        String customerKey = creditCardInfo.get().getCustomerKey();
        String billingKey = creditCardInfo.get().getTossBillingKey();

        try {
            JSONObject payResult = tossUtil.payToCard(customerKey, totalPrice.intValue(), orderItemMembership.getOrder().getCode(), orderItemMembership.getMembershipSubscriptionType(), billingKey);

            // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
            if (payResult.get("status").equals("DONE")) {
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

    @Override
    @Transactional
    public void updateFailedMembershipPayment(Membership membership) {
        membership.changeAutoPaymentStatus(false);
        membership.getUser().changeMembershipStatus(false);
        entityManager.merge(membership);
    }

    @Override
    @Transactional
    public void cancelOrderDailyFoodNice(OrderDailyFood order, User user) throws IOException, ParseException {
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal deliveryFee = BigDecimal.ZERO;
        BigDecimal point = BigDecimal.ZERO;
        synchronized (niceItemsLocks.computeIfAbsent(user, u -> new Object())) {
            // 이전에 환불을 진행한 경우
            List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

            for (OrderItem orderItem : order.getOrderItems()) {
                OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) Hibernate.unproxy(orderItem);
                // 상태값이 이미 7L(취소)라면 건너뛰기.
                if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
                    continue;
                }

                if (orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.STOP_SALE) || orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.PASS_LAST_ORDER_TIME)) {
                    throw new ApiException(ExceptionEnum.LAST_ORDER_TIME_PASSED);
                }

                BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories());

                RefundPriceDto refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());
                price = price.add(refundPriceDto.getPrice());
                deliveryFee = deliveryFee.add(refundPriceDto.getDeliveryFee());
                point = point.add(refundPriceDto.getPoint());

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

                // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
                if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0 || refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
                    PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemDailyFood(orderItemDailyFood, refundPriceDto, paymentCancelHistories);
                    paymentCancelHistories.add(paymentCancelHistoryRepository.save(paymentCancelHistory));
                    // 환불 포인트 내역 남기기
                    pointUtil.createPointHistoryByOthers(user, paymentCancelHistory.getId(), PointStatus.CANCEL, paymentCancelHistory.getRefundPointPrice());
                }
                orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);

                if (refundPriceDto.getIsLastItemOfGroup()) {
                    orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
                }
            }
            user.updatePoint(user.getPoint().add(point));

            // 결제 환불 금액이 0일 경우 토스페이를 거치지 않고 환불
            if (price.compareTo(BigDecimal.ZERO) != 0) {
                String token = niceUtil.getToken();
                niceUtil.cardCancelOne(order.getPaymentKey(), "전체 주문 취소", price.intValue(), token);
            }
        }
    }

    @Override
    @Transactional
    public void cancelOrderItemDailyFoodNice(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException {
        Order order = orderItemDailyFood.getOrder();
        User orderUser = (User) Hibernate.unproxy(order.getUser());

        synchronized (niceItemsLocks.computeIfAbsent(user, u -> new Object())) {

            if (!orderUser.equals(user)) {
                throw new ApiException(ExceptionEnum.UNAUTHORIZED);
            }

            // 상태값이 이미 7L(취소)인지 확인
            if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
                throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
            }

            if (!DailyFoodStatus.cancelableStatus().contains(orderItemDailyFood.getDailyFood().getDailyFoodStatus())) {
                throw new ApiException(ExceptionEnum.LAST_ORDER_TIME_PASSED);
            }

            // 이전에 환불을 진행한 경우
            List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

            BigDecimal usedSupportPrice = orderItemDailyFood.getOrderItemDailyFoodGroup().getUsingSupportPrice();

            RefundPriceDto refundPriceDto = null;

            if (((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpot().getGroup().getName().equals("메드트로닉")) {
                refundPriceDto = OrderUtil.getMedtronicRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());
            } else {
                refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());
            }


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

            PaymentCancelHistory paymentCancelHistory = null;
            // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
            if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0) {
                paymentCancelHistory = orderUtil.cancelOrderItemDailyFoodNice(order.getPaymentKey(), "주문 마감 전 주문 취소", orderItemDailyFood, refundPriceDto);
                paymentCancelHistoryRepository.save(paymentCancelHistory);
            }
            // 결제 정보가 없지만 포인트 내역이 있다면
            if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) == 0 && refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
                paymentCancelHistory = orderUtil.cancelPointPaidOrderItemDailyFood(orderItemDailyFood, refundPriceDto);
                paymentCancelHistoryRepository.save(paymentCancelHistory);
            }

            // 환불한 포인트가 있으면
            if (paymentCancelHistory != null && refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
                // 환불 포인트 내역 남기기
                pointUtil.createPointHistoryByOthers(user, paymentCancelHistory.getId(), PointStatus.CANCEL, paymentCancelHistory.getRefundPointPrice());
            }


            user.updatePoint(user.getPoint().add(refundPriceDto.getPoint()));
            orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);

            if (refundPriceDto.getIsLastItemOfGroup()) {
                orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
            }
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
    public void adminCancelOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException {
        Order order = orderItemDailyFood.getOrder();
        User orderUser = (User) Hibernate.unproxy(order.getUser());

        synchronized (niceItemsLocks.computeIfAbsent(user, u -> new Object())) {
            // 상태값이 이미 7L(취소)인지 확인
            if (orderItemDailyFood.getOrderStatus().equals(OrderStatus.CANCELED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
                throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
            }

            if (!orderUser.equals(user)) {
                throw new ApiException(ExceptionEnum.UNAUTHORIZED);
            }

            // 이전에 환불을 진행한 경우
            List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

            BigDecimal usedSupportPrice = orderItemDailyFood.getOrderItemDailyFoodGroup().getUsingSupportPrice();

            RefundPriceDto refundPriceDto = null;

            if (((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpot().getGroup().getName().equals("메드트로닉")) {
                refundPriceDto = OrderUtil.getMedtronicRefundPriceAdmin(orderItemDailyFood, paymentCancelHistories, order.getPoint());
            } else {
                refundPriceDto = OrderUtil.getRefundPriceAdmin(orderItemDailyFood, paymentCancelHistories, order.getPoint());
            }

            if (!refundPriceDto.isSameSupportPrice(usedSupportPrice)) {
                List<DailyFoodSupportPrice> userSupportPriceHistories = orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories();
                for (DailyFoodSupportPrice dailyFoodSupportPrice : userSupportPriceHistories) {
                    dailyFoodSupportPrice.updateMonetaryStatus(MonetaryStatus.REFUND);
                    dailyFoodSupportPriceRepository.save(dailyFoodSupportPrice);
                }
                DailyFoodSupportPrice dailyFoodSupportPrice = dailyFoodSupportPriceMapper.toEntity(orderItemDailyFood, refundPriceDto.getRenewSupportPrice());
                if (dailyFoodSupportPrice.getUsingSupportPrice().compareTo(BigDecimal.ZERO) != 0) {
                    dailyFoodSupportPriceRepository.save(dailyFoodSupportPrice);
                }
            }

            PaymentCancelHistory paymentCancelHistory = null;
            // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
            if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0) {
                paymentCancelHistory = orderUtil.cancelOrderItemDailyFoodNice(order.getPaymentKey(), "주문 마감 전 주문 취소", orderItemDailyFood, refundPriceDto);
                paymentCancelHistoryRepository.save(paymentCancelHistory);
            }
            // 결제 정보가 없지만 포인트 내역이 있다면
            if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) == 0 && refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
                paymentCancelHistory = orderUtil.cancelPointPaidOrderItemDailyFood(orderItemDailyFood, refundPriceDto);
                paymentCancelHistoryRepository.save(paymentCancelHistory);
            }

            // 환불한 포인트가 있으면
            if (paymentCancelHistory != null && refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
                // 환불 포인트 내역 남기기
                pointUtil.createPointHistoryByOthers(user, paymentCancelHistory.getId(), PointStatus.CANCEL, paymentCancelHistory.getRefundPointPrice());
            }


            user.updatePoint(user.getPoint().add(refundPriceDto.getPoint()));
            userRepository.save(user);
            orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);
            orderItemDailyFoodRepository.save(orderItemDailyFood);

            if (refundPriceDto.getIsLastItemOfGroup()) {
                OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFood.getOrderItemDailyFoodGroup();
                orderItemDailyFoodGroup.updateOrderStatus(OrderStatus.CANCELED);
                orderItemDailyFoodGroupRepository.save(orderItemDailyFoodGroup);
            }

            entityManager.flush();
            entityManager.clear();
        }
    }

    @Override
    @Transactional
    public void payMembershipNice(Membership preMembership, PaymentType paymentType) throws IOException, ParseException {

        PeriodDto periodDto = (preMembership.getMembershipSubscriptionType().equals(MembershipSubscriptionType.MONTH)) ?
                MembershipUtil.getStartAndEndDateMonthly(preMembership.getEndDate()) :
                MembershipUtil.getStartAndEndDateYearly(preMembership.getEndDate().plusMonths(1));

        User user = preMembership.getUser();
        MembershipSubscriptionType membershipSubscriptionType = preMembership.getMembershipSubscriptionType();

        BigDecimal defaultPrice = membershipSubscriptionType.getPrice();
        BigDecimal yearDescriptionDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, membershipSubscriptionType.getDiscountRate());
        // 기간 할인 가격이 일치하는지 확인
        BigDecimal periodDiscountPrice = BigDecimal.ZERO;
        Integer periodDiscountRate = 0;
        // 베스핀글로벌 멤버십 첫 결제 할인
        if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) {
            periodDiscountRate = 50;
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, periodDiscountRate);
        } else if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.YEAR)) {
            periodDiscountRate = 30;
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, periodDiscountRate);
        }
        BigDecimal totalPrice = defaultPrice.subtract(yearDescriptionDiscountPrice).subtract(periodDiscountPrice);

        // 멤버십 등록
        Membership membership = membershipRepository.save(orderMembershipMapper.toMembership(membershipSubscriptionType, user, periodDto));

        // 연간 구독 구매자라면, 할인 정책 저장.
        if (membershipSubscriptionType.equals(MembershipSubscriptionType.YEAR)) {
            MembershipDiscountPolicy yearDescriptionDiscountPolicy = orderMembershipMapper.toMembershipDiscountPolicy(membership, DiscountType.YEAR_DESCRIPTION_DISCOUNT, MembershipSubscriptionType.YEAR.getDiscountRate());
            membershipDiscountPolicyRepository.save(yearDescriptionDiscountPolicy);
        }

        // TODO: 할인 혜택을 가지고 있는 유저인지 확인 후 할인 정책 저장.
        if (periodDiscountPrice.compareTo(BigDecimal.ZERO) > 0) {
            MembershipDiscountPolicy periodDiscountPolicy = orderMembershipMapper.toMembershipDiscountPolicy(membership, DiscountType.PERIOD_DISCOUNT, periodDiscountRate);
            membershipDiscountPolicyRepository.save(periodDiscountPolicy);
        }

        //카드정보 가져오기
        Optional<CreditCardInfo> creditCardInfo = qCreditCardInfoRepository.findOneByUser(user);
        if (creditCardInfo.isEmpty()) {
            throw new ApiException(ExceptionEnum.CARD_NOT_FOUND);
        }
        CreditCardValidator.isValidCreditCard(creditCardInfo.get(), user);

        // 멤버십 결제 요청
        OrderUserInfoDto orderUserInfoDto = orderUserInfoMapper.toDto(user);
        OrderMembership order = orderMembershipRepository.save(orderMembershipMapper.toOrderMembership(orderUserInfoDto, creditCardInfo.get(), membershipSubscriptionType, BigDecimal.ZERO, totalPrice, paymentType, membership));

        // 멤버십 결제 내역 등록(진행중 상태)
        OrderItemMembership orderItemMembership = orderItemMembershipRepository.save(orderMembershipMapper.toOrderItemMembership(order, membership, periodDiscountRate));

        // 파운더스 확인
        if (!foundersUtil.isFounders(user) && !foundersUtil.isOverFoundersLimit()) {
            Founders founders = foundersMapper.toEntity(user, membership, foundersUtil.getMaxFoundersNumber() + 1);
            foundersUtil.saveFounders(founders);
        }

        // 결제 진행. 실패시 오류 날림
        BigDecimal price = discountPolicy.orderItemTotalPrice(orderItemMembership);

        String billingKey = creditCardInfo.get().getNiceBillingKey();

        //String billingKey, Integer amount, String orderId, String token, String orderName
        String token = niceUtil.getToken();
        JSONObject payResult = niceUtil.niceBilling(billingKey, totalPrice.intValue(), orderItemMembership.getOrder().getCode(), token, orderItemMembership.getMembershipSubscriptionType());

        try {
            if (payResult == null) throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
            Long code = (Long) payResult.get("code");
            JSONObject JSONResult = (JSONObject) payResult.get("response");
            String status = JSONResult.get("status").toString();
            // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
            if (code == 0 && !status.equals("failed")) {
                orderItemMembership.updateDiscountPrice(membership.getMembershipSubscriptionType().getPrice().subtract(totalPrice));
                orderItemMembership.updateOrderStatus(OrderStatus.COMPLETED);

                //Order 테이블에 paymentKey와 receiptUrl 업데이트
                String receiptUrl = JSONResult.get("receipt_url").toString();

                String paymentKey = JSONResult.get("imp_uid").toString();
                order.updatePaymentKey(paymentKey);
                order.updateReceiptUrl(receiptUrl);

                order.updateOrderMembershipAfterPayment(receiptUrl, paymentKey, orderItemMembership.getOrder().getCode(), creditCardInfo.get());
            } else {
                orderItemMembership.updateOrderStatus(OrderStatus.FAILED);
                throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
            }
        }
        // 결제 실패시 orderMembership의 상태값을 결제 실패 상태(3)로 변경
        catch (Exception e) {
            preMembership.changeAutoPaymentStatus(false);
            preMembership.getUser().changeMembershipStatus(false);
            log.info("[Membership 결제 실패] : {}", preMembership.getId());
        }
    }

    @Override
    @Transactional
    public void payMembershipNice(User user, MembershipSubscriptionType membershipSubscriptionType, PeriodDto periodDto, PaymentType paymentType) throws IOException, ParseException {
        BigDecimal defaultPrice = membershipSubscriptionType.getPrice();
        BigDecimal yearDescriptionDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, membershipSubscriptionType.getDiscountRate());
        // 기간 할인 가격이 일치하는지 확인
        BigDecimal periodDiscountPrice = BigDecimal.ZERO;
        Integer periodDiscountRate = 0;
        // 베스핀글로벌 멤버십 첫 결제 할인
        if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) {
            periodDiscountRate = 50;
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, periodDiscountRate);
        } else if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.YEAR)) {
            periodDiscountRate = 30;
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, periodDiscountRate);
        }
        BigDecimal totalPrice = defaultPrice.subtract(yearDescriptionDiscountPrice).subtract(periodDiscountPrice);

        // 멤버십 등록
        Membership membership = membershipRepository.save(orderMembershipMapper.toMembership(membershipSubscriptionType, user, periodDto));

        // 연간 구독 구매자라면, 할인 정책 저장.
        if (membershipSubscriptionType.equals(MembershipSubscriptionType.YEAR)) {
            MembershipDiscountPolicy yearDescriptionDiscountPolicy = orderMembershipMapper.toMembershipDiscountPolicy(membership, DiscountType.YEAR_DESCRIPTION_DISCOUNT, MembershipSubscriptionType.YEAR.getDiscountRate());
            membershipDiscountPolicyRepository.save(yearDescriptionDiscountPolicy);
        }

        // TODO: 할인 혜택을 가지고 있는 유저인지 확인 후 할인 정책 저장.
        if (periodDiscountPrice.compareTo(BigDecimal.ZERO) > 0) {
            MembershipDiscountPolicy periodDiscountPolicy = orderMembershipMapper.toMembershipDiscountPolicy(membership, DiscountType.PERIOD_DISCOUNT, periodDiscountRate);
            membershipDiscountPolicyRepository.save(periodDiscountPolicy);
        }

        //카드정보 가져오기
        Optional<CreditCardInfo> creditCardInfo = qCreditCardInfoRepository.findOneByUser(user);
        if (creditCardInfo.isEmpty()) {
            throw new ApiException(ExceptionEnum.CARD_NOT_FOUND);
        }
        CreditCardValidator.isValidCreditCard(creditCardInfo.get(), user);

        // 멤버십 결제 요청
        OrderUserInfoDto orderUserInfoDto = orderUserInfoMapper.toDto(user);
        OrderMembership order = orderMembershipRepository.save(orderMembershipMapper.toOrderMembership(orderUserInfoDto, creditCardInfo.get(), membershipSubscriptionType, BigDecimal.ZERO, totalPrice, paymentType, membership));

        // 멤버십 결제 내역 등록(진행중 상태)
        OrderItemMembership orderItemMembership = orderItemMembershipRepository.save(orderMembershipMapper.toOrderItemMembership(order, membership, periodDiscountRate));

        // 파운더스 확인
        if (!foundersUtil.isFounders(user) && !foundersUtil.isOverFoundersLimit()) {
            Founders founders = foundersMapper.toEntity(user, membership, foundersUtil.getMaxFoundersNumber() + 1);
            foundersUtil.saveFounders(founders);
        }

        // 결제 진행. 실패시 오류 날림
        BigDecimal price = discountPolicy.orderItemTotalPrice(orderItemMembership);

        String billingKey = creditCardInfo.get().getNiceBillingKey();

        //String billingKey, Integer amount, String orderId, String token, String orderName
        String token = niceUtil.getToken();
        JSONObject payResult = niceUtil.niceBilling(billingKey, totalPrice.intValue(), orderItemMembership.getOrder().getCode(), token, orderItemMembership.getMembershipSubscriptionType());


        if (payResult == null) throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        Long code = (Long) payResult.get("code");
        JSONObject JSONResult = (JSONObject) payResult.get("response");
        String status = JSONResult.get("status").toString();
        // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
        if (code == 0 && !status.equals("failed")) {
            orderItemMembership.updateDiscountPrice(membership.getMembershipSubscriptionType().getPrice().subtract(totalPrice));
            orderItemMembership.updateOrderStatus(OrderStatus.COMPLETED);

            //Order 테이블에 paymentKey와 receiptUrl 업데이트
            String receiptUrl = JSONResult.get("receipt_url").toString();

            String paymentKey = JSONResult.get("imp_uid").toString();
            order.updatePaymentKey(paymentKey);
            order.updateReceiptUrl(receiptUrl);

            order.updateOrderMembershipAfterPayment(receiptUrl, paymentKey, orderItemMembership.getOrder().getCode(), creditCardInfo.get());
        } else {
            orderItemMembership.updateOrderStatus(OrderStatus.FAILED);
            throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        }
    }

}
