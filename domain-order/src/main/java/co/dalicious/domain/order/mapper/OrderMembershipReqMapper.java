package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItemMembership;
import co.dalicious.domain.user.entity.Membership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMembershipReqMapper {
//    @Mapping(target = "orderStatus", constant = "PROCESSING")
//    @Mapping(source = "order", target = "order")
//    @Mapping(source = "membership", target = "membership")
//    @Mapping(source = "membership.membershipSubscriptionType.price", target = "price")
//    OrderItemMembership toEntity(Order order, Membership membership);
}
