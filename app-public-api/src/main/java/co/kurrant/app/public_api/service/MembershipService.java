package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.MembershipDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface MembershipService {
    // 유저의 전체 멤버십 내용을 조회한다.
    List<MembershipDto> retrieveMembership(HttpServletRequest httpServletRequest);
    // 유저가 멤버십에 가입한다
    void joinMembership(HttpServletRequest httpServletRequest, String subscriptionType);
    // 유저가 멤버십을 환불한다
    void refundMembership(User user, Order order, Membership membership);
    // 유저가 멤버십을 해지 또는 환불한다
    void unsubscribeMembership(HttpServletRequest httpServletRequest);
    // 유저가 멤버십을 이용하는 동안 받았던 정기식사 혜택 금액을 조회한다.
    void getDailyFoodPriceBenefits(HttpServletRequest httpServletRequest);
    // 유저가 멤버십을 이용하는 동안 받았던 마켓 할인 금액을 조회한다.
    void getMarketPriceBenefits(HttpServletRequest httpServletRequest);
    // 유저가 멤버십을 이용하는 동안 받았던 정기 식사 리뷰 포인트 적립을 조회한다.
    void getDailyFoodPointBenefits(HttpServletRequest httpServletRequest);
    // 유저가 멤버십을 이용하는 동안 받았던 마켓 구매/리뷰 포인트 적립을 조회한다.
    void getMarketPointBenefits(HttpServletRequest httpServletRequest);
    // 유저가 멤버십을 자동 결제할 시 사용할 결제 수단을 정한다.
    void saveMembershipAutoPayment(HttpServletRequest httpServletRequest);
}
