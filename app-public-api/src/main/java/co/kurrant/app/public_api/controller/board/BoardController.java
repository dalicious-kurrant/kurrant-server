package co.kurrant.app.public_api.controller.board;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.BoardService;
import co.kurrant.app.public_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Tag(name = "게시판")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/boards")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "팝업 공지사항 조회", description = "팝업 공지사항을 불러온다.")
    @GetMapping("notices/popup")
    public ResponseMessage popupNoticeList(Authentication authentication){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(boardService.popupNoticeList(securityUser))
                .message("팝업 공지사항을 불러오는데 성공했습니다.")
                .build();
    }

    @Operation(summary = "고객센터 조회", description = "고객센터 페이지 조회")
    @GetMapping("customers")
    public ResponseMessage customerList(){
        return ResponseMessage.builder()
                .data(boardService.customerBoardList())
                .message("고객센터 페이지를 불러오는데 성공했습니다.")
                .build();
    }

    @Operation(summary = "알림센터 조회", description = "알림센터 조회")
    @GetMapping("alarms")
    public ResponseMessage alarmList(Authentication authentication){
        SecurityUser securityUser =  UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(boardService.alarmBoardList(securityUser))
                .message("알람을 불러오는데 성공했습니다.")
                .build();
    }

    @Operation(summary = "모든 알림 삭제", description = "모든 알림 삭제")
    @DeleteMapping("alarms")
    public ResponseMessage deleteAllAlarm(Authentication authentication){
        SecurityUser securityUser =  UserUtil.securityUser(authentication);
        boardService.deleteAllAlarm(securityUser);
        return ResponseMessage.builder()
                .message("알림을 모두 지웠습니다.")
                .build();
    }

    @Operation(summary = "모든 알림 읽기", description = "모든 알림 읽기")
    @PatchMapping("alarms")
    public ResponseMessage readAllAlarm(Authentication authentication, @RequestBody List<String> ids){
        SecurityUser securityUser =  UserUtil.securityUser(authentication);
        boardService.readAllAlarm(securityUser, ids);
        return ResponseMessage.builder()
                .message("알림을 모두 읽었습니다.")
                .build();
    }

    @Operation(summary = "공지사항 조회", description = "공지사항을 불러온다.")
    @GetMapping("notices")
    public ResponseMessage noticeList(Authentication authentication, @RequestParam(required = false) BigInteger groupId,
                                      @RequestParam(required = false, defaultValue = "15") Integer limit,
                                      @RequestParam Integer page) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(((long) limit * (page - 1)), limit, Sort.unsorted());
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(boardService.noticeList(securityUser, groupId, pageable))
                .message("공지사항을 불러오는데 성공했습니다.")
                .build();
    }

}
