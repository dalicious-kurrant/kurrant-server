package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HomeController {
    @ControllerMarker(ControllerType.GROUP)
    @Operation(summary = "대시보드 조회", description = "오늘, 이번주, 이번달 식수 조회 및 메이커스와 고객사 판매량을 조회한다.")
    @GetMapping("")
    public ResponseMessage getDashboard(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .message("대시보드 조회에 성공하였습니다.")
                .build();
    }
}
