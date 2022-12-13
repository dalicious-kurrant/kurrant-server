package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.order.entity.OrderStatus;
import co.dalicious.domain.order.repository.OrderMembershipRepository;
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
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    private final CommonService commonService;
    private final MembershipRepository membershipRepository;
    private final OrderMembershipRepository orderMembershipRepository;

    @Override
    @Transactional
    public void joinMembership(HttpServletRequest httpServletRequest, String subscriptionType) {
        // 유저 가져오기
        User user = commonService.getUser(httpServletRequest);
        // 이 사람이 기존에 멤버십을 가입했는 지 확인
        if(user.getIsMembership()) {
            List<Membership> memberships = membershipRepository.findByUserOrderByCreatedDateTimeDesc(user);
            Membership recentMembership = memberships.get(0);
            LocalDate currantEndDate = recentMembership.getEndDate();
        }
        // 멤버십 결제 요청(진행중 상태)
        MembershipSubscriptionType membershipSubscriptionType = MembershipSubscriptionType.valueOf(subscriptionType);
        OrderMembership orderMembership = OrderMembership.builder()
                .membershipSubscriptionType(membershipSubscriptionType)
                .build();
        orderMembershipRepository.save(orderMembership);

//        /* 결제. 실패시 오류 날림
        double price = membershipSubscriptionType.getDiscountedPrice() ;
        int statusCode = requestPayment()
        // 결제 실패시 orderMembership의 상태값을 결제 실패 상태(3)로 변경
        if(statusCode != 200) {
            orderMembership.updateStatus(OrderStatus.FAILED);
            throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        }
        // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
        else {

        }
//        */



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
    public int requestPayment(String paymentCode, int price, int statusCode) {
        return statusCode;
    }
}
