package co.kurrant.app.public_api.dto.order;

import lombok.Data;

import java.math.BigInteger;

@Data
public class UpdateCart {
    private BigInteger dailyFoodId;
    private Integer count;
}
