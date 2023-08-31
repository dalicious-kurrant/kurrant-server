package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.data.redis.event.ReloadEvent;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestAtHomepageDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestAtHomepageDto;
import co.dalicious.domain.order.dto.OrderDto;
import co.kurrant.app.admin_api.service.ApplicationFormAtHomepageService;
import co.kurrant.app.admin_api.service.GroupService;
import co.kurrant.app.admin_api.service.AdminPaycheckService;
import co.kurrant.app.admin_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class PublicController {
    private final AdminPaycheckService adminPaycheckService;
    private final GroupService groupService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserService userService;
    private final ApplicationFormAtHomepageService applicationFormAtHomepageService;

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
    public ResponseMessage subscribe(@RequestBody OrderDto.IdList idList) {
        applicationEventPublisher.publishEvent(new ReloadEvent(idList.getIdList()));
        return ResponseMessage.builder()
                .message("메세지 전송 성공")
                .build();
    }

    @ControllerMarker(ControllerType.PUBLIC)
    @Operation(summary = "유저 조회", description = "유저 조회")
    @GetMapping("/users")
    public ResponseMessage getUserInfo() {
        return ResponseMessage.builder()
                .data(userService.getUserInfos())
                .message("유저 조회에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.PUBLIC)
    @Operation(summary = "고객사 신청 생성 - 홈페이지", description = "홈페이지에서 고객사 신청을 추가합니다.")
    @PostMapping("/homepage/application-form/corporation")
    public ResponseMessage createCorporationRequestAtHomepage(@RequestBody CorporationRequestAtHomepageDto request) {
        applicationFormAtHomepageService.createCorporationRequestAtHomepage(request);
        return ResponseMessage.builder()
                .message("홈페이지에서 고객사 신청을 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.PUBLIC)
    @Operation(summary = "메이커스 신청 생성 - 홈페이지", description = "홈페이지에서 메이커스 신청을 추가합니다.")
    @PostMapping("/homepage/application-form/makers")
    public ResponseMessage createMakersRequestAtHomepage(@Valid @RequestBody MakersRequestAtHomepageDto request) {
        applicationFormAtHomepageService.createMakersRequestAtHomepage(request);
        return ResponseMessage.builder()
                .message("홈페이지에서 메이커스 신청을 성공했습니다.")
                .build();
    }
}
