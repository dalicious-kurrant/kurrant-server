package co.kurrant.app.public_api.controller.client;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormRequestDto;
import co.kurrant.app.public_api.dto.client.ApplicationFormMemoDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.controller.food.service.ApplicationFormService;
import co.kurrant.app.public_api.controller.food.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@Tag(name = "7. Application Form ClientType")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/application-form")
@RestController
public class ApplicationFormController {
    private final ApplicationFormService applicationFormService;

    @Operation(summary = "아파트 스팟 개설 신청 API", description = "아파트 스팟 개설을 신청한다.")
    @PostMapping("/apartments")
    public ResponseMessage registerApartmentSpot(Authentication authentication,
                                                 @RequestBody ApartmentApplicationFormRequestDto apartmentApplicationFormRequestDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(applicationFormService.registerApartmentSpot(securityUser, apartmentApplicationFormRequestDto))
                .message("아파트 스팟 개설 신청에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "아파트 스팟 개설 신청 내역", description = "아파트 스팟 개설 신청 상세 내역을 조회한다.")
    @GetMapping("/apartments/{id}")
    public ResponseMessage getApartmentApplicationFormDetail(Authentication authentication, @PathVariable BigInteger id) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("아파트 스팟 개설 신청 내역 조회에 성공하였습니다.")
                .data(applicationFormService.getApartmentApplicationFormDetail(securityUser.getId(), id))
                .build();
    }

    @Operation(summary = "아파트 스팟 개설 신청 내역 기타 내용 저장", description = "아파트 스팟 개설 신청 내역 기타 내용을 저장한다.")
    @PutMapping("/apartments/{id}/memo")
    public ResponseMessage updateApartmentApplicationFormMemo(Authentication authentication, @PathVariable BigInteger id, @RequestBody ApplicationFormMemoDto applicationFormMemoDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        applicationFormService.updateApartmentApplicationFormMemo(securityUser, id, applicationFormMemoDto);
        return ResponseMessage.builder()
                .message("아파트 스팟 개설 신청 내역의 기타 내용을 업데이트 하였습니다.")
                .build();
    }

    @Operation(summary = "기업 스팟 개설 신청 API", description = "기업 스팟 개설을 신청한다.")
    @PostMapping("/corporations")
    public ResponseMessage registerCorporationSpot(Authentication authentication, @RequestBody CorporationApplicationFormRequestDto corporationApplicationFormRequestDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(applicationFormService.registerCorporationSpot(securityUser, corporationApplicationFormRequestDto))
                .message("기업 스팟 개설 신청에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 스팟 개설 신청 내역", description = "기업 스팟 개설 신청 내역을 조회한다.")
    @GetMapping("/corporations/{id}")
    public ResponseMessage getCorporationApplicationFormDetail(Authentication authentication, @PathVariable BigInteger id) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("기업 스팟 신청 내역 조회에 성공하였습니다.")
                .data(applicationFormService.getCorporationApplicationFormDetail(securityUser.getId(), id))
                .build();
    }

    @Operation(summary = "기업 스팟 개설 신청 내역 기타 내용 저장", description = "기업 스팟 개설 신청 내역 기타 내용을 저장한다.")
    @PutMapping("/corporations/{id}/memo")
    public ResponseMessage SaveCorporationsApplicationFormMemo(Authentication authentication, @PathVariable BigInteger id, @RequestBody ApplicationFormMemoDto applicationFormMemoDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        applicationFormService.updateCorporationApplicationFormMemo(securityUser, id, applicationFormMemoDto);
        return ResponseMessage.builder()
                .message("기업 스팟 개설 신청 내역 기타 내용 수정에 성공하였습니다.")
                .build();

    }

    @Operation(summary = "스팟 신청 리스트", description = "스팟 개설 요청들의 리스트를 돌려준다.")
    @GetMapping("/clients")
    public ResponseMessage getSpotsApplicationList(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("스팟 신청 리스트를 조회하는데 성공하였습니다.")
                .data(applicationFormService.getSpotsApplicationList(securityUser.getId()))
                .build();
    }
}
