package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.service.GroupService;
import co.kurrant.app.admin_api.service.AdminPaycheckService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class PublicController {
    private final AdminPaycheckService adminPaycheckService;
    private final GroupService groupService;
    @Operation(summary = "메이커스 조회", description = "메이커스 조회")
    @GetMapping("/makersInfos")
    public ResponseMessage getMakers() {
        return ResponseMessage.builder()
                .data(adminPaycheckService.getMakers())
                .message("메이커스 정산 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "상세스팟 조회", description = "프라이빗 스팟 조회")
    @GetMapping("/groups/{groupId}/spots")
    public ResponseMessage getSpotDetail(@PathVariable BigInteger groupId) {
        return ResponseMessage.builder()
                .data(groupService.getSpots(groupId))
                .message("상세스팟 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "프라이빗 스팟 조회", description = "프라이빗 스팟 조회")
    @GetMapping("/corporationInfos")
    public ResponseMessage getCorporations() {
        return ResponseMessage.builder()
                .data(adminPaycheckService.getCorporations())
                .message("프라이빗 스팟 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "스파크플러스 로그 추가", description = "스파크플러스 로그 추가")
    @GetMapping("/sparkplus/logs/{log}")
    public ResponseMessage postSparkplusLog(@PathVariable Integer log) {
        adminPaycheckService.postSparkplusLog(log);
        return ResponseMessage.builder()
                .message("스파크플러스 로그 저장에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "스파크플러스 로그 조회", description = "스파크플러스 로그 조회")
    @GetMapping("/sparkplus/logs")
    public ResponseMessage getSparkplusLog() {

        return ResponseMessage.builder()
                .data(adminPaycheckService.getSpartplusLog())
                .message("스파크플러스 로그 조회에 성공하였습니다.")
                .build();
    }
}
