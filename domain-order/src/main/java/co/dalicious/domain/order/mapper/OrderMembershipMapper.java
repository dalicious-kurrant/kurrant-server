package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.MembershipSupportPrice;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItemMembership;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.MembershipDiscountPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.enums.DiscountType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", imports = OrderUtil.class)
public interface OrderMembershipMapper {
    @Mapping(target = "orderType", constant = "MEMBERSHIP")
    @Mapping(target = "code", expression = "java(OrderUtil.generateOrderCode(OrderType.MEMBERSHIP, orderUserInfoDto.getUser().getId()))")
    @Mapping(target = "defaultPrice", expression = "java(membershipSubscriptionType.getPrice())")
    @Mapping(source = "point", target = "point")
    @Mapping(source = "totalPrice", target = "totalPrice")
    @Mapping(source = "creditCardInfo", target = "creditCardInfo")
    @Mapping(source = "paymentType", target = "paymentType")
    @Mapping(source = "orderUserInfoDto.user", target = "user")
    @Mapping(source = "orderUserInfoDto.address", target = "address")
    @Mapping(source = "membership", target = "membership")
    OrderMembership toOrderMembership(OrderUserInfoDto orderUserInfoDto, CreditCardInfo creditCardInfo, MembershipSubscriptionType membershipSubscriptionType, BigDecimal point, BigDecimal totalPrice, PaymentType paymentType, Membership membership);

    @Mapping(target = "membershipStatus", constant = "PROCESSING")
    @Mapping(source = "membershipSubscriptionType", target = "membershipSubscriptionType")
    @Mapping(source = "periodDto.startDate", target = "startDate")
    @Mapping(source = "periodDto.endDate", target = "endDate")
    @Mapping(target = "autoPayment", constant = "true")
    @Mapping(source = "user", target = "user")
    Membership toMembership(MembershipSubscriptionType membershipSubscriptionType, User user, PeriodDto periodDto);

    @Mapping(source = "membership", target = "membership")
    @Mapping(source = "membership.membershipSubscriptionType.membershipSubscriptionType", target = "membershipSubscriptionType")
    @Mapping(source = "membership.membershipSubscriptionType.price", target = "price")
    @Mapping(target = "discountPrice", expression = "java(order.getDiscountPrice())")
    @Mapping(target = "orderStatus", constant = "COMPLETED")
    OrderItemMembership toOrderItemMembership(Order order, Membership membership);

    @Mapping(source = "membership", target = "membership")
    @Mapping(source = "membership.membershipSubscriptionType.membershipSubscriptionType", target = "membershipSubscriptionType")
    @Mapping(source = "membership.membershipSubscriptionType.price", target = "price")
    @Mapping(target = "discountPrice", expression = "java(order.getDiscountPrice())")
    @Mapping(source = "periodDiscountedRate", target = "periodDiscountedRate")
    @Mapping(target = "orderStatus", constant = "COMPLETED")
    OrderItemMembership toOrderItemMembership(Order order, Membership membership, Integer periodDiscountedRate);

    // TODO: 기간 할인시, 변경 필요
    @Mapping(source = "discountType", target = "discountType")
    @Mapping(source = "membership", target = "membership")
    @Mapping(source = "discountRate", target = "discountRate")
    MembershipDiscountPolicy toMembershipDiscountPolicy(Membership membership, DiscountType discountType, Integer discountRate);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "group", target = "group")
    @Mapping(source = "orderItemMembership", target = "usingSupportPrice" ,qualifiedByName = "getUsingSupportPrice")
    @Mapping(target = "monetaryStatus", constant = "DEDUCTION")
    @Mapping(source = "orderItemMembership", target = "orderItemMembership")
    MembershipSupportPrice toMembershipSupportPrice(User user, Group group, OrderItemMembership orderItemMembership);

    @Named("getUsingSupportPrice")
    default BigDecimal getUsingSupportPrice(OrderItemMembership orderItemMembership) {
        return orderItemMembership.getPrice().subtract(orderItemMembership.getDiscountPrice());
    }
}
