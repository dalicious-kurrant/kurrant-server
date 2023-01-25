package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.entity.OrderItemMembership;
import co.dalicious.domain.user.dto.MembershipDto;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.util.MembershipUtil;
import org.mapstruct.*;

import java.time.LocalDate;


@Mapper(componentModel = "spring")
public interface OrderMembershipResMapper {
    @Mapping(source = "orderItemMembership.id", target = "id")
    @Mapping(source = "orderItemMembership.membership.membershipSubscriptionType.membershipSubscriptionType", target = "membershipSubscriptionType")
    @Mapping(source = "orderItemMembership.membership.startDate", target = "startDate")
    @Mapping(source = "orderItemMembership.membership.endDate", target = "endDate")
    @Mapping(source = "orderItemMembership.price", target = "price")
    @Mapping(source = "membershipUsingPeriod", target = "membershipUsingPeriod")
    @Mapping(target = "discountedPrice", expression = "java(orderItemMembership.getPrice().subtract(orderItemMembership.getDiscountPrice()))")
    MembershipDto toDto(OrderItemMembership orderItemMembership, int membershipUsingPeriod);
}
