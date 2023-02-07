package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.DailyFoodMembershipDiscountDto;
import co.dalicious.domain.user.dto.MembershipBenefitDto;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface MembershipBenefitMapper {
    @Mapping(target = "nextPayDate", expression = "java(DateUtils.format(membership.getEndDate(), \"yyyy년 MM월 dd일\"))")
    @Mapping(source = "dailyFoodMembershipDiscountDto.totalMembershipDiscountDeliveryFee", target = "deliveryFee")
    @Mapping(source = "dailyFoodMembershipDiscountDto.totalMembershipDiscountPrice", target = "dailyFoodDiscountPrice")
    @Mapping(source = "dailyFoodMembershipDiscountDto", target = "totalDiscountBenefitPrice", qualifiedByName = "getTotalDiscountBenefitPrice")
    @Mapping(source = "refundablePrice", target = "refundablePrice")
    MembershipBenefitDto toDto(Membership membership, DailyFoodMembershipDiscountDto dailyFoodMembershipDiscountDto, BigDecimal refundablePrice);

    // TODO: 추후 마켓 추가시 더하는 로직 구현
    @Named("getTotalDiscountBenefitPrice")
    default BigDecimal getTotalDiscountBenefitPrice(DailyFoodMembershipDiscountDto dailyFoodMembershipDiscountDto) {
        BigDecimal totalDiscountBenefitPrice = BigDecimal.ZERO;
        return totalDiscountBenefitPrice.add(dailyFoodMembershipDiscountDto.getTotalMembershipDiscountPrice()).add(dailyFoodMembershipDiscountDto.getTotalMembershipDiscountDeliveryFee());
    }
}
