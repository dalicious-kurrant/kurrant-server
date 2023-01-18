package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigInteger;

@Schema(description = "장바구니 담기 요청 DTO")
@Getter
public class CartDto {
    BigInteger dailyFoodId;
    Integer count;
}
