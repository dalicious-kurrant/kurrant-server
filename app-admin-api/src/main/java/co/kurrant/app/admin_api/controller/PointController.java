package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.domain.user.dto.PointPolicyReqDto;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;

@Tag(name = "Point")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/points")
@RestController
public class PointController {

    private final PointService pointService;

    @ControllerMarker(ControllerType.POINT)
    @Operation(summary = "포인트 정책 조회", description = "리뷰 포인트 정책을 조회합니다.")
    @GetMapping("/policy/review")
    public ResponseMessage findReviewPointPolicy() {
        return ResponseMessage.builder()
                .data(pointService.findReviewPointPolicy())
                .message("리뷰 포인트 정책 조회에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.POINT)
    @Operation(summary = "포인트 정책 조회", description = "이벤트 포인트 정책을 조회합니다.")
    @GetMapping("/policy/event")
    public ResponseMessage findEventPointPolicy() {
        return ResponseMessage.builder()
                .data(pointService.findEventPointPolicy())
                .message("이벤트 포인트 정책 조회에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.POINT)
    @Operation(summary = "포인트 정책 생성", description = "이벤트 포인트 정책을 생성합니다.")
    @PostMapping("/policy/event")
    public ResponseMessage createReviewPointPolicy(@RequestBody PointPolicyReqDto.EventPointPolicy requestDto) {
        pointService.createReviewPointPolicy(requestDto);
        return ResponseMessage.builder()
                .message("이벤트 포인트 정책 생성에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.POINT)
    @Operation(summary = "포인트 정책 수정", description = "이벤트 포인트 정책을 수정합니다.")
    @PatchMapping("/policy/event")
    public ResponseMessage updateEventPointPolicy(@RequestParam BigInteger policyId, @RequestBody PointPolicyReqDto.EventPointPolicy requestDto) {
        pointService.updateEventPointPolicy(policyId, requestDto);
        return ResponseMessage.builder()
                .message("이벤트 포인트 정책 수정에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.POINT)
    @Operation(summary = "포인트 정책 삭제", description = "이벤트 포인트 정책을 삭제합니다..")
    @DeleteMapping("/policy/event")
    public ResponseMessage deleteEventPointPolicy(@RequestParam BigInteger policyId) {
        pointService.deleteEventPointPolicy(policyId);
        return ResponseMessage.builder()
                .message("이벤트 포인트 정책 삭제에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.POINT)
    @Operation(summary = "포인트 직접 적립", description = "운영자가 유저에게 포인트를 직접 적립합니다.")
    @PostMapping("/user")
    public ResponseMessage addPointsToUser(@RequestBody PointPolicyReqDto.AddPointToUser requestDto) {
        pointService.addPointsToUser(requestDto);
        return ResponseMessage.builder()
                .message("유저의 포인트 적립에 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.POINT)
    @Operation(summary = "포인트 정책 조회", description = "파운더스 포인트 정책을 조회합니다.")
    @GetMapping("/policy/founders")
    public ResponseMessage findFoundersPointPolicy() {
        return ResponseMessage.builder()
                .data(pointService.findFoundersPointPolicy())
                .message("파운더스 포인트 정책 조회에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.POINT)
    @Operation(summary = "지난 파운더스 포인트 적립", description = "지난 파운더스 포인트를 적립합니다.")
    @PatchMapping("/founders")
    public ResponseMessage updateFoundersPoint() {
//        LocalDate date = DateUtils.stringToDate(selectDate);
        return ResponseMessage.builder()
                .data(pointService.AccumulatedFoundersPointSave(LocalDate.now(ZoneId.of("Asia/Seoul"))))
                .message("지난 파운더스 포인트 적립에 성공하였습니다.")
                .build();
    }
}
