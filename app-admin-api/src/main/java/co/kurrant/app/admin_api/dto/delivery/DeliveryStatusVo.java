package co.kurrant.app.admin_api.dto.delivery;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class DeliveryStatusVo {
    private BigInteger spotId;
    private String deliveryTime;
}
