package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.domain.board.dto.AppBoardRequestDto;
import co.kurrant.app.admin_api.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/board")
public class BoardController {
    private final BoardService boardService;
    @ControllerMarker(ControllerType.BOARD)
    @Operation(summary = "앱 공지사항 등록", description = "앱 공지사항을 등록한다.")
    @PostMapping("/app")
    public ResponseMessage postAppBoard(@RequestBody AppBoardRequestDto request) {
        boardService.createAppBoard(request);
        return ResponseMessage.builder()
                .message("공지사항 등록에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.BOARD)
    @Operation(summary = "앱 공지사항 조회", description = "앱 공지사항을 조회한다")
    @GetMapping("/app")
    public ResponseMessage getAppBoard(@RequestParam(required = false) Map<String, Object> parameters,
                                       @RequestParam(required = false, defaultValue = "15") Integer limit,
                                       @RequestParam Integer page) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(((long) limit * (page - 1)), limit, Sort.unsorted());
        return ResponseMessage.builder()
                .message("공지사항 조회에 성공하였습니다.")
                .data(boardService.getAppBoard(parameters, pageable))
                .build();
    }

    @ControllerMarker(ControllerType.BOARD)
    @Operation(summary = "앱 공지사항 수정", description = "앱 공지사항을 수정한다.")
    @PatchMapping("/app/{noticeId}")
    public ResponseMessage updateAppBoard(@PathVariable BigInteger noticeId, @RequestBody AppBoardRequestDto request) {
        boardService.updateAppBoard(noticeId, request);
        return ResponseMessage.builder()
                .message("앱 공지사항 수정에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.BOARD)
    @Operation(summary = "앱 공지사항 푸시알림", description = "앱 공지사항 푸시알림을 전송합니다.")
    @PostMapping("/app/push")
    public ResponseMessage postPushAlarm(@RequestBody BigInteger noticeId) {
        boardService.postPushAlarm(noticeId);
        return ResponseMessage.builder()
                .message("앱 공지사항 푸시알림 전송에 성공했습니다.")
                .build();
    }

}
