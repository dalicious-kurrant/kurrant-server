package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.DailyFoodMembershipDiscountDto;
import co.dalicious.domain.user.dto.MembershipBenefitDto;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface MembershipBenefitMapper {
    @Mapping(target = "nextPayDate", expression = "java(DateUtils.format(membership.getEndDate(), \"yyyy년 MM월 dd일\"))")
    @Mapping(source = "dailyFoodMembershipDiscountDto.totalMembershipDiscountPrice", target = "deliveryFee")
    @Mapping(source = "dailyFoodMembershipDiscountDto.totalMembershipDiscountDeliveryFee", target = "dailyFoodDiscountPrice")
    MembershipBenefitDto toDto(Membership membership, DailyFoodMembershipDiscountDto dailyFoodMembershipDiscountDto);
}