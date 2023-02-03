package co.dalicious.domain.payment.dto;

import co.dalicious.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Schema(description = "CreditCardInfo 저장용 DTO")
@Setter
public class CreditCardSaveDto {
    private String cardNumber;
    private User userId;
    private String ownerType;
    private String cardType;
    private String customerKey;
    private String billingKey;
    private String cardCompany;
}
