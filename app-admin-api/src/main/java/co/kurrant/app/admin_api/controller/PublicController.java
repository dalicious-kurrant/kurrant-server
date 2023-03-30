package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.service.PaycheckService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class PublicController {
    private final PaycheckService paycheckService;
    @Operation(summary = "메이커스 조회", description = "메이커스 조회")
    @GetMapping("/makersInfos")
    public ResponseMessage getMakers() {
        return ResponseMessage.builder()
                .data(paycheckService.getMakers())
                .message("메이커스 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "프라이빗 스팟 조회", description = "프라이빗 스팟 조회")
    @GetMapping("/corporationInfos")
    public ResponseMessage getCorporations() {
        return ResponseMessage.builder()
                .data(paycheckService.getCorporations())
                .message("프라이빗 스팟 조회에 성공하였습니다.")
                .build();
    }
}
