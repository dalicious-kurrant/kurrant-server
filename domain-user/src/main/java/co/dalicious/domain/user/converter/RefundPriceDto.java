package co.dalicious.domain.user.converter;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RefundPriceDto {
    private BigDecimal price;
    private BigDecimal renewSupportPrice;
    private BigDecimal point;

    public RefundPriceDto(BigDecimal price, BigDecimal renewSupportPrice, BigDecimal point) {
        this.price = price;
        this.renewSupportPrice  = renewSupportPrice;
        this.point = point;
    }

    public BigDecimal getDeductedSupportPrice(BigDecimal oldPrice) {
        return oldPrice.subtract(renewSupportPrice);
    }

    public Boolean isSameSupportPrice(BigDecimal oldPrice) {
        return oldPrice.compareTo(this.renewSupportPrice) == 0;
    }
}
