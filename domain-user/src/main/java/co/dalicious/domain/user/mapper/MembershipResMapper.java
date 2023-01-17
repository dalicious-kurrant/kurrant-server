package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.MembershipDto;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.util.MembershipUtil;
import org.mapstruct.*;

import java.time.LocalDate;


@Mapper(componentModel = "spring")
public interface MembershipResMapper {
    @Mapping(source = "membershipSubscriptionType.membershipSubscriptionType", target = "membershipSubscriptionType")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    MembershipDto toDto(Membership membership);

    @AfterMapping
    default void calculateMembershipUsingPeriod(Membership membership, @MappingTarget MembershipDto membershipDto) {
        LocalDate startDate = membership.getStartDate();
        LocalDate endDate = membership.getEndDate();
        membershipDto.setMembershipUsingPeriod(MembershipUtil.getPeriodWithStartAndEndDate(startDate, endDate));
    }
}
