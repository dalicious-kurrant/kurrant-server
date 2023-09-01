package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestReqDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedReqDto;
import co.dalicious.domain.application_form.dto.StatusUpdateDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.RequestedMySpotDetailDto;
import co.dalicious.domain.application_form.dto.share.ShareSpotDto;
import co.kurrant.app.admin_api.dto.IdListDto;
import co.kurrant.app.admin_api.service.ApplicationFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Tag(name = "스팟 신청서")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/application-forms")
@RestController
public class ApplicationFormController {
    private final ApplicationFormService applicationFormService;

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "필터 정보 조회", description = "필터를 위한 정보를 조회합니다.")
    @GetMapping("/spots/my/filter")
    public ResponseMessage getAllListForFilter(@RequestParam(required = false) Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .message("조회를 성공했습니다.")
                .data(applicationFormService.getAllListForFilter(parameters))
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "마이 스팟 신청 조회", description = "마이 스팟 신청 현황를 조회합니다.")
    @GetMapping("/spots/my")
    public ResponseMessage getAllMySpotRequestList(@RequestParam(required = false) Map<String, Object> parameters,
                                                   @RequestParam Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("마이 스팟 신청 현황 조회를 성공했습니다.")
                .data(applicationFormService.getAllMySpotRequestList(parameters, limit, page, pageable))
                .build();
    }


    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "마이 스팟 신청 생성", description = "마이 스팟 신청을 추가합니다.")
    @PostMapping("/spots/my")
    public ResponseMessage createMySpotRequest(@RequestBody CreateRequestDto createRequestDto) {
        applicationFormService.createMySpotRequest(createRequestDto);
        return ResponseMessage.builder()
                .message("마이 스팟 신청을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "마이 스팟 신청 수정", description = "마이 스팟 신청 내역을 수정합니다.")
    @PatchMapping("/spots/my")
    public ResponseMessage updateMySpotRequest(@RequestBody RequestedMySpotDetailDto requestedMySpotDetailDto) {
        applicationFormService.updateMySpotRequest(requestedMySpotDetailDto);
        return ResponseMessage.builder()
                .message("마이 스팟 신청 내역 수정을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "마이 스팟 신청 삭제", description = "마이 스팟 신청을 삭제합니다.")
    @DeleteMapping("/spots/my")
    public ResponseMessage deleteMySpotRequest(@RequestBody IdListDto ids) {
        applicationFormService.deleteMySpotRequest(ids.getIds());
        return ResponseMessage.builder()
                .message("마이 스팟 신청을 삭제했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "마이 스팟 생성", description = "마이 스팟을 추가합니다.")
    @PostMapping("/create/zone")
    public ResponseMessage createMySpotZonesFromRequest(@RequestBody IdListDto ids) {
        applicationFormService.createMySpotZonesFromRequest(ids.getIds());
        return ResponseMessage.builder()
                .message("마이 스팟 생성을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "공유 스팟 신청 조회", description = "공유 스팟 신청 현황를 조회합니다.")
    @GetMapping("/spots/share")
    public ResponseMessage getAllShareSpotRequestList(@RequestParam(required = false) Integer type,
                                                   @RequestParam Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("공유 스팟 신청 현황 조회를 성공했습니다.")
                .data(applicationFormService.getAllShareSpotRequestList(type, limit, page, pageable))
                .build();
    }


    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "공유 스팟 신청 생성", description = "공유 스팟 신청을 추가합니다.")
    @PostMapping("/spots/share")
    public ResponseMessage createShareSpotRequest(@RequestBody ShareSpotDto.AdminRequest request) throws ParseException {
        applicationFormService.createShareSpotRequest(request);
        return ResponseMessage.builder()
                .message("공유 스팟 신청을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "공유 스팟 신청 수정", description = "공유 스팟 신청 내역을 수정합니다.")
    @PatchMapping("/spots/share/{applicationId}")
    public ResponseMessage updateMySpotRequest(@PathVariable BigInteger applicationId, @RequestBody ShareSpotDto.AdminRequest request) throws ParseException {
        applicationFormService.updateShareSpotRequest(applicationId, request);
        return ResponseMessage.builder()
                .message("공유 스팟 신청 내역 수정을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "공유 스팟 신청 삭제", description = "공유 스팟 신청을 삭제합니다.")
    @DeleteMapping("/spots/share")
    public ResponseMessage deleteShareSpotRequest(@RequestBody IdListDto ids) {
        applicationFormService.deleteShareSpotRequest(ids.getIds());
        return ResponseMessage.builder()
                .message("공유 스팟 신청을 삭제했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "마이 스팟 신청 갱신 조회", description = "이미 개설된 마이 스팟이 있는지 조회합니다.")
    @GetMapping("/spots/my/renew")
    public ResponseMessage findRenewalMySpotRequest() {
        return ResponseMessage.builder()
                .message("마이 스팟 갱신 신청 여부를 조회했습니다.")
                .data(applicationFormService.findRenewalMySpotRequest())
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "마이 스팟 신청 갱신", description = "이미 개설된 마이 스팟을 삭제합니다.")
    @PostMapping("/spots/my/renew")
    public ResponseMessage renewalMySpotRequest(@RequestBody IdListDto ids) {
        applicationFormService.renewalMySpotRequest(ids.getIds());
        return ResponseMessage.builder()
                .message("마이 스팟 신청을 갱신했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "메이커스 신청 조회", description = "메이커스 신청 현황를 조회합니다.")
    @GetMapping("/makers")
    public ResponseMessage getAllMakersRequestList(@RequestParam Integer limit, @RequestParam Integer page) {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(((long) limit * (page - 1)), limit, Sort.unsorted());
        return ResponseMessage.builder()
                .message("메이커스 신청 현황 조회를 성공했습니다.")
                .data(applicationFormService.getAllMakersRequestList(offsetBasedPageRequest))
                .build();
    }


    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "메이커스 신청 생성", description = "메이커스 신청을 추가합니다.")
    @PostMapping("/makers")
    public ResponseMessage createMakersRequest(@RequestBody MakersRequestedReqDto request) throws ParseException {
        applicationFormService.createMakersRequest(request);
        return ResponseMessage.builder()
                .message("메이커스 신청을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "메이커스 신청 수정", description = "메이커스 신청 내역을 수정합니다.")
    @PatchMapping("/makers/status/{applicationId}")
    public ResponseMessage updateMakerRequest(@PathVariable BigInteger applicationId, @RequestBody StatusUpdateDto request) throws ParseException {
        applicationFormService.updateMakerRequestStatus(applicationId, request);
        return ResponseMessage.builder()
                .message("메이커스 신청 내역 수정을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "메이커스 신청 삭제", description = "메이커스 신청을 삭제합니다.")
    @DeleteMapping("/makers")
    public ResponseMessage deleteMakersRequest(@RequestBody IdListDto ids) {
        applicationFormService.deleteMakersRequest(ids.getIds());
        return ResponseMessage.builder()
                .message("메이커스 신청을 삭제했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "프라이빗 스팟 신청 조회", description = "프라이빗 스팟 신청 현황를 조회합니다.")
    @GetMapping("/corporation")
    public ResponseMessage getAllCorporationRequestList(@RequestParam Integer limit, @RequestParam Integer page) {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(((long) limit * (page - 1)), limit, Sort.unsorted());
        return ResponseMessage.builder()
                .message("프라이빗 스팟 신청 현황 조회를 성공했습니다.")
                .data(applicationFormService.getAllCorporationRequestList(offsetBasedPageRequest))
                .build();
    }


    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "프라이빗 스팟 신청 생성", description = "프라이빗 스팟 신청을 추가합니다.")
    @PostMapping("/corporation")
    public ResponseMessage createCorporationRequest(@RequestBody CorporationRequestReqDto request) throws ParseException {
        applicationFormService.createCorporationRequest(request);
        return ResponseMessage.builder()
                .message("프라이빗 스팟 신청을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "프라이빗 스팟 신청 수정", description = "프라이빗 스팟 신청 내역을 수정합니다.")
    @PatchMapping("/corporation/status/{applicationId}")
    public ResponseMessage updateCorporationRequest(@PathVariable BigInteger applicationId, @RequestBody StatusUpdateDto request) throws ParseException {
        applicationFormService.updateCorporationRequestStatus(applicationId, request);
        return ResponseMessage.builder()
                .message("프라이빗 스팟 신청 내역 수정을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.APPLICATION_FORM)
    @Operation(summary = "프라이빗 스팟 신청 삭제", description = "프라이빗 스팟 신청을 삭제합니다.")
    @DeleteMapping("/corporation")
    public ResponseMessage deleteCorporationRequest(@RequestBody IdListDto ids) {
        applicationFormService.deleteCorporationRequest(ids.getIds());
        return ResponseMessage.builder()
                .message("프라이빗 스팟 신청을 삭제했습니다.")
                .build();
    }

}
