package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.service.GroupRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Group Request")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/groups/requests")
@RestController
public class GroupRequestController {

    private GroupRequestService groupRequestService;

    @Operation(summary = "필터 정보 조회", description = "필터를 위한 정보를 조회합니다.")
    @GetMapping("/filter")
    public ResponseMessage getAllListForFilter(@RequestParam(required = false) Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .message("조회를 성공했습니다.")
                .data(groupRequestService.getAllListForFilter(parameters))
                .build();
    }

}


