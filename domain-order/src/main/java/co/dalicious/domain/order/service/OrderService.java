package co.dalicious.domain.order.service;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface OrderService {
    // 유저가 멤버십에 가입한다
    void joinMembership(User user, String subscriptionType);
    // 유저가 멤버십을 환불한다
    void refundMembership(User user, Order order, Membership membership);
    // 유저가 멤버십을 해지 또는 환불한다
    void unsubscribeMembership(User user);
    // 유저가 멤버십을 이용하는 동안 받았던 정기식사 혜택 금액을 조회한다.
    void getDailyFoodPriceBenefits(User user);
    // 유저가 멤버십을 이용하는 동안 받았던 마켓 할인 금액을 조회한다.
    void getMarketPriceBenefits(User user);
    // 유저가 멤버십을 이용하는 동안 받았던 정기 식사 리뷰 포인트 적립을 조회한다.
    void getDailyFoodPointBenefits(User user);
    // 유저가 멤버십을 이용하는 동안 받았던 마켓 구매/리뷰 포인트 적립을 조회한다.
    void getMarketPointBenefits(User user);
}
