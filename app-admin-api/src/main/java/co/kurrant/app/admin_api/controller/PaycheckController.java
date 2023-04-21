package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.service.PaycheckService;
import co.kurrant.app.admin_api.service.AdminPaycheckService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/paycheck")
public class PaycheckController {
    private final AdminPaycheckService adminPaycheckService;

    @Operation(summary = "메이커스 정산 등록", description = "메이커스 정산 등록")
    @PostMapping("/makers")
    public ResponseMessage postMakersPaycheck(@RequestPart(required = false) MultipartFile makersXlsx,
                                              @RequestPart(required = false) MultipartFile makersPdf,
                                              @RequestPart PaycheckDto.MakersRequest paycheckDto) throws IOException {
        adminPaycheckService.postMakersPaycheck(makersXlsx, makersPdf, paycheckDto);
        return ResponseMessage.builder()
                .message("메이커스 정산 등록에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "메이커스 정산 조회", description = "메이커스 정산 조회")
    @GetMapping("/makers")
    public ResponseMessage getMakersPaychecks(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .data(adminPaycheckService.getMakersPaychecks(parameters))
                .message("메이커스 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "메이커스 정산 상세 조회", description = "메이커스 정산 조회")
    @GetMapping("/makers/{makersPaycheckId}")
    public ResponseMessage getMakersPaycheckDetail(@PathVariable BigInteger makersPaycheckId) {
        return ResponseMessage.builder()
                .data(adminPaycheckService.getMakersPaycheckDetail(makersPaycheckId))
                .message("메이커스 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "메이커스 정산 상태 변경", description = "메이커스 정산 상태 변경")
    @PutMapping("/makers/status/{status}")
    public ResponseMessage updateMakersPaycheckStatus(@PathVariable Integer status, @RequestBody List<BigInteger> ids) {
        adminPaycheckService.updateMakersPaycheckStatus(status, ids);
        return ResponseMessage.builder()
                .message("메이커스 정산 상태 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "메이커스 정산 이슈 추가", description = "메이커스 정산 이슈 추가")
    @PostMapping("/makers/{makersPaycheckId}/issues")
    public ResponseMessage postPaycheckAdd(@PathVariable BigInteger makersPaycheckId, @RequestBody List<PaycheckDto.PaycheckAddDto> paycheckAddDtos) {
        adminPaycheckService.postPaycheckAdd(makersPaycheckId, paycheckAddDtos);
        return ResponseMessage.builder()
                .message("메이커스 정산 이슈 추가에 성공하였습니다.")
                .build();
    }

//    @Operation(summary = "메이커스 정산 수정", description = "메이커스 정산 등록")
//    @PatchMapping("/makers")
//    public ResponseMessage updateMakersPaycheck(@RequestPart(required = false) MultipartFile makersXlsx,
//                                                @RequestPart(required = false) MultipartFile makersPdf,
//                                                @RequestPart PaycheckDto.MakersResponse paycheckDto) throws IOException {
//        adminPaycheckService.updateMakersPaycheck(makersXlsx, makersPdf, paycheckDto);
//        return ResponseMessage.builder()
//                .message("메이커스 정산 수정에 성공하였습니다.")
//                .build();
//    }

//    @Operation(summary = "메이커스 정산 삭제", description = "메이커스 정산 삭제")
//    @DeleteMapping("/makers")
//    public ResponseMessage deleteMakersPaycheck(@RequestBody List<BigInteger> ids) {
//        adminPaycheckService.deleteMakersPaycheck(ids);
//        return ResponseMessage.builder()
//                .message("메이커스 정산 상태 변경에 성공하였습니다.")
//                .build();
//    }

    @Operation(summary = "기업 정산 등록", description = "기업 정산 등록")
    @PostMapping("/corporations")
    public ResponseMessage postCorporationPaycheck(@RequestPart(required = false) MultipartFile corporationXlsx,
                                                   @RequestPart(required = false) MultipartFile corporationPdf,
                                                   @RequestPart PaycheckDto.CorporationRequest paycheckDto) throws IOException {
        adminPaycheckService.postCorporationPaycheck(corporationXlsx, corporationPdf, paycheckDto);
        return ResponseMessage.builder()
                .message("기업 정산 등록에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 조회", description = "기업 정산 조회")
    @GetMapping("/corporations")
    public ResponseMessage getCorporationPaychecks() {
        return ResponseMessage.builder()
                .data(adminPaycheckService.getCorporationPaychecks())
                .message("기업 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 식수 내역 조회", description = "기업 정산 식수 내역 조회")
    @GetMapping("/corporations/{corporationPaycheckId}/orders")
    public ResponseMessage getCorporationOrderHistory(@PathVariable BigInteger corporationPaycheckId) {
        return ResponseMessage.builder()
                .data(adminPaycheckService.getCorporationOrderHistory(corporationPaycheckId))
                .message("기업 정산 식수 내역 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 인보이스 조회", description = "기업 정산 인보이스 조회")
    @GetMapping("/corporations/{corporationPaycheckId}/invoice")
    public ResponseMessage getCorporationInvoice(@PathVariable BigInteger corporationPaycheckId) {
        return ResponseMessage.builder()
                .data(adminPaycheckService.getCorporationOrderHistory(corporationPaycheckId))
                .message("기업 정산 인보이스 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 수정", description = "기업 정산 등록")
    @PatchMapping("/corporations")
    public ResponseMessage updateCorporationPaycheck(@RequestPart(required = false) MultipartFile corporationXlsx,
                                                     @RequestPart(required = false) MultipartFile corporationPdf,
                                                     @RequestPart(required = false) PaycheckDto.CorporationResponse paycheckDto) throws IOException {
        adminPaycheckService.updateCorporationPaycheck(corporationXlsx, corporationPdf, paycheckDto);
        return ResponseMessage.builder()
                .message("기업 정산 수정에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 삭제", description = "기업 정산 삭제")
    @DeleteMapping("/corporations")
    public ResponseMessage deleteCorporationPaycheck(@RequestBody List<BigInteger> ids) {
        adminPaycheckService.deleteCorporationPaycheck(ids);
        return ResponseMessage.builder()
                .message("기업 정산 상태 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 상태 변경", description = "기업 정산 상태 변경")
    @PutMapping("/corporations/status/{status}")
    public ResponseMessage updateCorporationPaycheckStatus(@PathVariable Integer status, @RequestBody List<BigInteger> ids) {
        adminPaycheckService.updateCorporationPaycheckStatus(status, ids);
        return ResponseMessage.builder()
                .message("기업 정산 상태 변경에 성공하였습니다.")
                .build();
    }
}
