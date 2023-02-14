package co.kurrant.app.client.controller;

import java.math.BigInteger;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.kurrant.app.client.dto.ArticleDetailResponseDto;
import co.kurrant.app.client.dto.ArticleListRequestDto;
import co.kurrant.app.client.dto.ArticleListResponseDto;
import co.kurrant.app.client.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "BOARD")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/v1/boards")
@RestController
public class BoardController {

  private final BoardService boardService;
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
