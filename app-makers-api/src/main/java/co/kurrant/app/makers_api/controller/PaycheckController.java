package co.kurrant.app.makers_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.PaycheckService;
import co.kurrant.app.makers_api.util.UserUtil;
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
    private final PaycheckService paycheckService;
    @Operation(summary = "메이커스 정산 조회", description = "메이커스 정산 조회")
    @GetMapping("")
    public ResponseMessage getMakersPaychecks(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(paycheckService.getMakersPaychecks(securityUser))
                .message("메이커스 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "메이커스 정산 상태 변경", description = "메이커스 정산 상태 변경")
    @PutMapping("/status/{status}")
    public ResponseMessage updateMakersPaycheckStatus(Authentication authentication, @PathVariable Integer status, @RequestBody List<BigInteger> ids) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        paycheckService.updateMakersPaycheckStatus(securityUser, status, ids);
        return ResponseMessage.builder()
                .message("메이커스 정산 상태 변경에 성공하였습니다.")
                .build();
    }
}
