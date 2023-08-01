package co.kurrant.app.client_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.BoardService;
import co.kurrant.app.client_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/board")
@RestController
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "고객사 공지사항 조회", description = "고객사 공지사항을 조회한다")
    @GetMapping("")
    public ResponseMessage getClientBoard(Authentication authentication, @RequestParam Integer type,
                                          @RequestParam(required = false, defaultValue = "15") Integer limit,
                                          @RequestParam Integer page) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(((long) limit * (page - 1)), limit, Sort.unsorted());
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("고객사 공지사항 조회에 성공하였습니다.")
                .data(boardService.getClientBoard(securityUser, type, pageable))
                .build();
    }
}

