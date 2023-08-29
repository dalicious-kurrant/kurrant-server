package co.kurrant.app.public_api.controller.point;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.PointService;
import co.kurrant.app.public_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "포인트")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "v1/users/me/point")
public class PointController {

    private final PointService pointService;

    @Operation(summary = "포인트 내역 조회", description = "포인트 내역을 조회합니다.")
    @GetMapping("")
    public ResponseMessage findAllPointLogs(Authentication authentication, @RequestParam Integer condition,
                                        @RequestParam(required = false, defaultValue = "20") Integer limit,
                                        @RequestParam Integer page) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(((long) limit * (page - 1)), limit, Sort.unsorted());
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("포인트 내역 조회를 완료했습니다.")
                .data(pointService.findAllPointLogs(securityUser, condition, pageable)) // limit, page
                .build();
    }
}
