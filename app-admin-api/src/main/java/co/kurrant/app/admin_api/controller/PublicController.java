package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.data.redis.pubsub.EventPublisher;
import co.dalicious.domain.file.entity.embeddable.enums.DirName;
import co.dalicious.domain.file.service.ImageService;
import co.dalicious.domain.order.dto.OrderDto;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.service.GroupService;
import co.kurrant.app.admin_api.service.AdminPaycheckService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class PublicController {
    private final AdminPaycheckService adminPaycheckService;
    private final GroupService groupService;
    private final EventPublisher eventPublisher;

    @ControllerMarker(ControllerType.PUBLIC)
    @Operation(summary = "메이커스 조회", description = "메이커스 조회")
    @GetMapping("/makersInfos")
    public ResponseMessage getMakers() {
        return ResponseMessage.builder()
                .data(adminPaycheckService.getMakers())
                .message("메이커스 정산 조회에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.PUBLIC)
    @Operation(summary = "상세스팟 조회", description = "프라이빗 스팟 조회")
    @GetMapping("/groups/{groupId}/spots")
    public ResponseMessage getSpotDetail(@PathVariable BigInteger groupId) {
        return ResponseMessage.builder()
                .data(groupService.getSpots(groupId))
                .message("상세스팟 조회에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.PUBLIC)
    @Operation(summary = "프라이빗 스팟 조회", description = "프라이빗 스팟 조회")
    @GetMapping("/corporationInfos")
    public ResponseMessage getCorporations() {
        return ResponseMessage.builder()
                .data(adminPaycheckService.getCorporations())
                .message("프라이빗 스팟 조회에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.PUBLIC)
    @Operation(summary = "스파크플러스 로그 추가", description = "스파크플러스 로그 추가")
    @GetMapping("/sparkplus/logs/{log}")
    public ResponseMessage postSparkplusLog(@PathVariable Integer log) {
        adminPaycheckService.postSparkplusLog(log);
        return ResponseMessage.builder()
                .message("스파크플러스 로그 저장에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.PUBLIC)
    @Operation(summary = "스파크플러스 로그 조회", description = "스파크플러스 로그 조회")
    @GetMapping("/sparkplus/logs")
    public ResponseMessage getSparkplusLog() {

        return ResponseMessage.builder()
                .data(adminPaycheckService.getSpartplusLog())
                .message("스파크플러스 로그 조회에 성공하였습니다.")
                .build();
    }

    @Description(value = "메세지 전송")
    @PostMapping(value = "/notification/send")
    public void subscribe(Authentication authentication,
                          @RequestBody OrderDto.IdList idList) {
        eventPublisher.publishEvent(StringUtils.BigIntegerListToString(idList.getIdList()));
    }
}
