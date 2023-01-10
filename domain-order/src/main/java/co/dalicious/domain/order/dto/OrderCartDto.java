package co.dalicious.domain.order.dto;

import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DiningType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDate;

@Schema(description = "장바구니 담기 요청 DTO")
@Getter
@Setter
public class OrderCartDto {
    BigInteger dailyFoodId;
    Integer count;


    @Builder
    public OrderCartDto(BigInteger dailyFoodId, LocalDate serviceDate, Integer count, DiningType diningType) {
        this.dailyFoodId = dailyFoodId;
        this.count = count;
    }
}
