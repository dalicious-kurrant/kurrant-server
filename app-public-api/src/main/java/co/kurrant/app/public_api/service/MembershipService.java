package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.OrderMembershipReqDto;
import co.dalicious.domain.order.dto.OrderMembershipResDto;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.dto.MembershipDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.util.List;

public interface MembershipService {
    // 유저의 전체 멤버십 내용을 조회한다.
    List<MembershipDto> retrieveMembership(SecurityUser securityUser);

    // 유저가 멤버십을 자동 결제할 시 사용할 결제 수단을 정한다.
    void saveMembershipAutoPayment(SecurityUser securityUser);
    // 유저가 멤버십에 가입한다
    void joinMembership(SecurityUser securityUser, OrderMembershipReqDto orderMembershipReqDto);
    // 멤버십 결제 정보를 가져온다.
    OrderMembershipResDto getOrderMembership(SecurityUser securityUser, Integer subscriptionType);
    // 유저가 멤버십을 환불한다
    void refundMembership(User user, Order order, Membership membership);
    // 유저가 멤버십을 해지 또는 환불한다
    void unsubscribeMembership(SecurityUser securityUser);
    // 유저가 멤버십을 이용하는 동안 받았던 정기식사 혜택 금액을 조회한다.
    void getDailyFoodPriceBenefits(User user);
    // 유저가 멤버십을 이용하는 동안 받았던 마켓 할인 금액을 조회한다.
    void getMarketPriceBenefits(User user);
    // 유저가 멤버십을 이용하는 동안 받았던 정기 식사 리뷰 포인트 적립을 조회한다.
    void getDailyFoodPointBenefits(User user);
    // 유저가 멤버십을 이용하는 동안 받았던 마켓 구매/리뷰 포인트 적립을 조회한다.
    void getMarketPointBenefits(User user);
}
