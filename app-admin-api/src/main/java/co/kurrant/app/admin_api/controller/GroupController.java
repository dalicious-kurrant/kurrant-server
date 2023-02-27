package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@Tag(name = "3. Group")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "v1/groups")
public class GroupController {

    public final GroupService groupService;

    @Operation(summary = "기업 정보 조회", description = "기업 정보를 조회합니다. 이름으로 필터링 할 수 있습니다.")
    @GetMapping("")
    public ResponseMessage getCorporationList(@RequestParam(required = false) BigInteger groupId, @RequestParam Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("기업 정보를 조회했습니다.")
                .data(groupService.getCorporationList(groupId, limit, page, pageable))
                .build();
    }
}
