package co.kurrant.app.public_api.controller.point;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.PointService;
import co.kurrant.app.public_api.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "v1/users/me/point")
public class PointController {

    private final PointService pointService;

    @Operation(summary = "포인트 내역 조회", description = "포인트 내역을 조회합니다.")
    @GetMapping("")
    public ResponseMessage findAllPointLogs(Authentication authentication, @RequestParam Integer condition,
                                        @RequestParam(required = false) Integer limit,
                                        @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("포인트 내역 조회를 완료했습니다.")
                .data(pointService.findAllPointLogs(securityUser, condition, limit, page, pageable))
                .build();
    }
}
