package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.order.OrderUtil;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.order.entity.OrderStatus;
import co.dalicious.domain.order.entity.OrderType;
import co.dalicious.domain.order.repository.OrderMembershipRepository;
import co.dalicious.domain.order.repository.OrderRepository;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.service.MembershipService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    private final CommonService commonService;
    private final MembershipRepository membershipRepository;
    private final OrderMembershipRepository orderMembershipRepository;
    private final OrderRepository orderRepository;

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
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .orderType(OrderType.MEMBERSHIP)
                .code(code)
                .build();
        orderRepository.save(order);

        MembershipSubscriptionType membershipSubscriptionType = MembershipSubscriptionType.valueOf(subscriptionType);
        OrderMembership orderMembership = OrderMembership.builder()
                .membershipSubscriptionType(membershipSubscriptionType)
                .discount_rate(membershipSubscriptionType.getDiscountRate())
                .order(order)
                .build();
        orderMembershipRepository.save(orderMembership);

        // 결제 진행. 실패시 오류 날림
        BigDecimal price = BigDecimal.valueOf(membershipSubscriptionType.getDiscountedPrice());
        int statusCode = requestPayment(code, price, 200);
        // 결제 실패시 orderMembership의 상태값을 결제 실패 상태(3)로 변경
        if(statusCode != 200) {
            order.updateStatus(OrderStatus.FAILED);
            throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        }
        // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
        else {
            order.updateTotalPrice(price);
            order.updateStatus(OrderStatus.COMPLETED);
        }

        // 멤버십 등록
        Membership membership = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        // 이 사람이 기존에 멤버십을 가입했는 지 확인
        if(user.getIsMembership()) {
            List<Membership> memberships = membershipRepository.findByUserOrderByCreatedDateTimeDesc(user);
            Membership recentMembership = memberships.get(0);
            LocalDate currantEndDate = recentMembership.getEndDate();
            startDate = currantEndDate.plusDays(1);
            endDate = startDate.plusMonths(1);

            membership = Membership.builder()
            .auto_payment(true)
            .startDate(startDate)
            .endDate(endDate)
            .build();
        }



    }

    @Override
    public void terminateMembership() {

    }

    @Override
    public void refundMembership() {

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
