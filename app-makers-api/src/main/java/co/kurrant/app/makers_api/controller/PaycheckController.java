package co.kurrant.app.makers_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.MakersPaycheckService;
import co.kurrant.app.makers_api.util.UserUtil;
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
    private final MakersPaycheckService makersPaycheckService;
    @Operation(summary = "메이커스 정산 조회", description = "메이커스 정산 조회")
    @GetMapping("")
    public ResponseMessage getMakersPaychecks(Authentication authentication, @RequestParam Map<String, Object> parameters) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(makersPaycheckService.getMakersPaychecks(securityUser, parameters))
                .message("메이커스 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "메이커스 정산 상세 조회", description = "메이커스 정산 조회")
    @GetMapping("/{paycheckId}")
    public ResponseMessage getPaycheckDetail(Authentication authentication, @PathVariable BigInteger paycheckId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(makersPaycheckService.getPaycheckDetail(securityUser, paycheckId))
                .message("메이커스 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "메이커스 정산 상태 변경", description = "메이커스 정산 상태 변경")
    @PutMapping("/status/{status}")
    public ResponseMessage updateMakersPaycheckStatus(Authentication authentication, @PathVariable Integer status, @RequestBody List<BigInteger> ids) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        makersPaycheckService.updateMakersPaycheckStatus(securityUser, status, ids);
        return ResponseMessage.builder()
                .message("메이커스 정산 상태 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "메이커스 정산 메모 작성", description = "메이커스 정산 메모 작성")
    @PutMapping("/{paycheckId}/memo")
    public ResponseMessage postMemo(Authentication authentication, @PathVariable BigInteger paycheckId, @RequestBody PaycheckDto.MemoDto memoDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        makersPaycheckService.postMemo(securityUser, paycheckId, memoDto);
        return ResponseMessage.builder()
                .message("메이커스 정산 메모 작성에 성공하였습니다.")
                .build();
    }
}
