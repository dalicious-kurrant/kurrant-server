package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.order.dto.OrderMembershipReqDto;
import co.dalicious.domain.order.dto.OrderMembershipResDto;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.mapper.OrderUserInfoMapper;
import co.dalicious.domain.order.repository.OrderMembershipRepository;
import co.dalicious.domain.order.repository.OrderRepository;
import co.dalicious.domain.order.service.DiscountPolicy;
import co.dalicious.domain.order.service.DiscountPolicyImpl;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.user.dto.PeriodDto;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.mapper.MembershipResMapper;
import co.dalicious.domain.user.repository.MembershipDiscountPolicyRepository;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.system.util.enums.DiscountType;
import co.dalicious.domain.user.dto.MembershipDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.repository.QMembershipRepository;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.service.MembershipService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    private final UserUtil userUtil;
    private final MembershipRepository membershipRepository;
    private final QMembershipRepository QmembershipRepository;
    private final OrderMembershipRepository orderMembershipRepository;
    private final OrderRepository orderRepository;
    private final BigDecimal DELIVERY_FEE = new BigDecimal("2200.0");
    private final BigDecimal REFUND_YEARLY_MEMBERSHIP_PER_MONTH = MembershipSubscriptionType.YEAR.getPrice().multiply(BigDecimal.valueOf((100 - MembershipSubscriptionType.YEAR.getDiscountRate()) * 0.01)).divide(BigDecimal.valueOf(12));
    private final BigDecimal DISCOUNT_YEARLY_MEMBERSHIP_PER_MONTH = MembershipSubscriptionType.MONTH.getPrice().subtract(REFUND_YEARLY_MEMBERSHIP_PER_MONTH);
    private final MembershipDiscountPolicyRepository membershipDiscountPolicyRepository;
    private final DiscountPolicy discountPolicy;
    private final OrderUserInfoMapper orderUserInfoMapper;
    private final MembershipResMapper membershipResMapper;

    @Override
    @Transactional
    public void joinMembership(SecurityUser securityUser, OrderMembershipReqDto orderMembershipReqDto) {
        User user = userUtil.getUser(securityUser);
        // TODO: 현재, 멤버십을 중복 가입할 수 있게 만들어졌지만, 수정 필요
        // 금액 정보가 일치하는지 확인
        MembershipSubscriptionType membershipSubscriptionType = MembershipSubscriptionType.ofCode(orderMembershipReqDto.getSubscriptionType());
        // 1. 기본 가격이 일치하는지 확인
        BigDecimal defaultPrice = membershipSubscriptionType.getPrice();
        if (!orderMembershipReqDto.getDefaultPrice().equals(defaultPrice)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }
        // 2. 연간 구독 할인 가격이 일치하는지 확인
        BigDecimal yearDescriptionDiscountPrice = DiscountPolicyImpl.discountedPriceByRate(membershipSubscriptionType.getPrice(), membershipSubscriptionType.getDiscountRate());
        if (!orderMembershipReqDto.getYearDescriptionDiscountPrice().equals(yearDescriptionDiscountPrice)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }
        // 3. 기간 할인 가격이 일치하는지 확인
        // TODO: 기간할인 추가시 기간할인 조회 로직 추가 필요
        BigDecimal periodDiscountPrice = BigDecimal.ZERO;
        if (!orderMembershipReqDto.getPeriodDiscountPrice().equals(periodDiscountPrice)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }

        // 이 사람이 기존에 멤버십을 가입했는 지 확인
        PeriodDto periodDto = null;
        if (user.getIsMembership()) {
            List<Membership> memberships = membershipRepository.findAllByUserOrderByEndDateDesc(user);
            if (memberships != null && !memberships.isEmpty()) {
                Membership recentMembership = memberships.get(0);
                LocalDate currantEndDate = recentMembership.getEndDate();
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
        Membership membership = Membership.builder()
                .autoPayment(true)
                .membershipStatus(MembershipStatus.PROCESSING)
                .membershipSubscriptionType(membershipSubscriptionType)
                .build();
        membership.setUser(user);
        membership.setDate(periodDto);
        membershipRepository.save(membership);

        // 연간 구독 구매자라면, 할인 정책 저장.
        MembershipDiscountPolicy yearDescriptionDiscountPolicy = MembershipDiscountPolicy.builder()
                .membership(membership)
                .discountRate(membershipSubscriptionType.getDiscountRate())
                .discountType(DiscountType.YEAR_DESCRIPTION_DISCOUNT)
                .build();
        membershipDiscountPolicyRepository.save(yearDescriptionDiscountPolicy);

        /* TODO: 할인 혜택을 가지고 있는 유저인지 확인 후 할인 정책 저장.
        MembershipDiscountPolicy periodDiscountPolicy = MembershipDiscountPolicy.builder()
                .membership(membership)
                .discountRate(membershipSubscriptionType.getDiscountRate())
                .discountType(DiscountType.PERIOD_DISCOUNT)
                .build();
        membershipDiscountPolicyRepository.save(periodDiscountPolicy);
         */

        // 멤버십 결제 요청
        String code = OrderUtil.generateOrderCode(OrderType.MEMBERSHIP, user.getId());
        Order order = Order.builder()
                .code(code)
                .orderType(OrderType.MEMBERSHIP)
                .paymentType(PaymentType.ofCode(orderMembershipReqDto.getPaymentType()))
                .build();
        // 유저 정보 저장
        OrderUserInfoDto orderUserInfoDto = orderUserInfoMapper.toDto(user);
        order.updateOrderUserInfo(orderUserInfoDto);
        orderRepository.save(order);

        // 멤버십 결제 내역 등록(진행중 상태)
        OrderMembership orderMembership = OrderMembership.builder()
                .order(order)
                .orderStatus(OrderStatus.PROCESSING)
                .membership(membership)
                .membershipSubscriptionType(membership.getMembershipSubscriptionType().getMembershipSubscriptionType())
                .price(membership.getMembershipSubscriptionType().getPrice())
                .build();
        orderMembershipRepository.save(orderMembership);

        OrderStatus orderStatus = null;

        // 결제 진행. 실패시 오류 날림
        BigDecimal price = discountPolicy.orderItemTotalPrice(orderMembership);

        try {
            int statusCode = requestPayment(code, price, 200);
            // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
            if (statusCode == 200) {
                order.updateTotalPrice(price);
                orderMembership.updateOrderStatus(OrderStatus.COMPLETED);
            }
            // 결제 실패시 orderMembership의 상태값을 결제 실패 상태(3)로 변경
            else {
                orderMembership.updateOrderStatus(OrderStatus.FAILED);
                throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
            }
        } catch (ApiException e) {
            orderMembership.updateOrderStatus(OrderStatus.FAILED);
            throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        }
        user.changeMembershipStatus(true);
    }

    @Override
    public OrderMembershipResDto getOrderMembership(SecurityUser securityUser, Integer subscriptionType) {
        MembershipSubscriptionType membershipSubscriptionType = MembershipSubscriptionType.ofCode(subscriptionType);
        BigDecimal defaultPrice = membershipSubscriptionType.getPrice();
        // 1. 월간 구독인지 연간 구독인지 확인 후 할인 금액 도출
        Integer yearSubscriptionDiscountRate = 0;
        if (membershipSubscriptionType == MembershipSubscriptionType.YEAR) {
            yearSubscriptionDiscountRate = membershipSubscriptionType.getDiscountRate();
        }
        BigDecimal yearSubscriptionDiscountPrice = DiscountPolicyImpl.discountedPriceByRate(defaultPrice, yearSubscriptionDiscountRate);
        // 2. 기간 할인이 적용되는 유저인지 확인
        User user = userUtil.getUser(securityUser);
        Integer periodDiscountRate = 0;
        BigDecimal periodDiscountPrice = DiscountPolicyImpl.discountedPriceByRate(defaultPrice.subtract(yearSubscriptionDiscountPrice), periodDiscountRate);
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
    public void refundMembership(User user, Order order, Membership membership) {
        // TODO: 연간구독 해지시, membership endDate update.
        BigDecimal price = BigDecimal.ZERO;

        List<Order> dailyFoodOrders = orderRepository.findAllByUserAndOrderType(user, OrderType.DAILYFOOD);
        // 정기 식사 배송비 계산
        price = price.add(DELIVERY_FEE.multiply(BigDecimal.valueOf(dailyFoodOrders.size())));

        // 정기 식사 할인율 계산

        // 마켓 상품 할인 계산
        List<Order> productDiscountedPrice = orderRepository.findAllByUserAndOrderType(user, OrderType.PRODUCT);

        // 멤버십 결제금액 가져오기
        BigDecimal paidPrice = order.getTotalPrice();

        // 멤버십 결제 가져오기
        List<OrderItem> orderItems = order.getOrderItems();
        OrderMembership orderMembership = null;
        for (OrderItem orderItem : orderItems) {
            if(orderItem.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                orderMembership = (OrderMembership) orderItem;
            }
        }
        if(orderMembership == null) {
            throw new ApiException(ExceptionEnum.ORDER_ITEM_NOT_FOUND);
        }

        // 주문 상태 변경
        orderMembership.updateOrderStatus(OrderStatus.AUTO_REFUND);
        // TODO: 결제 취소.
        OrderUtil.refundOrderMembership(user, orderMembership, membership);
        // TODO: 지불한 금액이 사용한 금액보다 크다면 환불 필요.
        if (paidPrice.compareTo(price) > 0) {
            OrderMembership orderMembership1 = OrderMembership.builder()
                    .orderStatus(OrderStatus.PRICE_DEDUCTION)
                    .order(order)
                    .membership(membership)
                    .membershipSubscriptionType(membership.getMembershipSubscriptionType().getMembershipSubscriptionType())
                    .price(paidPrice.subtract(price))
                    .build();
            orderMembershipRepository.save(orderMembership1);
        }
    }

    @Override
    @Transactional
    public void unsubscribeMembership(User user) {
        // 멤버십 사용중인 유저인지 가져오기
        if (!user.getIsMembership()) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }
        // 현재 사용중인 멤버십 가져오기
        Optional<Membership> userCurrentMembership = QmembershipRepository.findUserCurrentMembership(user, LocalDate.now());

        if (userCurrentMembership.isEmpty()) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }
        // 멤버십 주문 가져오기
        OrderMembership orderMembership = orderMembershipRepository.findOneByMembership(userCurrentMembership.get()).orElseThrow(
                () -> new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND)
        );
        // 주문 상태가 "완료됨"이 아닌 경우 제외
        if (!orderMembership.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }

        // 멤버십 주문 내역 가져오기


        // 사용한 날짜 계산하기
        int membershipUsingDays = userCurrentMembership.get().getStartDate().until(LocalDate.now()).getDays();

        // 7일 이하일 경우 멤버십 환불
        if (membershipUsingDays <= 7) {
            refundMembership(user, orderMembership.getOrder(), userCurrentMembership.get());
            return;
        }
        userCurrentMembership.get().changeAutoPaymentStatus(false);
    }

    @Override
    @Transactional
    public void getDailyFoodPriceBenefits(User user) {

    }

    @Override
    @Transactional
    public void getMarketPriceBenefits(User user) {

    }

    @Override
    @Transactional
    public void getDailyFoodPointBenefits(User user) {

    }

    @Override
    public void getMarketPointBenefits(User user) {

    }


    // TODO: 결제 모듈 구현시 수정
    @Transactional
    public int requestPayment(String paymentCode, BigDecimal price, int statusCode) {
        return statusCode;
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
                        builder.in(root.get("membershipStatus")).value(1).value(2)
                );

        // create a Sort object to specify the ordering of the query
        Sort sort = Sort.by(Sort.Direction.ASC, "endDate");

        // execute the query using the repository
        List<OrderMembership> orderMemberships = orderMembershipRepository.findAll(specification, sort);

        List<MembershipDto> membershipDtos = new ArrayList<>();

        for (OrderMembership orderMembership : orderMemberships) {
            // membershipDto를 생성한다.
            MembershipDto membershipDto = membershipResMapper.toDto(orderMembership.getMembership());
            membershipDto.setPrice(orderMembership.getPrice());
            membershipDto.setDiscountedPrice(orderMembership.getOrder().getTotalPrice());
            membershipDtos.add(membershipDto);
        }
        // 멤버십 종료 날짜의 내림차순으로 멤버십 이용내역을 반환한다.
        Collections.reverse(membershipDtos);
        return membershipDtos;
    }

    @Override
    @Transactional
    public void saveMembershipAutoPayment(SecurityUser securityUser) {

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
}
