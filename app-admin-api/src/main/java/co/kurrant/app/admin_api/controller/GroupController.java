package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.dto.GroupListDto;
import co.kurrant.app.admin_api.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Tag(name = "3. Group")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "v1/groups")
public class GroupController {

    public final GroupService groupService;

    @Operation(summary = "기업 정보 조회", description = "기업 정보를 조회합니다. 이름으로 필터링 할 수 있습니다.")
    @GetMapping("")
    public ResponseMessage getGroupList(@RequestParam(required = false) BigInteger groupId, @RequestParam(required = false) Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("기업 정보를 조회했습니다.")
                .data(groupService.getGroupList(groupId, limit, page, pageable))
                .build();
    }

    @Operation(summary = "기업 정보 저장", description = "기업 정보를 저장했습니다.")
    @PostMapping("/excel")
    public ResponseMessage saveCorporationList(@RequestBody List<GroupExcelRequestDto> corporationListDto) throws ParseException {
        groupService.saveCorporationList(corporationListDto);
        return ResponseMessage.builder()
                .message("기업 정보를 저장했습니다.")
                .build();
    }
}
