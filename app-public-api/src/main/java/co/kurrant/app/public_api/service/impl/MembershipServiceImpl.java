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
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.util.MembershipUtil;
import co.kurrant.app.public_api.dto.user.MembershipDto;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.service.MembershipService;
import co.kurrant.app.public_api.service.impl.mapper.MembershipMapper;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
        List<Membership> memberships = membershipRepository.findByUserOrderByEndDateDesc(user);
        List<MembershipDto> membershipDtos = new ArrayList<>();
        for(Membership membership : memberships) {
            membershipDtos.add(MembershipMapper.INSTANCE.toDto(membership));
        }
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
                periodDto = MembershipUtil.getStartAndEndDateMonthly(currantEndDate);
            }
        } else {
            LocalDate now = LocalDate.now();
            periodDto = MembershipUtil.getStartAndEndDateMonthly(now);
        }

        assert periodDto != null;

        // 멤버십 등록
        membership = Membership.builder()
                .autoPayment(true)
                .startDate(periodDto.getStartDate())
                .endDate(periodDto.getEndDate())
                .build();

        membershipRepository.save(membership);
        user.changeMembershipStatus(true);
    }

    @Override
    public void terminateMembership(HttpServletRequest httpServletRequest) {
        // 유저 가져오기
        User user = commonService.getUser(httpServletRequest);

        // 현재 사용중인 멤버십 가져오기
        Membership currantMembership = membershipRepository.findByUserAndStartDateBeforeAndEndDateAfter(user, LocalDate.now(), LocalDate.now());

        // 현재 이용중인 멤버십의 자동 결제 여부를 확인 후 변경



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
