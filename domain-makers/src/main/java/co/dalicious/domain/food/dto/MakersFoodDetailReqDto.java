package co.dalicious.domain.food.dto;

import co.dalicious.domain.file.dto.ImageCreateRequestDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class MakersFoodDetailReqDto {
    private BigInteger foodId;
    private BigDecimal defaultPrice;
    private Integer makersDiscountRate;
    private Integer periodDiscountRate;
    private List<Integer> foodTags;
    private BigDecimal customPrice;
    private Integer capacity;
    private List<ImageCreateRequestDto> images;
}
