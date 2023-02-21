package co.kurrant.app.admin_api.controller.client;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.service.SpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "3.Client")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/clients")
@RestController
public class ClientController {

    private final SpotService spotService;

    @Operation(summary = "스팟정보 전체 조회", description = "존재하는 스팟을 모두 조회합니다.")
    @GetMapping("/all")
    public ResponseMessage getAllSpotList() {
        return ResponseMessage.builder()
                .message("모든 스팟을 조회했습니다.")
                .data(spotService.getAllSpotList())
                .build();
    }
}
