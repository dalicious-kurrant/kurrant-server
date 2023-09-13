package co.dalicious.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "나이스 빌링키 발급을 위한 요청 DTO")
public class BillingKeyDto {
    @Schema(description = "카드 회사")
    private String corporationCode;
    @Schema(description = "개인/법인")
    private String cardType;
    @Schema(description = "카드 번호")
    private String cardNumber;
    @Schema(description = "만료년")
    private String expirationYear;
    @Schema(description = "만료월")
    private String expirationMonth;
    @Schema(description = "생년월일 및 사업자 번호")
    private String identityNumber;
    @Schema(description = "카드 비밀번호")
    private String cardPassword;
    @Schema(description = "디폴트타입(0:아무것도아님, 1:기본결제카드, 2:멤버십 기본 결제 카드)")
    private Integer defaultType;
    @Schema(description = "유저가 입력한 결제 비밀번호")
    private String payNumber;
    @Schema(description = "유저 이메일 설정을 위한 값")
    private String email;
    @Schema(description = "유저 비밀번호 설정을 위한 값")
    private String password;
}
