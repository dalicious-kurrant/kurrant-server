package co.dalicious.domain.user.converter;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RefundPriceDto {
    private BigDecimal price;
    private BigDecimal deliveryFee;
    private BigDecimal renewSupportPrice;
    private BigDecimal point;
    private Boolean isLastItemOfGroup;

    public RefundPriceDto(BigDecimal price, BigDecimal renewSupportPrice, BigDecimal point, BigDecimal deliveryFee, Boolean isLastItemOfGroup) {
        this.price = price;
        this.deliveryFee = deliveryFee;
        this.renewSupportPrice  = renewSupportPrice;
        this.point = point;
        this.isLastItemOfGroup = isLastItemOfGroup;
    }

    public BigDecimal getDeductedSupportPrice(BigDecimal oldPrice) {
        return oldPrice.subtract(renewSupportPrice);
    }

    public Boolean isSameSupportPrice(BigDecimal oldPrice) {
        return oldPrice.compareTo(this.renewSupportPrice) == 0;
    }
}
