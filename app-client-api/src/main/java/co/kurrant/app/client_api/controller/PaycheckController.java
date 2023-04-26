package co.kurrant.app.client_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.ClientPaycheckService;
import co.kurrant.app.client_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/paycheck")
public class PaycheckController {
    private final ClientPaycheckService clientPaycheckService;

    @Operation(summary = "기업 정산 조회", description = "기업 정산 조회")
    @GetMapping("")
    public ResponseMessage getCorporationPaychecks(Authentication authentication, @RequestParam Map<String, Object> parameters) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(clientPaycheckService.getCorporationPaychecks(securityUser, parameters))
                .message("기업 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 식수 내역 조회", description = "기업 정산 식수 내역 조회")
    @GetMapping("/{paycheckId}/orders")
    public ResponseMessage getPaycheckOrders(Authentication authentication, @PathVariable BigInteger paycheckId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(clientPaycheckService.getPaycheckOrders(securityUser, paycheckId))
                .message("기업 정산 식수 내역 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 인보이스 조회", description = "기업 정산 인보이스 조회")
    @GetMapping("/{paycheckId}/invoice")
    public ResponseMessage getPaycheckInvoice(Authentication authentication, @PathVariable BigInteger paycheckId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(clientPaycheckService.getPaycheckInvoice(securityUser, paycheckId))
                .message("기업 정산 인보이스 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 상태 변경", description = "기업 정산 상태 변경")
    @PutMapping("/status/{status}")
    public ResponseMessage updateCorporationPaycheckStatus(Authentication authentication, @PathVariable Integer status, @RequestBody List<BigInteger> ids) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        clientPaycheckService.updateCorporationPaycheckStatus(securityUser, status, ids);
        return ResponseMessage.builder()
                .message("기업 정산 상태 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 완료", description = "기업 정산 완료")
    @PutMapping("/complete")
    public ResponseMessage completeCorporationPaycheckStatus(Authentication authentication, @RequestBody BigInteger id) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        clientPaycheckService.completeCorporationPaycheckStatus(securityUser, id);
        return ResponseMessage.builder()
                .message("기업 정산 완료 상태 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 메모 작성", description = "기업 정산 메모 작성")
    @PutMapping("/{paycheckId}/memo")
    public ResponseMessage postMemo(Authentication authentication, @PathVariable BigInteger paycheckId, @RequestBody PaycheckDto.MemoDto memoDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        clientPaycheckService.postMemo(securityUser, paycheckId, memoDto);
        return ResponseMessage.builder()
                .message("기업 정산 메모 작성에 성공하였습니다.")
                .build();
    }
}
