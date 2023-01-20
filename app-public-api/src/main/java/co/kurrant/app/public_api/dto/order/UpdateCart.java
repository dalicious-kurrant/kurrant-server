package co.kurrant.app.public_api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;


@Getter
@Setter
@Schema(description = "장바구니 수량수정 요청 DTO")
public class UpdateCart {
    private BigInteger cartItemId;
    private BigInteger dailyFoodId;
    private Integer count;
}
