package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.user.dto.MembershipDto;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.util.MembershipUtil;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Mapper(componentModel = "spring")
public interface OrderMembershipResMapper {
    @Mapping(source = "membership.membershipSubscriptionType.membershipSubscriptionType", target = "membershipSubscriptionType")
    @Mapping(source = "membership.startDate", target = "startDate")
    @Mapping(source = "membership.endDate", target = "endDate")
    @Mapping(source = "membership", target = "membershipUsingPeriod", qualifiedByName = "calculateMembershipUsingPeriod")
    @Mapping(source = "price", target = "price")
    @Mapping(target = "discountedPrice", expression = "java(orderMembership.getPrice().subtract(orderMembership.getDiscountPrice()))")
    MembershipDto toDto(OrderMembership orderMembership);

    @Named("calculateMembershipUsingPeriod")
    default int calculateMembershipUsingPeriod(Membership membership) {
        LocalDate startDate = membership.getStartDate();
        LocalDate endDate = membership.getEndDate();
        return MembershipUtil.getPeriodWithStartAndEndDate(startDate, endDate);
    }
}
