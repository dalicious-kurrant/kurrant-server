package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.RequestedMySpotDetailDto;
import co.kurrant.app.admin_api.service.GroupRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Tag(name = "Group Request")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/my/zone/requests")
@RestController
public class GroupRequestController {

    private final GroupRequestService groupRequestService;

    @ControllerMarker(ControllerType.GROUP_REQUEST)
    @Operation(summary = "필터 정보 조회", description = "필터를 위한 정보를 조회합니다.")
    @GetMapping("/filter")
    public ResponseMessage getAllListForFilter(@RequestParam(required = false) Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .message("조회를 성공했습니다.")
                .data(groupRequestService.getAllListForFilter(parameters))
                .build();
    }

    @ControllerMarker(ControllerType.GROUP_REQUEST)
    @Operation(summary = "마이 스팟 신청 조회", description = "마이 스팟 신청 현황를 조회합니다.")
    @GetMapping("/all")
    public ResponseMessage getAllMySpotRequestList(@RequestParam(required = false) Map<String, Object> parameters,
                                                   @RequestParam Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("마이 스팟 신청 현황 조회를 성공했습니다.")
                .data(groupRequestService.getAllMySpotRequestList(parameters, limit, page, pageable))
                .build();
    }


    @ControllerMarker(ControllerType.GROUP_REQUEST)
    @Operation(summary = "마이 스팟 신청 생성", description = "마이 스팟 신청을 추가합니다.")
    @PostMapping("")
    public ResponseMessage createMySpotRequest(@RequestBody CreateRequestDto createRequestDto) {
        groupRequestService.createMySpotRequest(createRequestDto);
        return ResponseMessage.builder()
                .message("마이 스팟 신청을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.GROUP_REQUEST)
    @Operation(summary = "마이 스팟 신청 수정", description = "마이 스팟 신청 내역을 수정합니다.")
    @PatchMapping("")
    public ResponseMessage updateMySpotRequest(@RequestBody RequestedMySpotDetailDto requestedMySpotDetailDto) {
        groupRequestService.updateMySpotRequest(requestedMySpotDetailDto);
        return ResponseMessage.builder()
                .message("마이 스팟 신청 내역 수정을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.GROUP_REQUEST)
    @Operation(summary = "마이 스팟 신청 삭제", description = "마이 스팟 신청을 삭제합니다.")
    @DeleteMapping("")
    public ResponseMessage deleteMySpotRequest(@RequestBody List<BigInteger> ids) {
        groupRequestService.deleteMySpotRequest(ids);
        return ResponseMessage.builder()
                .message("마이 스팟 신청을 삭제했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.GROUP_REQUEST)
    @Operation(summary = "마이 스팟 생성", description = "마이 스팟을 추가합니다.")
    @PostMapping("/create/zone")
    public ResponseMessage createMySpotZonesFromRequest(@RequestBody List<BigInteger> ids) {
        groupRequestService.createMySpotZonesFromRequest(ids);
        return ResponseMessage.builder()
                .message("마이 스팟 생성을 성공했습니다.")
                .build();
    }
}


