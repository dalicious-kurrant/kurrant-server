package co.kurrant.app.public_api.mapper.food;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodDetailDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.domain.food.entity.Origin;
import co.dalicious.domain.food.util.OriginDto;
import co.dalicious.domain.order.service.DiscountPolicyImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodMapper {
    @Mapping(source = "makers.name", target = "makersName")
    @Mapping(target = "membershipDiscountedPrice", qualifiedByName = "discount.membershipDiscountedPrice")
    @Mapping(target = "membershipDiscountedRate", qualifiedByName = "discount.membershipDiscountedRate")
    @Mapping(target = "makersDiscountedPrice", qualifiedByName = "discount.makersDiscountedPrice")
    @Mapping(target = "makersDiscountedRate", qualifiedByName = "discount.makersDiscountedRate")
    @Mapping(target = "periodDiscountedPrice", qualifiedByName = "discount.periodDiscountedPrice")
    @Mapping(target = "periodDiscountedRate", qualifiedByName = "discount.periodDiscountedRate")
    @Mapping(source = "image.location", target = "image")
    @Mapping(source = "spicy.spicy", target = "spicy")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "origins", target = "origins", qualifiedByName = "originsToDto")
    FoodDetailDto toDto(Food food);

    @Named("originsToDto")
    default List<OriginDto> originsToDto(List<Origin> origins) {
        List<OriginDto> originDtos = new ArrayList<>();
        for (Origin origin : origins) {
            OriginDto originDto = OriginDto.builder()
                    .name(origin.getName())
                    .from(origin.getFrom())
                    .build();
            originDtos.add(originDto);
        }
        return originDtos;
    }
    @Named("discount")
    default DiscountDto getDiscount(Food food) {
        DiscountDto discountDto = new DiscountDto();
        // 기본 가격
        BigDecimal price = food.getPrice();
        discountDto.setPrice(price);
        // 할인 비율
        Integer membershipDiscountedRate = 0;
        Integer makersDiscountedRate = 0;
        Integer periodDiscountedRate = 0;
        // 할인 가격
        BigDecimal membershipDiscountedPrice = null;
        BigDecimal makersDiscountedPrice = null;
        BigDecimal periodDiscountedPrice = null;

        for(FoodDiscountPolicy foodDiscountPolicy : food.getFoodDiscountPolicyList()) {
            switch (foodDiscountPolicy.getDiscountType()) {
                case MEMBERSHIP_DISCOUNT -> membershipDiscountedRate = foodDiscountPolicy.getDiscountRate();
                case MAKERS_DISCOUNT -> makersDiscountedRate = foodDiscountPolicy.getDiscountRate();
                case PERIOD_DISCOUNT -> periodDiscountedRate = foodDiscountPolicy.getDiscountRate();
            }
        }

        // 1. 멤버십 할인
        if(membershipDiscountedRate != 0) {
            membershipDiscountedPrice = DiscountPolicyImpl.discountedTotalPrice(price, membershipDiscountedRate);
            price = membershipDiscountedPrice;
        }
        // 2. 메이커스 할인
        if(makersDiscountedRate != 0) {
            makersDiscountedPrice = DiscountPolicyImpl.discountedTotalPrice(price, makersDiscountedRate);
            price = makersDiscountedPrice;
        }
        // 3. 기간 할인
        if(periodDiscountedRate != 0) {
            periodDiscountedPrice = DiscountPolicyImpl.discountedTotalPrice(price, periodDiscountedRate);
            price = membershipDiscountedPrice;
        }
        discountDto.setMembershipDiscountedRate(membershipDiscountedRate);
        discountDto.setMembershipDiscountedPrice(membershipDiscountedPrice);
        discountDto.setMakersDiscountedRate(makersDiscountedRate);
        discountDto.setMakersDiscountedPrice(makersDiscountedPrice);
        discountDto.setPeriodDiscountedRate(periodDiscountedRate);
        discountDto.setPeriodDiscountedPrice(periodDiscountedPrice);

        return discountDto;
    }
}
