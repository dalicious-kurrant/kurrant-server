package co.dalicious.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "결제 부분 취소 요청 DTO")
public class PaymentCancelRequestDto {

    @Schema(description = "결제 부분 취소할 OrderId")
    private BigInteger orderItemId;
    @Schema(description = "결제 취소 사유")
    private String cancelReason;
    @Schema(description = "부분 취소할 금액")
    private Integer cancelAmount;


}

/*
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.tosspayments.com/v1/payments/4f5G2XX3kYJWrxhMrDSkJ/cancel"))
    .header("Authorization", "Basic dGVzdF9za19ZWjFhT3dYN0s4bWdwYnEyUjRRVnlReHp2TlBHOg==")
    .header("Content-Type", "application/json")
    .method("POST", HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\"고객이 취소를 원함\",\"cancelAmount\":1000}"))
    .build();
HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
 */