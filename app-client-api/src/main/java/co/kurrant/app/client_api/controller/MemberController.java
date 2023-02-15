package co.kurrant.app.client_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.kurrant.app.client_api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins="*", allowedHeaders = "*")
@Tag(name = "1. Member")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/v1/client/members")
@RestController
public class MemberController {

  private final MemberService memberService;

  @Operation(summary = "유저조회", description = "유저 목록을 조회한다.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("")
  public ResponseMessage getUserList(@RequestParam String code, @PageableDefault(size = 20, sort = "id",
                                                                     direction = Sort.Direction.DESC) OffsetBasedPageRequest pageable) {
    return ResponseMessage.builder()
            .data(memberService.getUserList(code, pageable))
            .message("유저 목록 조회")
            .build();
  }


  @Operation(summary = "가입 대기 유저조회", description = "가입 대기 유저 목록을 조회한다.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/waiting")
  public ResponseMessage getWaitingUserList(@RequestParam String code, @PageableDefault(size = 20, sort = "id",
          direction = Sort.Direction.DESC) OffsetBasedPageRequest pageable){
    return ResponseMessage.builder()
            .data(memberService.getWaitingUserList(code, pageable))
            .message("가입대기 유저 목록 조회")
            .build();
  }

  @Operation(summary = "엑셀 불러오기", description = "엑셀 파일을 불러온다.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/waiting/excel")
  public ResponseMessage importExcelForWaitingUserList(@RequestParam("file") MultipartFile file) throws IOException {
    return ResponseMessage.builder()
            .data(memberService.importExcelForWaitingUserList(file))
            .message("가입대기 유저 목록 조회")
            .build();
  }

  @Operation(summary = "선택 유저 탈퇴처리", description = "선택한 유저를 탈퇴처리한다")
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("")
  public ResponseMessage deleteMember(@RequestBody DeleteMemberRequestDto deleteMemberRequestDto){
    memberService.deleteMember(deleteMemberRequestDto);
    return ResponseMessage.builder()
            .message("선택한 유저를 탈퇴처리했습니다.")
            .build();
  }


 /*
  @Operation(summary = "목록조회", description = "배너 목록을 조회한다.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{boardName}/articles")
  public ListItemResponseDto<ArticleListResponseDto> getList(HttpServletRequest request,
      @PathVariable String boardName,
      @Parameter(name = "쿼리정보", description = "",
          required = false) @RequestParam Map<String, String> params,
      @PageableDefault(size = 20, sort = "createdDateTime",
          direction = Direction.DESC) OffsetBasedPageRequest pageable) {

    JsonMapper mapper = JsonMapper.builder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();
    ArticleListRequestDto queryDto = mapper.convertValue(params, ArticleListRequestDto.class);

    return boardService.findAll(queryDto, boardName, pageable);
  }

  @Operation(summary = "상세조회", description = "배너 상세조회를 수행한다.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{boardName}/articles/{articleId}")
  public ArticleDetailResponseDto getOne(HttpServletRequest request, @PathVariable String boardName,
      @PathVariable BigInteger articleId) {

    return boardService.getOne(boardName, articleId);
  }

  */
}
