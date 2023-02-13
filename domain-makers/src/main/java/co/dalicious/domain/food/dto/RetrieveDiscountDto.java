package co.dalicious.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RetrieveDiscountDto {
    private BigDecimal price;
    private BigDecimal totalDiscountRate;
    private BigDecimal totalDiscountedPrice;
    private BigDecimal membershipDiscountedPrice;
    private Integer membershipDiscountRate;
    private BigDecimal makersDiscountedPrice;
    private Integer makersDiscountRate;
    private BigDecimal periodDiscountedPrice;
    private Integer periodDiscountRate;

    public RetrieveDiscountDto(DiscountDto discountDto) {
        this.price = discountDto.getPrice();
        this.totalDiscountRate = discountDto.getPrice().subtract(discountDto.getMembershipDiscountPrice()).subtract(discountDto.getMakersDiscountPrice()).subtract(discountDto.getPeriodDiscountPrice());
        this.totalDiscountedPrice = discountDto.getPrice().subtract(discountDto.getPrice().subtract(discountDto.getMembershipDiscountPrice()).subtract(discountDto.getMakersDiscountPrice()).subtract(discountDto.getPeriodDiscountPrice())).divide(discountDto.getPrice(), 3).multiply(BigDecimal.valueOf(100L));
        this.membershipDiscountedPrice = (discountDto.getMembershipDiscountRate() == 0) ?
                BigDecimal.ZERO : discountDto.getPrice().subtract(discountDto.getMembershipDiscountPrice());
        this.membershipDiscountRate = discountDto.getMembershipDiscountRate();
        this.makersDiscountedPrice = (discountDto.getMakersDiscountRate() == 0) ?
                BigDecimal.ZERO : discountDto.getPrice().subtract(discountDto.getMembershipDiscountPrice()).subtract(discountDto.getMakersDiscountPrice());
        this.makersDiscountRate = discountDto.getMakersDiscountRate();
        this.periodDiscountedPrice = (discountDto.getPeriodDiscountRate() == 0) ?
                BigDecimal.ZERO : discountDto.getPrice().subtract(discountDto.getMembershipDiscountPrice()).subtract(discountDto.getMakersDiscountPrice()).subtract(discountDto.getPeriodDiscountPrice());
        this.periodDiscountRate = discountDto.getPeriodDiscountRate();
    }
}
