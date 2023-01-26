package co.kurrant.app.public_api.controller.board;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.BoardService;
import co.kurrant.app.public_api.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "8. Board")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/boards")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "공지사항 조회", description = "공지사항을 불러온다.")
    @GetMapping("notices")
    public ResponseMessage noticeList(@RequestParam Integer type){
        return ResponseMessage.builder()
                .data(boardService.noticeList(type))
                .message("공지사항을 불러오는데 성공했습니다.")
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
        return ResponseMessage.builder()
                .data(boardService.deleteAllAlarm(securityUser))
                .message("알림을 모두 지웠습니다.")
                .build();
    }

}
