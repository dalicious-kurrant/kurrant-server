package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/qr")
public class QrController {
    @ControllerMarker(ControllerType.HOME)
    @Operation(summary = "", description = "")
    @GetMapping("")
    public ResponseMessage getDashboard() {
        return ResponseMessage.builder()
                .message("QR값 내보내기에 성공하였습니다.")
                .build();
    }
}
