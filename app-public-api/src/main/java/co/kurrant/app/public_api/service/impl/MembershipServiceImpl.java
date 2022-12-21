package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.order.OrderUtil;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.order.entity.OrderStatus;
import co.dalicious.domain.order.entity.OrderType;
import co.dalicious.domain.order.repository.OrderMembershipRepository;
import co.dalicious.domain.order.repository.OrderRepository;
import co.dalicious.domain.user.dto.PeriodDto;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.PaymentType;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.util.MembershipUtil;
import co.kurrant.app.public_api.dto.user.MembershipDto;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.service.MembershipService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    private final CommonService commonService;
    private final MembershipRepository membershipRepository;
    private final OrderMembershipRepository orderMembershipRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<MembershipDto> retrieveMembership(HttpServletRequest httpServletRequest) {
        User user = commonService.getUser(httpServletRequest);
        // 멤버십 종료 날짜의 오름차순으로 멤버십 정보를 조회한다.
        List<Membership> memberships = membershipRepository.findByUserOrderByEndDateAsc(user);

        List<MembershipDto> membershipDtos = new ArrayList<>();
        int membershipUsingPeriod = 0;
        for (Membership membership : memberships) {
            // 총 이용 기간을 계산한다.
            membershipUsingPeriod += MembershipUtil.getPeriodWithStartAndEndDate(membership.getStartDate(), membership.getEndDate());
            // membershipDto를 생성한다.
            MembershipDto membershipDto = MembershipDto.builder()
                    .membershipUsingPeriod(membershipUsingPeriod)
                    .price(BigDecimal.valueOf(membership.getMembershipSubscriptionType().getPrice()))
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
    public void joinMembership(HttpServletRequest httpServletRequest, String subscriptionType) {
        // 유저 가져오기
        User user = commonService.getUser(httpServletRequest);

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
            List<Membership> memberships = membershipRepository.findByUserOrderByEndDateDesc(user);
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
        BigDecimal price = BigDecimal.ZERO;

        List<Order> dailyFoodOrders = orderRepository.findByUserAndOrderType(user, OrderType.DAILYFOOD);
        // 정기 식사 배송비 계산
        BigDecimal deliveryFee = new BigDecimal("2200.0");
        price = price.add(deliveryFee.multiply(BigDecimal.valueOf(dailyFoodOrders.size())));

        // 정기 식사 할인율 계산

        // 마켓 상품 할인 계산
        List<Order> productDiscountedPrice = orderRepository.findByUserAndOrderType(user, OrderType.PRODUCT);

        // 결제금액 가져오기
        BigDecimal paidPrice = BigDecimal.valueOf(membership.getMembershipSubscriptionType().getDiscountedPrice());

        // 주문 상태 변경
        order.updateStatus(OrderStatus.REFUNDED);
        // TODO: 결제 취소.

        // TODO: 지불한 금액이 사용한 금액보다 크다면 환불 필요.
        if(paidPrice.compareTo(price) > 0) {
            BigDecimal refundPrice = paidPrice.subtract(price);
            // TODO: Order / OrderMembership 생성
        }
    }

    @Override
    @Transactional
    public void unsubscribeMembership(HttpServletRequest httpServletRequest) {
        // 유저 가져오기
        User user = commonService.getUser(httpServletRequest);

        // 현재 사용중인 멤버십 가져오기
        List<Membership> memberships =
        membershipRepository.findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(user, LocalDate.now(), LocalDate.now());

        if (memberships.isEmpty()) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }

        Order currantMembershipOrder = null;

        for(Membership membership : memberships) {
            Order order = orderMembershipRepository.findByMembership(membership).getOrder();
            if(!order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                memberships.remove(membership);
            }
            currantMembershipOrder = order;
        }
        if(memberships.size() > 1) {
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
        if(membershipUsingDays <= 7){
            refundMembership(user, currantMembershipOrder, currantMembership);
        }

        currantMembership.changeAutoPaymentStatus(false);
        user.changeMembershipStatus(false);
    }

    @Override
    public void getDailyFoodPriceBenefits(HttpServletRequest httpServletRequest) {

    }

    @Override
    public void getMarketPriceBenefits(HttpServletRequest httpServletRequest) {

    }

    @Override
    public void getDailyFoodPointBenefits(HttpServletRequest httpServletRequest) {

    }

    @Override
    public void getMarketPointBenefits(HttpServletRequest httpServletRequest) {

    }

    @Override
    public void saveMembershipAutoPayment(HttpServletRequest httpServletRequest) {

    }

    // 결제 로직 구현. 검증
    public int requestPayment(String paymentCode, BigDecimal price, int statusCode) {
        return statusCode;
    }
}
