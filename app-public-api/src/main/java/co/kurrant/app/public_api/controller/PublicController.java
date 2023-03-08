package co.kurrant.app.public_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.client.dto.ApartmentRequestDto;
import co.dalicious.domain.client.dto.CorporationRequestDto;
import co.dalicious.domain.client.service.ClientService;
import co.dalicious.domain.payment.dto.PaymentConfirmDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.OrderDailyFoodService;
import co.kurrant.app.public_api.service.UserService;
import co.kurrant.app.public_api.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins="*", allowedHeaders = "*")
@Tag(name = "5. Public")
@RequestMapping(value = "/v1/public")
@RestController
@RequiredArgsConstructor
public class PublicController {
    private final ClientService clientService;
    private final UserService userService;
    private final OrderDailyFoodService orderDailyFoodService;

    @Operation(summary = "멤버십 구독 정보 조회", description = "멤버십 구독 정보를 조회한다.")
    @GetMapping("/membership")
    public ResponseMessage getMembershipSubscriptionInfo() {
        return ResponseMessage.builder()
                .data(userService.getMembershipSubscriptionInfo())
                .message("멤버십 구독 정보 조회에 성공하셨습니다.")
                .build();
    }

    // TODO: 추후 백오피스 구현시 삭제
    @PostMapping("/apartment")
    public ResponseMessage createApartment(@RequestBody ApartmentRequestDto apartmentRequestDto) throws ParseException {
        clientService.createApartment(apartmentRequestDto);
        return ResponseMessage.builder()
                .message("아파트 개설에 성공하셨습니다.")
                .build();
    }
    // TODO: 추후 백오피스 구현시 삭제
    @PostMapping("/corporation")
    public ResponseMessage createCorporation(@RequestBody CorporationRequestDto corporationRequestDto) throws ParseException {
        clientService.createCorporation(corporationRequestDto);
        return ResponseMessage.builder()
                .message("기업 개설에 성공하셨습니다.")
                .build();
    }


}
