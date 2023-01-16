package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.repository.OrderMembershipRepository;
import co.dalicious.domain.order.repository.OrderRepository;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.user.dto.PeriodDto;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.util.MembershipUtil;
import co.kurrant.app.public_api.dto.user.MembershipDto;
import co.kurrant.app.public_api.model.SecurityUser;
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

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    private final UserUtil userUtil;
    private final MembershipRepository membershipRepository;
    private final OrderMembershipRepository orderMembershipRepository;
    private final OrderRepository orderRepository;
    private final BigDecimal DELIVERY_FEE = new BigDecimal("2200.0");
    private final BigDecimal REFUND_YEARLY_MEMBERSHIP_PER_MONTH = BigDecimal.valueOf(MembershipSubscriptionType.YEAR.getDiscountedPrice() / 12);
    private final BigDecimal DISCOUNT_YEARLY_MEMBERSHIP_PER_MONTH = BigDecimal.valueOf(MembershipSubscriptionType.MONTH.getPrice()).subtract(REFUND_YEARLY_MEMBERSHIP_PER_MONTH);

    @Override
    @Transactional
    public void joinMembership(User user, String subscriptionType) {
        // TODO: 현재, 멤버십을 중복 가입할 수 있게 만들어졌지만, 수정 필요

        // 멤버십 구매 할인 혜택을 가지고 있는 유저인지 검증.

        // 멤버십 결제 요청(진행중 상태)
        String code = OrderUtil.generateOrderCode(OrderType.MEMBERSHIP, user.getId());
        Order order = Order.builder()
                .user(user)
                .paymentType(PaymentType.BANK_TRANSFER)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .orderType(OrderType.MEMBERSHIP)
                .code(code)
                .build();
        orderRepository.save(order);

        MembershipSubscriptionType membershipSubscriptionType = MembershipSubscriptionType.valueOf(subscriptionType);

        // 결제 진행. 실패시 오류 날림
        BigDecimal price = BigDecimal.valueOf(membershipSubscriptionType.getDiscountedPrice());

        try {
            int statusCode = requestPayment(code, price, 200);
            // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
            if (statusCode == 200) {
                order.updateTotalPrice(price);
                order.updateStatus(OrderStatus.COMPLETED);
            }
            // 결제 실패시 orderMembership의 상태값을 결제 실패 상태(3)로 변경
            else {
                order.updateStatus(OrderStatus.FAILED);
                throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
            }
        } catch (ApiException e) {
            order.updateStatus(OrderStatus.FAILED);
            throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        }

        // 이 사람이 기존에 멤버십을 가입했는 지 확인
        Membership membership = null;
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
        membership = Membership.builder()
                .autoPayment(true)
                .membershipSubscriptionType(membershipSubscriptionType)
                .user(user)
                .startDate(periodDto.getStartDate())
                .endDate(periodDto.getEndDate())
                .membershipStatus(MembershipStatus.PROCESSING)
                .build();
        membershipRepository.save(membership);

        // 멤버십 결제 내역 등록
        OrderMembership orderMembership = OrderMembership.builder()
                .order(order)
                .membership(membership)
                .build();
        orderMembershipRepository.save(orderMembership);

        user.changeMembershipStatus(true);
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
        BigDecimal paidPrice = BigDecimal.valueOf(membership.getMembershipSubscriptionType().getDiscountedPrice());

        // 월간 구독권인지, 연간 구독권인지 구분 필요
        int period = 0;
        if (membership.getMembershipSubscriptionType().equals(MembershipSubscriptionType.YEAR)) {
            period = MembershipUtil.getPeriodWithStartAndEndDate(membership.getStartDate(), LocalDate.now());
            // Day가 일치하지 않으면 1을 추가한다. -> 2022-12-22과 2023-01-21은 0의 결과 값이 나오므로.
            if (membership.getStartDate().getDayOfMonth() > LocalDate.now().getDayOfMonth()) {
                period++;
            }
            paidPrice = paidPrice.subtract(DISCOUNT_YEARLY_MEMBERSHIP_PER_MONTH.multiply(BigDecimal.valueOf(period)));
        }

        // 주문 상태 변경
        order.updateStatus(OrderStatus.AUTO_REFUND);
        // TODO: 결제 취소.
        OrderUtil.refundOrderMembership(user, order, membership);
        // TODO: 지불한 금액이 사용한 금액보다 크다면 환불 필요.
        if (paidPrice.compareTo(price) > 0) {
            BigDecimal refundPrice = paidPrice.subtract(price);
            // TODO: Order / OrderMembership 생성
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
        List<Membership> memberships =
                membershipRepository.findAllByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(user, LocalDate.now(), LocalDate.now());

        if (memberships.isEmpty()) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }

        Order currantMembershipOrder = null;
        // 주문 상태가 "완료됨"이 아닌 경우, 주문 조회 목록에서 삭제
        for (Membership membership : memberships) {
            Order order = orderMembershipRepository.findOneByMembership(membership).orElseThrow(
                    () -> new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND)
            ).getOrder();
            if (!order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                memberships.remove(membership);
            }
            currantMembershipOrder = order;
        }
        if (memberships.size() > 1) {
            throw new ApiException(ExceptionEnum.DUPLICATED_MEMBERSHIP);
        }

        Membership currantMembership = memberships.get(0);
        /* 현재 멤버십이 존재하지 않는다면 에러 발생.
        if(!MembershipUtil.isValidMembership(currantMembership)) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        } */

        // 멤버십 주문 내역 가져오기


        // 사용한 날짜 계산하기
        int membershipUsingDays = currantMembership.getStartDate().until(LocalDate.now()).getDays();

        // 7일 이하일 경우 멤버십 환불
        if (membershipUsingDays <= 7) {
            refundMembership(user, currantMembershipOrder, currantMembership);
            return;
        }

        currantMembership.changeAutoPaymentStatus(false);
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
        List<Membership> memberships = membershipRepository.findAll(specification, sort);

        List<MembershipDto> membershipDtos = new ArrayList<>();
        int membershipUsingPeriod = 0;
        for (Membership membership : memberships) {
            // 총 이용 기간을 계산한다.
            membershipUsingPeriod += MembershipUtil.getPeriodWithStartAndEndDate(membership.getStartDate(), membership.getEndDate());
            // membershipDto를 생성한다.
            MembershipDto membershipDto = MembershipDto.builder()
                    .id(membership.getId())
                    .membershipSubscriptionType(membership.getMembershipSubscriptionType().getMembershipSubscriptionType())
                    .membershipUsingPeriod(membershipUsingPeriod)
                    .price(BigDecimal.valueOf(membership.getMembershipSubscriptionType().getPrice()))
                    .discountedPrice(BigDecimal.valueOf(membership.getMembershipSubscriptionType().getDiscountedPrice()))
                    .startDate(membership.getStartDate())
                    .endDate(membership.getEndDate())
                    .build();
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
}
