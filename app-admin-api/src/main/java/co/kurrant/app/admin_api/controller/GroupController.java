package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
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
@RequestMapping(value = "/v1/groups")
public class GroupController {

    public final GroupService groupService;

    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "기업 정보 조회", description = "기업 정보를 조회합니다. 이름으로 필터링 할 수 있습니다.")
    @GetMapping("")
    public ResponseMessage getGroupList(@RequestParam(required = false) BigInteger groupId, @RequestParam(required = false) Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("기업 정보를 조회했습니다.")
                .data(groupService.getGroupList(groupId, limit, page, pageable))
                .build();
    }

    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "기업 정보 저장", description = "기업 정보를 저장했습니다.")
    @PostMapping("/excel")
    public ResponseMessage saveCorporationList(@RequestBody List<GroupExcelRequestDto> corporationListDto) throws ParseException {
        groupService.saveCorporationList(corporationListDto);
        return ResponseMessage.builder()
                .message("기업 정보를 저장했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "기업 정보 조회", description = "기업 정보를 조회했습니다.")
    @GetMapping("/excels")
    public ResponseMessage getAllGroupForExcel() {
        return ResponseMessage.builder()
                .message("기업 정보를 조회했습니다.")
                .data(groupService.getAllGroupForExcel())
                .build();
    }

    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "기업 정보 상세 조회", description = "기업 의 상세정보를 조회합니다.")
    @GetMapping("/detail")
    public ResponseMessage getSpotDetail(@RequestParam Integer spotId) {
        return ResponseMessage.builder()
                .message("기업 상세 정보를 조회했습니다.")
                .data(groupService.getGroupDetail(spotId))
                .build();
    }

    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "기업 정보 상세 수정", description = "기업 의 상세정보를 수정합니다.")
    @PatchMapping("/detail")
    public ResponseMessage updateSpotDetail(@RequestBody UpdateSpotDetailRequestDto updateSpotDetailRequestDto) throws ParseException {
        groupService.updateGroupDetail(updateSpotDetailRequestDto);
        return ResponseMessage.builder()
                .message("기업  상세 정보를 수정했습니다.")
                .build();
    }

}
