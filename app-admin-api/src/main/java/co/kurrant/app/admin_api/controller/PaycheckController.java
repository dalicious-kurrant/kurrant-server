package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.kurrant.app.admin_api.service.PaycheckService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class PaycheckController {
    private final PaycheckService paycheckService;
    @Operation(summary = "메이커스 정산 등록", description = "메이커스 정산 등록")
    @PostMapping("/paycheck")
    public ResponseMessage postPaycheck(Authentication authentication,
                                        @RequestPart MultipartFile makersXlsx,
                                        @RequestPart MultipartFile makersPdf,
                                        @RequestPart PaycheckDto.MakersRequest paycheckDto) {
        return ResponseMessage.builder()
                .message("정산 등록에 성공하였습니다.")
                .build();
    }
}
