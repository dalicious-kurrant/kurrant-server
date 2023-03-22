package co.dalicious.domain.food.dto;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.system.enums.DiscountType;
import co.dalicious.system.util.PriceUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "정기식사 가격/할인 응답 DTO")
public class DiscountDto {
    private BigDecimal price;
    private BigDecimal membershipDiscountPrice;
    private Integer membershipDiscountRate;
    private BigDecimal makersDiscountPrice;
    private Integer makersDiscountRate;
    private BigDecimal periodDiscountPrice;
    private Integer periodDiscountRate;

    public static DiscountDto getDiscount(Food food) {
        DiscountDto discountDto = new DiscountDto();
        // 기본 가격
        BigDecimal price = food.getPrice();
        discountDto.setPrice(price);
        // 할인 비율
        Integer membershipDiscountedRate = food.getFoodDiscountPolicy(DiscountType.MEMBERSHIP_DISCOUNT) == null ? 0 : food.getFoodDiscountPolicy(DiscountType.MEMBERSHIP_DISCOUNT).getDiscountRate();
        Integer makersDiscountedRate = food.getFoodDiscountPolicy(DiscountType.MAKERS_DISCOUNT) == null ? 0 : food.getFoodDiscountPolicy(DiscountType.MAKERS_DISCOUNT).getDiscountRate();
        Integer periodDiscountedRate = food.getFoodDiscountPolicy(DiscountType.PERIOD_DISCOUNT) == null ? 0 : food.getFoodDiscountPolicy(DiscountType.PERIOD_DISCOUNT).getDiscountRate();
        // 할인 가격
        BigDecimal membershipDiscountedPrice = BigDecimal.ZERO;
        BigDecimal makersDiscountedPrice = BigDecimal.ZERO;
        BigDecimal periodDiscountedPrice = BigDecimal.ZERO;

        // 1. 멤버십 할인
        if(membershipDiscountedRate != 0) {
            membershipDiscountedPrice = price.multiply(BigDecimal.valueOf((membershipDiscountedRate / 100.0)));
            price = price.subtract(membershipDiscountedPrice);
        }
        // 2. 메이커스 할인
        if(makersDiscountedRate != 0) {
            makersDiscountedPrice = price.multiply(BigDecimal.valueOf((makersDiscountedRate) / 100.0));
            price = price.subtract(makersDiscountedPrice);
        }
        // 3. 기간 할인
        if(periodDiscountedRate != 0) {
            periodDiscountedPrice = price.multiply(BigDecimal.valueOf((periodDiscountedRate) / 100.0));
        }
        discountDto.setMembershipDiscountRate(membershipDiscountedRate);
        discountDto.setMembershipDiscountPrice(membershipDiscountedPrice);
        discountDto.setMakersDiscountRate(makersDiscountedRate);
        discountDto.setMakersDiscountPrice(makersDiscountedPrice);
        discountDto.setPeriodDiscountRate(periodDiscountedRate);
        discountDto.setPeriodDiscountPrice(periodDiscountedPrice);

        return discountDto;
    }

    public static DiscountDto getDiscountWithoutMembership(Food food) {
        DiscountDto discountDto = new DiscountDto();
        // 기본 가격
        BigDecimal price = food.getPrice();
        discountDto.setPrice(price);
        // 할인 비율
        Integer membershipDiscountedRate = 0;
        Integer makersDiscountedRate = 0;
        Integer periodDiscountedRate = 0;
        // 할인 가격
        BigDecimal membershipDiscountedPrice = BigDecimal.ZERO;
        BigDecimal makersDiscountedPrice = BigDecimal.ZERO;
        BigDecimal periodDiscountedPrice = BigDecimal.ZERO;

        for(FoodDiscountPolicy foodDiscountPolicy : food.getFoodDiscountPolicyList()) {
            switch (foodDiscountPolicy.getDiscountType()) {
                case MAKERS_DISCOUNT -> makersDiscountedRate = foodDiscountPolicy.getDiscountRate();
                case PERIOD_DISCOUNT -> periodDiscountedRate = foodDiscountPolicy.getDiscountRate();
            }
        }

        // 1. 메이커스 할인
        if(makersDiscountedRate != 0) {
            makersDiscountedPrice = price.multiply(BigDecimal.valueOf((makersDiscountedRate) / 100.0));
            makersDiscountedPrice = PriceUtils.roundToOneDigit(makersDiscountedPrice);
            price = price.subtract(makersDiscountedPrice);
        }
        // 2. 기간 할인
        if(periodDiscountedRate != 0) {
            periodDiscountedPrice = price.multiply(BigDecimal.valueOf((periodDiscountedRate) / 100.0));
            periodDiscountedPrice = PriceUtils.roundToOneDigit(periodDiscountedPrice);
        }
        discountDto.setMembershipDiscountRate(membershipDiscountedRate);
        discountDto.setMembershipDiscountPrice(membershipDiscountedPrice);
        discountDto.setMakersDiscountRate(makersDiscountedRate);
        discountDto.setMakersDiscountPrice(makersDiscountedPrice);
        discountDto.setPeriodDiscountRate(periodDiscountedRate);
        discountDto.setPeriodDiscountPrice(periodDiscountedPrice);

        return discountDto;
    }

    public BigDecimal getDiscountedPrice() {
        BigDecimal totalPrice = this.price;
        if(this.membershipDiscountPrice != null && this.membershipDiscountPrice.compareTo(BigDecimal.ZERO) > 0) {
            totalPrice = totalPrice.subtract(this.membershipDiscountPrice);
        }
        if(this.makersDiscountPrice != null && this.makersDiscountPrice.compareTo(BigDecimal.ZERO) > 0) {
            totalPrice = totalPrice.subtract(this.makersDiscountPrice);
        }
        if(this.periodDiscountPrice != null && this.periodDiscountPrice.compareTo(BigDecimal.ZERO) > 0) {
            totalPrice = totalPrice.subtract(this.periodDiscountPrice);
        }
        return totalPrice;
    }
}
