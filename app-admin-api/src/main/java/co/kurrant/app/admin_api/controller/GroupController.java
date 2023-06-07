package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.domain.address.dto.UpdateLocationDto;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.integration.client.user.dto.mySpotZone.CreateRequestDto;
import co.dalicious.integration.client.user.dto.mySpotZone.UpdateRequestDto;
import co.kurrant.app.admin_api.service.GroupService;
import co.kurrant.app.admin_api.util.AddressUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Tag(name = "3. Group")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/groups")
public class GroupController {

    public final GroupService groupService;
    public final AddressUtil addressUtil;

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

    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "필터 정보 조회", description = "필터를 위한 정보를 조회합니다.")
    @GetMapping("my/spot/zones/filter")
    public ResponseMessage getAllListForFilter(@RequestParam(required = false) Map<String, Object> filterRequest) {
        return ResponseMessage.builder()
                .message("조회를 성공했습니다.")
                .data(groupService.getAllListForFilter(filterRequest))
                .build();
    }

    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "마이 스팟 조회", description = "마이 스팟을 조회합니다.")
    @GetMapping("my/spot/zones")
    public ResponseMessage getAllMySpotZoneList(@RequestParam(required = false) Map<String, Object> parameters, Integer limit, Integer size, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("조회를 성공했습니다.")
                .data(groupService.getAllMySpotZoneList(parameters, limit, size, pageable))
                .build();
    }

    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "마이 스팟 생성", description = "마이 스팟을 생성합니다.")
    @PostMapping("my/spot/zones")
    public ResponseMessage createMySpotZone(@RequestBody CreateRequestDto createRequestDto) {
        groupService.createMySpotZone(createRequestDto);
        return ResponseMessage.builder()
                .message("스팟 생성에 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "마이 스팟 수정", description = "마이 스팟을 수정합니다.")
    @PatchMapping("my/spot/zones")
    public ResponseMessage updateMySpotZone(@RequestBody UpdateRequestDto updateRequestDto) {
        groupService.updateMySpotZone(updateRequestDto);
        return ResponseMessage.builder()
                .message("스팟 수정에 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "마이 스팟 삭제", description = "마이 스팟을 삭제합니다.")
    @PatchMapping("my/spot/zones/delete")
    public ResponseMessage deleteMySpotZone(@RequestBody List<BigInteger> id) {
        groupService.deleteMySpotZone(id);
        return ResponseMessage.builder()
                .message("스팟 삭제에 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "주소 location 등록", description = "좌표를 등록합니다.")
    @PatchMapping("location")
    public ResponseMessage updateLocation(@RequestBody List<UpdateLocationDto> dto) throws ParseException {
        addressUtil.updateLocation(dto);
        return ResponseMessage.builder()
                .message("좌표 등록에 성공했습니다.")
                .build();
    }
}
