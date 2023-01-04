package co.kurrant.app.public_api.controller.client;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationSpotRequestDto;
import co.kurrant.app.public_api.dto.client.ApplicationFormMemoDto;
import co.kurrant.app.public_api.service.ApplicationFormService;
import io.swagger.models.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "7. Application Form ClientType")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/application-form")
@RestController
public class ApplicationFormController {
    private final ApplicationFormService applicationFormService;

    @Operation(summary = "아파트 스팟 개설 신청 API", description = "아파트 스팟 개설을 신청한다.")
    @PostMapping("/apartments")
    public ResponseMessage registerApartmentSpot(HttpServletRequest httpServletRequest,
                                                 @RequestBody ApartmentApplicationFormRequestDto apartmentApplicationFormRequestDto) {
        return ResponseMessage.builder()
                .data(applicationFormService.registerApartmentSpot(httpServletRequest, apartmentApplicationFormRequestDto))
                .message("아파트 스팟 개설 신청에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "아파트 스팟 개설 신청 내역", description = "아파트 스팟 개설 신청 상세 내역을 조회한다.")
    @GetMapping("/apartments/{id}")
    public ResponseMessage getApartmentApplicationFormDetail(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        return ResponseMessage.builder()
                .message("아파트 스팟 개설 신청 내역 조회에 성공하였습니다.")
                .data(applicationFormService.getApartmentApplicationFormDetail(httpServletRequest, id))
                .build();
    }

    @Operation(summary = "아파트 스팟 개설 신청 내역 기타 내용 저장", description = "아파트 스팟 개설 신청 내역 기타 내용을 저장한다.")
    @PutMapping("/apartments/{id}/memo")
    public ResponseMessage updateApartmentApplicationFormMemo(HttpServletRequest httpServletRequest, @PathVariable Long id, @RequestBody ApplicationFormMemoDto applicationFormMemoDto) {
        applicationFormService.updateApartmentApplicationFormMemo(httpServletRequest, id, applicationFormMemoDto);
        return ResponseMessage.builder()
                .message("아파트 스팟 개설 신청 내역의 기타 내용을 업데이트 하였습니다.")
                .build();
    }

    @Operation(summary = "기업 스팟 개설 신청 API", description = "기업 스팟 개설을 신청한다.")
    @PostMapping("/corporations")
    public ResponseMessage registerCorporationSpot(HttpServletRequest httpServletRequest, @RequestBody CorporationApplicationFormRequestDto corporationApplicationFormRequestDto) {
        return ResponseMessage.builder()
                .data(applicationFormService.registerCorporationSpot(httpServletRequest, corporationApplicationFormRequestDto))
                .message("기업 스팟 개설 신청에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 스팟 개설 신청 내역", description = "기업 스팟 개설 신청 내역을 조회한다.")
    @GetMapping("/corporations/{id}")
    public ResponseMessage getCorporationApplicationFormDetail(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        return ResponseMessage.builder()
                .message("기업 스팟 신청 내역 조회에 성공하였습니다.")
                .data(applicationFormService.getCorporationApplicationFormDetail(httpServletRequest, id))
                .build();
    }

    @Operation(summary = "기업 스팟 개설 신청 내역 기타 내용 저장", description = "기업 스팟 개설 신청 내역 기타 내용을 저장한다.")
    @PutMapping("/corporations/{id}/memo")
    public ResponseMessage SaveCorporationsApplicationFormMemo(HttpServletRequest httpServletRequest, @PathVariable Long id, @RequestBody ApplicationFormMemoDto applicationFormMemoDto) {
        applicationFormService.updateCorporationApplicationFormMemo(httpServletRequest, id, applicationFormMemoDto);
        return ResponseMessage.builder()
                .message("기업 스팟 개설 신청 내역 기타 내용 수정에 성공하였습니다.")
                .build();

    }

    @Operation(summary = "스팟 신청 리스트", description = "스팟 개설 요청들의 리스트를 돌려준다.")
    @GetMapping("/clients")
    public ResponseMessage getSpotsApplicationList(HttpServletRequest httpServletRequest) {
        return ResponseMessage.builder()
                .message("스팟 신청 리스트를 조회하는데 성공하였습니다.")
                .data(applicationFormService.getSpotsApplicationList(httpServletRequest))
                .build();
    }
}
