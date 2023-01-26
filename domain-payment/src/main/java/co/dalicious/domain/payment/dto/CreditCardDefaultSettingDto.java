package co.dalicious.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "카드 세팅 Request DTO")
public class CreditCardDefaultSettingDto {
    private BigInteger cardId;
    private Integer defaultType;
}
