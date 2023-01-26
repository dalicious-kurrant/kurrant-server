package co.kurrant.app.public_api.controller.payment;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.payment.dto.PaymentRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "9. Payment")
@RequestMapping(value = "/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    @Operation(summary = "카드 결제", description = "결제 한다.")
    @PostMapping("")
    public ResponseMessage payCard(Authentication authentication, @RequestBody PaymentRequestDto paymentRequestDto){
        return ResponseMessage.builder()
                .message("결제가 완료되었습니다.")
                .build();
    }

}
