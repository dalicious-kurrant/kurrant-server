package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.kurrant.app.admin_api.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/logs")
public class LogController {
    private final LogService logService;
    @ControllerMarker(ControllerType.LOGS)
    @GetMapping("")
    public ResponseMessage getLogs(@RequestParam Map<String, Object> parameters, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("로그 조회에 성공하였습니다.")
                .data(logService.getLogs(parameters, pageable))
                .build();
    }

    @ControllerMarker(ControllerType.LOGS)
    @GetMapping("/devices")
    public ResponseMessage getDevices() {
        return ResponseMessage.builder()
                .message("기기 조회에 성공하였습니다.")
                .data(logService.getDevices())
                .build();
    }
}
