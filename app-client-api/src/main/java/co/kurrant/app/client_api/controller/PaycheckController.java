package co.kurrant.app.client_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.PaycheckService;
import co.kurrant.app.client_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/paycheck")
public class PaycheckController {
    private PaycheckService paycheckService;
    @Operation(summary = "기업 정산 조회", description = "기업 정산 조회")
    @GetMapping("/corporations")
    public ResponseMessage getCorporationPaychecks(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(paycheckService.getCorporationPaychecks(securityUser))
                .message("기업 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 상태 변경", description = "기업 정산 상태 변경")
    @PutMapping("/corporations/status/{status}")
    public ResponseMessage updateCorporationPaycheckStatus(Authentication authentication, @PathVariable Integer status, @RequestBody List<BigInteger> ids) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        paycheckService.updateCorporationPaycheckStatus(securityUser, status, ids);
        return ResponseMessage.builder()
                .message("기업 정산 상태 변경에 성공하였습니다.")
                .build();
    }
}
