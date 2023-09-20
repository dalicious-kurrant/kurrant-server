package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.event.MembershipDiscountEvent;
import co.dalicious.domain.order.dto.OrderMembershipReqDto;
import co.dalicious.domain.order.dto.OrderMembershipResDto;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.mapper.OrderMembershipResMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.user.dto.DailyFoodMembershipDiscountDto;
import co.dalicious.domain.user.dto.MembershipBenefitDto;
import co.dalicious.domain.user.dto.MembershipDto;
import co.dalicious.domain.user.dto.MembershipSubscriptionTypeDto;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.entity.enums.PointStatus;
import co.dalicious.domain.user.mapper.MembershipBenefitMapper;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.repository.QPointHistoryRepository;
import co.dalicious.domain.user.util.FoundersUtil;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.dalicious.domain.user.repository.QMembershipRepository;
import co.kurrant.app.public_api.util.UserUtil;
import co.kurrant.app.public_api.service.MembershipService;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipServiceImpl implements MembershipService {
    private final UserUtil userUtil;
    private final MembershipRepository membershipRepository;
    private final QMembershipRepository QmembershipRepository;
    private final OrderItemMembershipRepository orderItemMembershipRepository;
    private final FoundersUtil foundersUtil;
    private final OrderMembershipRepository orderMembershipRepository;
    private final BigDecimal REFUND_YEARLY_MEMBERSHIP_PER_MONTH = MembershipSubscriptionType.YEAR.getPrice().multiply(BigDecimal.valueOf((100 - MembershipSubscriptionType.YEAR.getDiscountRate()) * 0.01)).divide(BigDecimal.valueOf(12));
    private final BigDecimal DISCOUNT_YEARLY_MEMBERSHIP_PER_MONTH = MembershipSubscriptionType.MONTH.getPrice().subtract(REFUND_YEARLY_MEMBERSHIP_PER_MONTH);
    private final OrderMembershipResMapper orderMembershipResMapper;
    private final QOrderItemDailyFoodRepository qOrderItemDailyFoodRepository;
    private final DeliveryFeePolicy deliveryFeePolicy;
    private final MembershipBenefitMapper membershipBenefitMapper;
    private final QMembershipRepository qMembershipRepository;
    private final OrderUtil orderUtil;
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;
    private final OrderService orderService;
    private final MembershipDiscountEvent membershipDiscountEvent;
    private final QPointHistoryRepository qPointHistoryRepository;

    @Override
    @Transactional
    public void joinMembership(SecurityUser securityUser, OrderMembershipReqDto orderMembershipReqDto) throws IOException, ParseException {
        User user = userUtil.getUser(securityUser);
        // 금액 정보가 일치하는지 확인
        MembershipSubscriptionType membershipSubscriptionType = MembershipSubscriptionType.ofCode(orderMembershipReqDto.getSubscriptionType());
        // 1. 기본 가격이 일치하는지 확인
        BigDecimal defaultPrice = membershipSubscriptionType.getPrice();
        if (!(orderMembershipReqDto.getDefaultPrice().compareTo(defaultPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }
        // 2. 연간 구독 할인 가격이 일치하는지 확인
        BigDecimal yearDescriptionDiscountPrice = OrderUtil.discountPriceByRate(membershipSubscriptionType.getPrice(), membershipSubscriptionType.getDiscountRate());
        if (!(orderMembershipReqDto.getYearDescriptionDiscountPrice().compareTo(yearDescriptionDiscountPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }
        // 3. 기간 할인 가격이 일치하는지 확인
        BigDecimal periodDiscountPrice = BigDecimal.ZERO;
        // 베스핀글로벌 멤버십 첫 결제 할인
        if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) {
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, 50);
        } else if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.YEAR)) {
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, 30);
        }
        if (!(orderMembershipReqDto.getPeriodDiscountPrice().compareTo(periodDiscountPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }
        // 4. 총 가격이 일치하는지 확인
        BigDecimal totalPrice = defaultPrice.subtract(yearDescriptionDiscountPrice).subtract(periodDiscountPrice);
        if (!(orderMembershipReqDto.getTotalPrice().compareTo(totalPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }

        // 5. 이 사람이 기존에 멤버십을 가입했는 지 확인 후 멤버십 기간 저장
        PeriodDto periodDto = null;
        if (user.getIsMembership()) {
            Membership membership = qMembershipRepository.findUserCurrentMembership(user, LocalDate.now());
            if (membership != null) {
                LocalDate currantEndDate = membership.getEndDate();
                if (LocalDate.now().isBefore(currantEndDate)) {
                    throw new ApiException(ExceptionEnum.ALREADY_EXISTING_MEMBERSHIP);
                }
                // 구독 타입에 따라 기간 정하기
                periodDto = (membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) ?
                        MembershipUtil.getStartAndEndDateMonthly(currantEndDate) :
                        MembershipUtil.getStartAndEndDateYearly(currantEndDate);
            }
        } else {
            LocalDate now = LocalDate.now();
            // 구독 타입에 따라 기간 정하기
            periodDto = (membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) ?
                    MembershipUtil.getStartAndEndDateMonthly(now) :
                    MembershipUtil.getStartAndEndDateYearly(now);
        }

        assert periodDto != null;
        // 멤버십 등록
        orderService.payMembership(user, membershipSubscriptionType, periodDto, PaymentType.ofCode(orderMembershipReqDto.getPaymentType()));
    }

    @Override
    public OrderMembershipResDto getOrderMembership(SecurityUser securityUser, Integer subscriptionType) {
        User user = userUtil.getUser(securityUser);
        // TODO: 결제 수단 구현 완료시 Response 값에 유저 결제 수단 정보가 포함될 수 있도록 한다.
        MembershipSubscriptionType membershipSubscriptionType = MembershipSubscriptionType.ofCode(subscriptionType);
        BigDecimal defaultPrice = membershipSubscriptionType.getPrice();
        // 1. 월간 구독인지 연간 구독인지 확인 후 할인 금액 도출
        Integer yearSubscriptionDiscountRate = 0;
        if (membershipSubscriptionType == MembershipSubscriptionType.YEAR) {
            yearSubscriptionDiscountRate = membershipSubscriptionType.getDiscountRate();
        }
        BigDecimal yearSubscriptionDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, yearSubscriptionDiscountRate);
        // 2. 기간 할인이 적용되는 유저인지 확인

        Integer periodDiscountRate = 0;
        BigDecimal periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice.subtract(yearSubscriptionDiscountPrice), periodDiscountRate);

        // 베스핀 글로벌 멤버십 첫 결제 할인 이벤트
        if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) {
            periodDiscountRate = 50;
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, periodDiscountRate);
        } else if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.YEAR)) {
            periodDiscountRate = 30;
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, periodDiscountRate);
        }
        // 3. 할인이 적용된 최종 가격 도출
        return OrderMembershipResDto.builder()
                .subscriptionType(subscriptionType)
                .defaultPrice(defaultPrice)
                .yearDescriptionDiscountPrice(yearSubscriptionDiscountPrice)
                .periodDiscountPrice(periodDiscountPrice)
                .totalPrice(defaultPrice.subtract(yearSubscriptionDiscountPrice).subtract(periodDiscountPrice))
                .build();
    }

    @Override
    @Transactional
    public void refundMembership(User user, Order order, Membership membership, OrderMembership orderMembership) throws IOException, ParseException {
        // TODO: 연간구독 해지시, membership endDate update.
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderItemDailyFoodRepository.findByUserAndGroupAndServiceDateBetween(user, null,membership.getStartDate(), membership.getEndDate());

        // 멤버십 결제금액 가져오기
        BigDecimal paidPrice = order.getTotalPrice();

        // 멤버십 결제 가져오기
        List<OrderItem> orderItems = order.getOrderItems();
        OrderItemMembership orderItemMembership = null;
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                orderItemMembership = (OrderItemMembership) orderItem;
            }
        }
        if (orderItemMembership == null) {
            throw new ApiException(ExceptionEnum.ORDER_ITEM_NOT_FOUND);
        }

        // 환불 가능 금액 계산하기
        BigDecimal refundPrice = getRefundableMembershipPrice(orderItemDailyFoods, orderItemMembership);

        // 자동 환불 설정
        orderItemMembership.updateOrderStatus(OrderStatus.AUTO_REFUND);
        OrderUtil.orderMembershipStatusUpdate(user, orderItemMembership);

        // 주문 상태 변경
        if (paidPrice.compareTo(refundPrice) < 0) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }

        // 취소 내역 저장
        PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemMembershipNice(order.getPaymentKey(), orderMembership.getCreditCardInfo(), "멤버십 환불", orderItemMembership, refundPrice);
        paymentCancelHistoryRepository.save(paymentCancelHistory);
    }

    @Override
    @Transactional
    public void unsubscribeMembership(SecurityUser securityUser) throws IOException, ParseException {
        // 유저 가져오기
        User user = userUtil.getUser(securityUser);
        // 멤버십 사용중인 유저인지 가져오기
        if (!user.getIsMembership()) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }
        // 현재 사용중인 멤버십 가져오기
        Membership userCurrentMembership = QmembershipRepository.findUserCurrentMembership(user, LocalDate.now());

        if (userCurrentMembership == null) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }
        // 멤버십 주문 가져오기
        OrderItemMembership orderItemMembership = orderItemMembershipRepository.findOneByMembership(userCurrentMembership).orElseThrow(
                () -> new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND)
        );
        OrderMembership orderMembership = orderMembershipRepository.findOneByMembership(userCurrentMembership).orElseThrow(
                () -> new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND)
        );

        // 사용한 날짜 계산하기
        int membershipUsingDays = userCurrentMembership.getStartDate().until(LocalDate.now()).getDays();

        // 7일 이하일 경우 멤버십 환불
        if (membershipUsingDays <= 7) {
            refundMembership(user, orderItemMembership.getOrder(), userCurrentMembership, orderMembership);
        }

        // 파운더스 멤버일 경우 해지
        foundersUtil.cancelFounders(user);
        userCurrentMembership.changeAutoPaymentStatus(false);
    }

    @Override
    @Transactional
    public MembershipBenefitDto getMembershipBenefit(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMonthAgo = LocalDate.now().minusMonths(3).atStartOfDay();

        Membership membership = qMembershipRepository.findUserCurrentMembership(user, now.toLocalDate());

        OrderItemMembership orderItemMembership = orderItemMembershipRepository.findOneByMembership(membership).orElseThrow(
                () -> new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND)
        );

        if (membership == null) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }

        // 멤버십 이용 금액 혜택을 받은 주문 상품 가져오기
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderItemDailyFoodRepository.findAllWhichGetMembershipBenefit(user, now, threeMonthAgo);

        // 최근 3개월동안 멤버십을 통해 할인 받은 정기식사 할인 금액을 가져온다.
        DailyFoodMembershipDiscountDto dailyFoodMembershipDiscountDto = getDailyFoodPriceBenefits(orderItemDailyFoods);

        // 환불 가능 금액 계산하기
        BigDecimal refundablePrice = getRefundableMembershipPrice(orderItemDailyFoods, orderItemMembership);

        // 정기식사 포인트 적립 조회
        BigDecimal earnDailyFoodEarnPoint = qPointHistoryRepository.getTotalEarnPointByPeriodAndStatus(user, threeMonthAgo, now, PointStatus.earnDailyFoodPoint());

        return membershipBenefitMapper.toDto(membership, dailyFoodMembershipDiscountDto, refundablePrice, earnDailyFoodEarnPoint);
    }

    @Override
    @Transactional
    public DailyFoodMembershipDiscountDto getDailyFoodPriceBenefits(List<OrderItemDailyFood> orderItemDailyFoods) {
        BigDecimal totalMembershipDiscountPrice = BigDecimal.ZERO;
        BigDecimal totalMembershipDiscountDeliveryFee = BigDecimal.ZERO;
        Set<OrderItemDailyFoodGroup> orderItemDailyFoodGroups = new HashSet<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            totalMembershipDiscountPrice = totalMembershipDiscountPrice.add(orderItemDailyFood.getMembershipDiscountPrice());
            orderItemDailyFoodGroups.add(orderItemDailyFood.getOrderItemDailyFoodGroup());
        }
        for (OrderItemDailyFoodGroup orderItemDailyFoodGroup : orderItemDailyFoodGroups) {
            totalMembershipDiscountDeliveryFee = totalMembershipDiscountDeliveryFee.add(deliveryFeePolicy.getNoMembershipGroupDeliveryFee(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getDailyFood().getGroup()));
        }
        return new DailyFoodMembershipDiscountDto(totalMembershipDiscountPrice, totalMembershipDiscountDeliveryFee);
    }

    @Override
    public BigDecimal getRefundableMembershipPrice(List<OrderItemDailyFood> orderItemDailyFoods, OrderItemMembership orderItemMembership) {
        Membership membership = orderItemMembership.getMembership();
        List<OrderItemDailyFood> orderItemDailyFoodList = orderItemDailyFoods.stream().filter(v -> v.getCreatedDateTime().after(Timestamp.valueOf(membership.getStartDate().atStartOfDay())) &&
                v.getCreatedDateTime().before(Timestamp.valueOf(membership.getEndDate().atTime(23, 59, 59)))).toList();
        DailyFoodMembershipDiscountDto dailyFoodCurrentMembershipDiscountDto = getDailyFoodPriceBenefits(orderItemDailyFoodList);

        return orderItemMembership.getOrder().getTotalPrice().subtract(dailyFoodCurrentMembershipDiscountDto.getTotalMembershipDiscountPrice()).subtract(dailyFoodCurrentMembershipDiscountDto.getTotalMembershipDiscountDeliveryFee());

    }

    @Override
    @Transactional
    public List<MembershipDto> retrieveMembership(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        // 멤버십 종료 날짜의 오름차순으로 멤버십 정보를 조회한다.

        // create a specification to specify the conditions of the query
        Specification<Membership> specification = (root, query, builder) ->
                builder.and(
                        builder.equal(root.get("user"), user),
                        builder.in(root.get("membershipStatus")).value(1).value(2).value(3)
                );

        // create a Sort object to specify the ordering of the query
        Sort sort = Sort.by(Sort.Direction.ASC, "endDate");

        // execute the query using the repository
        List<Membership> memberships = membershipRepository.findAll(specification, sort);
        List<OrderItemMembership> orderItemMemberships = orderItemMembershipRepository.findAllByMembership(memberships);

        List<MembershipDto> membershipDtos = new ArrayList<>();
        int membershipUsingPeriod = 0;
        for (OrderItemMembership orderItemMembership : orderItemMemberships) {
            membershipUsingPeriod += MembershipUtil.getPeriodWithStartAndEndDate(orderItemMembership.getMembership().getStartDate(), orderItemMembership.getMembership().getEndDate());
            // membershipDto를 생성한다.
            MembershipDto membershipDto = orderMembershipResMapper.toDto(orderItemMembership, membershipUsingPeriod);
            membershipDtos.add(membershipDto);
        }
        // 멤버십 종료 날짜의 내림차순으로 멤버십 이용내역을 반환한다.
        Collections.reverse(membershipDtos);
        return membershipDtos;
    }


    public void yearlyDescriptionRefund(Membership membership) {
        // 월간 구독권인지, 연간 구독권인지 구분 필요
        int period = 0;
        BigDecimal paidPrice = BigDecimal.ZERO;
        if (membership.getMembershipSubscriptionType().equals(MembershipSubscriptionType.YEAR)) {
            period = MembershipUtil.getPeriodWithStartAndEndDate(membership.getStartDate(), LocalDate.now());
            // Day가 일치하지 않으면 1을 추가한다. -> 2022-12-22과 2023-01-21은 0의 결과 값이 나오므로.
            if (membership.getStartDate().getDayOfMonth() > LocalDate.now().getDayOfMonth()) {
                period++;
            }
            paidPrice = paidPrice.subtract(DISCOUNT_YEARLY_MEMBERSHIP_PER_MONTH.multiply(BigDecimal.valueOf(period)));
        }
    }

    @Override
    @Transactional
    public void joinMembershipNice(SecurityUser securityUser, OrderMembershipReqDto orderMembershipReqDto) throws IOException, ParseException {
        User user = userUtil.getUser(securityUser);
        // 금액 정보가 일치하는지 확인
        MembershipSubscriptionType membershipSubscriptionType = MembershipSubscriptionType.ofCode(orderMembershipReqDto.getSubscriptionType());
        // 1. 기본 가격이 일치하는지 확인
        BigDecimal defaultPrice = membershipSubscriptionType.getPrice();
        if (!(orderMembershipReqDto.getDefaultPrice().compareTo(defaultPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }
        // 2. 연간 구독 할인 가격이 일치하는지 확인
        BigDecimal yearDescriptionDiscountPrice = OrderUtil.discountPriceByRate(membershipSubscriptionType.getPrice(), membershipSubscriptionType.getDiscountRate());
        if (!(orderMembershipReqDto.getYearDescriptionDiscountPrice().compareTo(yearDescriptionDiscountPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }
        // 3. 기간 할인 가격이 일치하는지 확인
        BigDecimal periodDiscountPrice = BigDecimal.ZERO;
        // 베스핀글로벌 멤버십 첫 결제 할인
        if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) {
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, 50);
        } else if (membershipDiscountEvent.isBespinGlobal(user) && membershipSubscriptionType.equals(MembershipSubscriptionType.YEAR)) {
            periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, 30);
        }
        if (!(orderMembershipReqDto.getPeriodDiscountPrice().compareTo(periodDiscountPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }
        // 4. 총 가격이 일치하는지 확인
        BigDecimal totalPrice = defaultPrice.subtract(yearDescriptionDiscountPrice).subtract(periodDiscountPrice);
        if (!(orderMembershipReqDto.getTotalPrice().compareTo(totalPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }

        // 5. 이 사람이 기존에 멤버십을 가입했는 지 확인 후 멤버십 기간 저장
        PeriodDto periodDto = null;
        if (user.getIsMembership()) {
            Membership membership = qMembershipRepository.findUserCurrentMembership(user, LocalDate.now());
            if (membership != null) {
                LocalDate currantEndDate = membership.getEndDate();
                if (LocalDate.now().isBefore(currantEndDate)) {
                    throw new ApiException(ExceptionEnum.ALREADY_EXISTING_MEMBERSHIP);
                }
                // 구독 타입에 따라 기간 정하기
                periodDto = (membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) ?
                        MembershipUtil.getStartAndEndDateMonthly(currantEndDate) :
                        MembershipUtil.getStartAndEndDateYearly(currantEndDate);
            }
        } else {
            LocalDate now = LocalDate.now();
            // 구독 타입에 따라 기간 정하기
            periodDto = (membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) ?
                    MembershipUtil.getStartAndEndDateMonthly(now) :
                    MembershipUtil.getStartAndEndDateYearly(now);
        }

        assert periodDto != null;
        // 멤버십 등록
        orderService.payMembership(user, membershipSubscriptionType, periodDto, PaymentType.ofCode(orderMembershipReqDto.getPaymentType()));
    }

    @Override
    @Transactional
    public void refundMembershipNice(User user, Order order, Membership membership, OrderMembership orderMembership) throws IOException, ParseException {
        // TODO: 연간구독 해지시, membership endDate update.
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderItemDailyFoodRepository.findByUserAndGroupAndServiceDateBetween(user, null, membership.getStartDate(), membership.getEndDate());

        // 멤버십 결제금액 가져오기
        BigDecimal paidPrice = order.getTotalPrice();

        // 멤버십 결제 가져오기
        List<OrderItem> orderItems = order.getOrderItems();
        OrderItemMembership orderItemMembership = null;
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                orderItemMembership = (OrderItemMembership) orderItem;
            }
        }
        if (orderItemMembership == null) {
            throw new ApiException(ExceptionEnum.ORDER_ITEM_NOT_FOUND);
        }

        // 환불 가능 금액 계산하기
        BigDecimal refundPrice = getRefundableMembershipPrice(orderItemDailyFoods, orderItemMembership);

        // 자동 환불 설정
        orderItemMembership.updateOrderStatus(OrderStatus.AUTO_REFUND);
        OrderUtil.orderMembershipStatusUpdate(user, orderItemMembership);

        // 주문 상태 변경
        if (paidPrice.compareTo(refundPrice) < 0) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }

        // 취소 내역 저장
        PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemMembershipNice(order.getPaymentKey(), orderMembership.getCreditCardInfo(), "멤버십 환불", orderItemMembership, refundPrice);
        paymentCancelHistoryRepository.save(paymentCancelHistory);
    }

    @Override
    @Transactional
    public void unsubscribeMembershipNice(SecurityUser securityUser) throws IOException, ParseException {
        // 유저 가져오기
        User user = userUtil.getUser(securityUser);
        // 멤버십 사용중인 유저인지 가져오기
        if (!user.getIsMembership()) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }
        // 현재 사용중인 멤버십 가져오기
        Membership userCurrentMembership = QmembershipRepository.findUserCurrentMembership(user, LocalDate.now());

        if (userCurrentMembership == null) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }
        // 멤버십 주문 가져오기
        OrderItemMembership orderItemMembership = orderItemMembershipRepository.findOneByMembership(userCurrentMembership).orElseThrow(
                () -> new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND)
        );
        OrderMembership orderMembership = orderMembershipRepository.findOneByMembership(userCurrentMembership).orElseThrow(
                () -> new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND)
        );

        // 사용한 날짜 계산하기
        int membershipUsingDays = userCurrentMembership.getStartDate().until(LocalDate.now()).getDays();

        // 7일 이하일 경우 멤버십 환불
        if (membershipUsingDays <= 7) {
            refundMembershipNice(user, orderItemMembership.getOrder(), userCurrentMembership, orderMembership);
        }

        // 파운더스 멤버일 경우 해지
        foundersUtil.cancelFounders(user);
        userCurrentMembership.changeAutoPaymentStatus(false);
    }

    @Override
    @Transactional
    public List<MembershipSubscriptionTypeDto> getMembershipSubscriptionInfo(SecurityUser securityUser) {
        // 베스핀 글로벌
        List<MembershipSubscriptionTypeDto> membershipSubscriptionTypeDtos = new ArrayList<>();

        User user = userUtil.getUser(securityUser);

        if(membershipDiscountEvent.isBespinGlobal(user)) {
            MembershipSubscriptionTypeDto monthSubscription = MembershipSubscriptionTypeDto.builder()
                    .membershipSubscriptionType(MembershipSubscriptionType.MONTH.getMembershipSubscriptionType())
                    .price(MembershipSubscriptionType.MONTH.getPrice())
                    .discountRate(50)
                    .discountedPrice(MembershipSubscriptionType.MONTH.getPrice().multiply(BigDecimal.valueOf(0.5)))
                    .build();

            MembershipSubscriptionTypeDto yearSubscription = MembershipSubscriptionTypeDto.builder()
                    .membershipSubscriptionType(MembershipSubscriptionType.YEAR.getMembershipSubscriptionType())
                    .price(MembershipSubscriptionType.YEAR.getPrice())
                    .discountRate(50)
                    .discountedPrice(MembershipSubscriptionType.YEAR.getPrice().multiply(BigDecimal.valueOf(0.5)))
                    .build();

            membershipSubscriptionTypeDtos.add(monthSubscription);
            membershipSubscriptionTypeDtos.add(yearSubscription);

            return membershipSubscriptionTypeDtos;
        }

        MembershipSubscriptionTypeDto monthSubscription =  new MembershipSubscriptionTypeDto(MembershipSubscriptionType.MONTH);

        MembershipSubscriptionTypeDto yearSubscription =  new MembershipSubscriptionTypeDto(MembershipSubscriptionType.YEAR);

        membershipSubscriptionTypeDtos.add(monthSubscription);
        membershipSubscriptionTypeDtos.add(yearSubscription);

        return membershipSubscriptionTypeDtos;
    }
}
