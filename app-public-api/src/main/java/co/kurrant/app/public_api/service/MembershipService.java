package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.OrderMembershipReqDto;
import co.dalicious.domain.order.dto.OrderMembershipResDto;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.OrderItemMembership;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.user.dto.DailyFoodMembershipDiscountDto;
import co.dalicious.domain.user.dto.MembershipBenefitDto;
import co.dalicious.domain.user.dto.MembershipSubscriptionTypeDto;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.dto.MembershipDto;
import co.kurrant.app.public_api.model.SecurityUser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface MembershipService {
    // 유저의 전체 멤버십 내용을 조회한다.
    List<MembershipDto> retrieveMembership(SecurityUser securityUser);

    // 유저가 멤버십에 가입한다
    void joinMembership(SecurityUser securityUser, OrderMembershipReqDto orderMembershipReqDto);

    // 멤버십 결제 정보를 가져온다.
    OrderMembershipResDto getOrderMembership(SecurityUser securityUser, Integer subscriptionType);

    // 유저가 멤버십을 환불한다
    void refundMembership(User user, Order order, Membership membership, OrderMembership orderMembership) throws IOException, ParseException;
    void refundMembershipNice(User user, Order order, Membership membership, OrderMembership orderMembership) throws IOException, ParseException;

    // 유저가 멤버십을 해지 또는 환불한다
    void unsubscribeMembership(SecurityUser securityUser) throws IOException, ParseException;

    // 멤버십 혜택 금액
    MembershipBenefitDto getMembershipBenefit(SecurityUser securityUser);

    // 유저가 멤버십을 이용하는 동안 받았던 정기식사 혜택 금액을 조회한다.
    DailyFoodMembershipDiscountDto getDailyFoodPriceBenefits(List<OrderItemDailyFood> orderItemDailyFoods);

    BigDecimal getRefundableMembershipPrice(List<OrderItemDailyFood> orderItemDailyFoods, OrderItemMembership orderItemMembership);

    void joinMembershipNice(SecurityUser securityUser, OrderMembershipReqDto orderMembershipReqDto) throws IOException, ParseException;

    void unsubscribeMembershipNice(SecurityUser securityUser) throws IOException, ParseException;
    List<MembershipSubscriptionTypeDto> getMembershipSubscriptionInfo(SecurityUser securityUser);
}
