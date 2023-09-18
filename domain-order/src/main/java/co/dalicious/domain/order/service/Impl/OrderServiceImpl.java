package co.dalicious.domain.order.service.Impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.enums.SupportType;
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
import co.dalicious.domain.order.util.OrderMembershipUtil;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.payment.dto.PaymentResponseDto;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.entity.enums.PaymentCompany;
import co.dalicious.domain.payment.repository.CreditCardInfoRepository;
import co.dalicious.domain.payment.repository.QCreditCardInfoRepository;
import co.dalicious.domain.payment.service.PaymentService;
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
import co.dalicious.domain.user.repository.QMembershipRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
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
    private final PaymentService paymentService;
    private final FoundersUtil foundersUtil;
    private final FoundersMapper foundersMapper;
    private final DiscountPolicy discountPolicy;
    private final MembershipDiscountEvent membershipDiscountEvent;
    private final QCreditCardInfoRepository qCreditCardInfoRepository;
    private final PointUtil pointUtil;
    private final OrderMembershipUtil orderMembershipUtil;
    private final QMembershipRepository qMembershipRepository;
    private final EntityManager entityManager;
    private final ConcurrentHashMap<User, Object> niceItemsLocks = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public Set<BigInteger> cancelOrderDailyFood(OrderDailyFood order, User user) throws IOException, ParseException {
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal deliveryFee = BigDecimal.ZERO;
        BigDecimal point = BigDecimal.ZERO;
        Set<BigInteger> makersIds = new HashSet<>();
        synchronized (niceItemsLocks.computeIfAbsent(user, u -> new Object())) {
            // 이전에 환불을 진행한 경우
            List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);
            List<PaymentCancelHistory> existedPaymentCancelHistories = paymentCancelHistories;

            for (OrderItem orderItem : order.getOrderItems()) {
                OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) Hibernate.unproxy(orderItem);
                checkIsAlreadyCanceled(orderItemDailyFood);

                checkIsOverLastOrderTime(orderItemDailyFood);

                BigDecimal usedSupportPrice = orderItemDailyFood.getOrderItemDailyFoodGroup().getUsingSupportPrice();

                RefundPriceDto refundPriceDto = getRefundPriceDto(orderItemDailyFood, order, paymentCancelHistories);

                price = price.add(refundPriceDto.getPrice());
                deliveryFee = deliveryFee.add(refundPriceDto.getDeliveryFee());
                point = point.add(refundPriceDto.getPoint());

                updateSupportPriceAndRefreshOrderGroup(refundPriceDto, orderItemDailyFood, usedSupportPrice);

                updateOrderStatusAndHandleMembershipRefund(refundPriceDto, orderItemDailyFood, user);

                // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
                if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0 || refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
                    PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderDailyFood(orderItemDailyFood, refundPriceDto, paymentCancelHistories);
                    paymentCancelHistories.add(paymentCancelHistoryRepository.save(paymentCancelHistory));
                    // 환불 포인트 내역 남기기
                    pointUtil.createPointHistoryByOthers(user, paymentCancelHistory.getId(), PointStatus.CANCEL, paymentCancelHistory.getRefundPointPrice());
                }
                orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);
                orderItemDailyFoodRepository.save(orderItemDailyFood);
                makersIds.add(orderItemDailyFood.getDailyFood().getFood().getMakers().getId());
            }
            user.updatePoint(user.getPoint().add(point));

            // 결제 환불 금액이 0일 경우 토스페이를 거치지 않고 환불
            if (price.compareTo(BigDecimal.ZERO) != 0) {
                if (!existedPaymentCancelHistories.isEmpty() && !existedPaymentCancelHistories.stream().filter(v -> v.getCancelPrice() != null && v.getCancelPrice().compareTo(BigDecimal.ZERO) > 0).toList().isEmpty()) {
                    paymentService.cancelPartial(user, order.getPaymentKey(), order.getCode(), price.intValue(), "전체 주문 취소 요청 (부분 취소)");
                }
                else {
                    paymentService.cancelAll(user, order.getPaymentKey(), order.getCode(), price.intValue(), "전체 주문 취소");
                }
            }
        }
        return makersIds;
    }

    @Override
    @Transactional
    public void cancelOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException {
        Order order = orderItemDailyFood.getOrder();
        User orderUser = order.getUser();

        synchronized (niceItemsLocks.computeIfAbsent(user, u -> new Object())) {
            if (!orderUser.equals(user)) {
                throw new ApiException(ExceptionEnum.UNAUTHORIZED);
            }

            checkIsAlreadyCanceled(orderItemDailyFood);

            checkIsOverLastOrderTime(orderItemDailyFood);

            // 이전에 환불을 진행한 경우
            List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

            BigDecimal usedSupportPrice = orderItemDailyFood.getOrderItemDailyFoodGroup().getUsingSupportPrice();

            RefundPriceDto refundPriceDto = getRefundPriceDto(orderItemDailyFood, order, paymentCancelHistories);

            updateSupportPriceAndRefreshOrderGroup(refundPriceDto, orderItemDailyFood, usedSupportPrice);

            updateOrderStatusAndHandleMembershipRefund(refundPriceDto, orderItemDailyFood, user);

            processPointBasedRefund(refundPriceDto, orderItemDailyFood, user);

            processPriceBasedRefund(refundPriceDto, orderItemDailyFood, order, user, paymentCancelHistories);

            orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
    public void adminCancelOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException {
        Order order = orderItemDailyFood.getOrder();
        User orderUser = (User) Hibernate.unproxy(order.getUser());

        synchronized (niceItemsLocks.computeIfAbsent(user, u -> new Object())) {
            if (!orderUser.equals(user)) {
                throw new ApiException(ExceptionEnum.UNAUTHORIZED);
            }

            checkIsAlreadyCanceled(orderItemDailyFood);

            // 이전에 환불을 진행한 경우
            List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

            BigDecimal usedSupportPrice = orderItemDailyFood.getOrderItemDailyFoodGroup().getUsingSupportPrice();

            RefundPriceDto refundPriceDto = getRefundPriceDto(orderItemDailyFood, order, paymentCancelHistories);

            updateSupportPriceAndRefreshOrderGroup(refundPriceDto, orderItemDailyFood, usedSupportPrice);

            updateOrderStatusAndHandleMembershipRefund(refundPriceDto, orderItemDailyFood, user);

            processPointBasedRefund(refundPriceDto, orderItemDailyFood, user);

            processPriceBasedRefund(refundPriceDto, orderItemDailyFood, order, user, paymentCancelHistories);

            userRepository.save(user);
            orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);
            orderItemDailyFoodRepository.save(orderItemDailyFood);

            entityManager.flush();
            entityManager.clear();
        }
    }

    @Override
    @Transactional
    public void payMembership(Membership preMembership, PaymentType paymentType) {

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

        try {
            //카드정보 가져오기
            Optional<CreditCardInfo> creditCardInfo = qCreditCardInfoRepository.findOneByUser(user);
            if (creditCardInfo.isEmpty()) {
                throw new ApiException(ExceptionEnum.CARD_NOT_FOUND);
            }
            CreditCardValidator.isValidCreditCard(creditCardInfo.get(), user);

            // 멤버십 결제 요청
            OrderMembership order = orderMembershipRepository.save(orderMembershipMapper.toOrderMembership(user, null, creditCardInfo.get(), membershipSubscriptionType, BigDecimal.ZERO, totalPrice, paymentType, membership));

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

            PaymentResponseDto paymentResponseDto = paymentService.pay(user, creditCardInfo.get(), totalPrice.intValue(), orderItemMembership.getOrder().getCode(), orderItemMembership.getMembershipSubscriptionType());

            orderItemMembership.updateDiscountPrice(membership.getMembershipSubscriptionType().getPrice().subtract(totalPrice));
            orderItemMembership.updateOrderStatus(OrderStatus.COMPLETED);

            order.updatePaymentKey(paymentResponseDto.getTransactionCode());
            order.updateReceiptUrl(paymentResponseDto.getReceipt());

            order.updateOrderMembershipAfterPayment(paymentResponseDto.getReceipt(), paymentResponseDto.getTransactionCode(), orderItemMembership.getOrder().getCode(), creditCardInfo.get());
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
    public void payMembership(User user, MembershipSubscriptionType membershipSubscriptionType, PeriodDto periodDto, PaymentType paymentType) throws IOException, ParseException {
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

        PaymentResponseDto paymentResponseDto = paymentService.pay(user, creditCardInfo.get(), totalPrice.intValue(), orderItemMembership.getOrder().getCode(), orderItemMembership.getMembershipSubscriptionType());

        orderItemMembership.updateDiscountPrice(membership.getMembershipSubscriptionType().getPrice().subtract(totalPrice));
        orderItemMembership.updateOrderStatus(OrderStatus.COMPLETED);

        order.updatePaymentKey(paymentResponseDto.getTransactionCode());
        order.updateReceiptUrl(paymentResponseDto.getReceipt());

        order.updateOrderMembershipAfterPayment(paymentResponseDto.getReceipt(), paymentResponseDto.getTransactionCode(), orderItemMembership.getOrder().getCode(), creditCardInfo.get());

        user.updateIsMembership(true);
    }

    @Override
    @Transactional
    public void updateFailedMembershipPayment(Membership membership) {
        membership.changeAutoPaymentStatus(false);
        membership.getUser().changeMembershipStatus(false);
        entityManager.merge(membership);
    }

    private RefundPriceDto getRefundPriceDto(OrderItemDailyFood orderItemDailyFood, Order order, List<PaymentCancelHistory> paymentCancelHistories) {
        RefundPriceDto refundPriceDto = null;
        SupportType supportType = UserSupportPriceUtil.getSupportTypeByOrderItem(orderItemDailyFood);
        if (supportType.equals(SupportType.PARTIAL)) {
            refundPriceDto = OrderUtil.getPartialRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());
        } else {
            refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());
        }
        return refundPriceDto;
    }

    private void updateOrderStatusAndHandleMembershipRefund(RefundPriceDto refundPriceDto, OrderItemDailyFood orderItemDailyFood, User user) {
        if (refundPriceDto.getIsLastItemOfGroup()) {
            orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
            if (user.getIsMembership() &&
                    Hibernate.unproxy(orderItemDailyFood.getDailyFood().getGroup()) instanceof Corporation corporation &&
                    corporation.getIsMembershipSupport()) {
                Membership membership = qMembershipRepository.findUserCurrentMembership(user, LocalDate.now());
                if (membership != null && orderMembershipUtil.isFirstItemInMembershipPeriod(membership, user, orderItemDailyFood)) {
                    orderMembershipUtil.refundCorporationMembership(membership);
                }
            }
            OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFood.getOrderItemDailyFoodGroup();
            orderItemDailyFoodGroup.updateOrderStatus(OrderStatus.CANCELED);
            orderItemDailyFoodGroupRepository.save(orderItemDailyFoodGroup);
        }
    }

    private void updateSupportPriceAndRefreshOrderGroup(RefundPriceDto refundPriceDto, OrderItemDailyFood orderItemDailyFood, BigDecimal usedSupportPrice) {
        if (!refundPriceDto.isSameSupportPrice(usedSupportPrice)) {
            List<DailyFoodSupportPrice> userSupportPriceHistories = orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories();
            for (DailyFoodSupportPrice dailyFoodSupportPrice : userSupportPriceHistories) {
                dailyFoodSupportPrice.updateMonetaryStatus(MonetaryStatus.REFUND);
            }
            DailyFoodSupportPrice dailyFoodSupportPrice = dailyFoodSupportPriceMapper.toEntity(orderItemDailyFood, refundPriceDto.getRenewSupportPrice());
            if (dailyFoodSupportPrice.getUsingSupportPrice().compareTo(BigDecimal.ZERO) != 0) {
                dailyFoodSupportPriceRepository.saveAndFlush(dailyFoodSupportPrice);
                OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFoodGroupRepository.saveAndFlush(orderItemDailyFood.getOrderItemDailyFoodGroup());
                entityManager.refresh(orderItemDailyFoodGroup);
            }
        }
    }

    private void checkIsAlreadyCanceled(OrderItemDailyFood orderItemDailyFood) {
        if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
            throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
        }
    }

    private void checkIsOverLastOrderTime(OrderItemDailyFood orderItemDailyFood) {
        if (!DailyFoodStatus.cancelableStatus().contains(orderItemDailyFood.getDailyFood().getDailyFoodStatus())) {
            throw new ApiException(ExceptionEnum.LAST_ORDER_TIME_PASSED);
        }
    }

    private void processPriceBasedRefund(RefundPriceDto refundPriceDto, OrderItemDailyFood orderItemDailyFood, Order order, User user, List<PaymentCancelHistory> paymentCancelHistories) throws IOException, ParseException {
        if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0) {
            PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemDailyFood(paymentCancelHistories, order.getPaymentKey(), "(백오피스)주문 마감 전 주문 취소", orderItemDailyFood, refundPriceDto);
            paymentCancelHistoryRepository.save(paymentCancelHistory);
            if(refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) > 0) {
                pointUtil.createPointHistoryByOthers(user, paymentCancelHistory.getId(), PointStatus.CANCEL, paymentCancelHistory.getRefundPointPrice());
            }
        }
    }

    private void processPointBasedRefund(RefundPriceDto refundPriceDto, OrderItemDailyFood orderItemDailyFood, User user) {
        if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) == 0 && refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
            PaymentCancelHistory paymentCancelHistory = orderUtil.cancelPointPaidOrderItemDailyFood(orderItemDailyFood, refundPriceDto);
            paymentCancelHistoryRepository.save(paymentCancelHistory);
            pointUtil.createPointHistoryByOthers(user, paymentCancelHistory.getId(), PointStatus.CANCEL, paymentCancelHistory.getRefundPointPrice());
        }
        user.updatePoint(user.getPoint().add(refundPriceDto.getPoint()));
    }
}
