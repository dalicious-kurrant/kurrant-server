package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.kurrant.app.admin_api.service.PaycheckService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/paycheck")
public class PaycheckController {
    private final PaycheckService paycheckService;

    @Operation(summary = "메이커스 정산 등록", description = "메이커스 정산 등록")
    @PostMapping("/makers")
    public ResponseMessage postMakersPaycheck(@RequestPart(required = false) MultipartFile makersXlsx,
                                              @RequestPart(required = false) MultipartFile makersPdf,
                                              @RequestPart PaycheckDto.MakersRequest paycheckDto) throws IOException {
        paycheckService.postMakersPaycheck(makersXlsx, makersPdf, paycheckDto);
        return ResponseMessage.builder()
                .message("메이커스 정산 등록에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "메이커스 정산 조회", description = "메이커스 정산 조회")
    @GetMapping("/makers")
    public ResponseMessage getMakersPaychecks() {
        return ResponseMessage.builder()
                .data(paycheckService.getMakersPaychecks())
                .message("메이커스 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "메이커스 정산 수정", description = "메이커스 정산 등록")
    @PatchMapping("/makers")
    public ResponseMessage updateMakersPaycheck(@RequestPart(required = false) MultipartFile makersXlsx,
                                                @RequestPart(required = false) MultipartFile makersPdf,
                                                @RequestPart PaycheckDto.MakersResponse paycheckDto) throws IOException {
        paycheckService.updateMakersPaycheck(makersXlsx, makersPdf, paycheckDto);
        return ResponseMessage.builder()
                .message("메이커스 정산 수정에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "메이커스 정산 상태 변경", description = "메이커스 정산 상태 변경")
    @PutMapping("/makers/status/{status}")
    public ResponseMessage updateMakersPaycheckStatus(@PathVariable Integer status, @RequestBody List<BigInteger> ids) {
        paycheckService.updateMakersPaycheckStatus(status, ids);
        return ResponseMessage.builder()
                .message("메이커스 정산 상태 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 등록", description = "기업 정산 등록")
    @PostMapping("/corporations")
    public ResponseMessage postCorporationPaycheck(@RequestPart(required = false) MultipartFile corporationXlsx,
                                                   @RequestPart(required = false) MultipartFile corporationPdf,
                                                   @RequestPart PaycheckDto.CorporationRequest paycheckDto) throws IOException {
        paycheckService.postCorporationPaycheck(corporationXlsx, corporationPdf, paycheckDto);
        return ResponseMessage.builder()
                .message("기업 정산 등록에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 조회", description = "기업 정산 조회")
    @GetMapping("/corporations")
    public ResponseMessage getCorporationPaychecks() {
        return ResponseMessage.builder()
                .data(paycheckService.getCorporationPaychecks())
                .message("기업 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 수정", description = "기업 정산 등록")
    @PatchMapping("/corporations")
    public ResponseMessage updateCorporationPaycheck(@RequestPart(required = false) MultipartFile corporationXlsx,
                                                @RequestPart(required = false) MultipartFile corporationPdf,
                                                @RequestPart(required = false) PaycheckDto.CorporationResponse paycheckDto) throws IOException {
        paycheckService.updateCorporationPaycheck(corporationXlsx, corporationPdf, paycheckDto);
        return ResponseMessage.builder()
                .message("기업 정산 수정에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "기업 정산 상태 변경", description = "기업 정산 상태 변경")
    @PutMapping("/corporations/status/{status}")
    public ResponseMessage updateCorporationPaycheckStatus(@PathVariable Integer status, @RequestBody List<BigInteger> ids) {
        paycheckService.updateCorporationPaycheckStatus(status, ids);
        return ResponseMessage.builder()
                .message("기업 정산 상태 변경에 성공하였습니다.")
                .build();
    }
}