package co.kurrant.app.public_api.controller.board;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "8. Board")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/boards")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "공지사항 조회", description = "공지사항을 불러온다.")
    @GetMapping("")
    public ResponseMessage noticeList(@RequestParam Integer type){
        return ResponseMessage.builder()
                .data(boardService.noticeList(type))
                .message("공지사항을 불러오는데 성공했습니다.")
                .build();
    }



}
