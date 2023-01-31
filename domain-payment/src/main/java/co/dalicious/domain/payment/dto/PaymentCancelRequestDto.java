package co.dalicious.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "결제 전체 취소 요청 DTO")
public class PaymentCancelRequestDto {
    @Schema(description = "결제 전체 취소할 PaymentKey")
    private String paymentKey;
    @Schema(description = "결제 전체 취소할 PaymentKey")
    private String cancelReason;


}

/*
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.tosspayments.com/v1/payments/z0my-X3ip3A2APNr5FWMu/cancel"))
            .header("Authorization", "Basic dGVzdF9za19ZWjFhT3dYN0s4bWdwYnEyUjRRVnlReHp2TlBHOg==")
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\"고객이 취소를 원함\"}"))
            .build();
    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
 */