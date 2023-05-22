package co.kurrant.app.public_api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "카드 할부 처리를 위한 임시 DTo")
public class OrderCardQuotaDto {

    private String orderId;
    private String orderName;
    private String billingKey;
    private Integer cardQuota;
    private Integer amount;


}
