package co.kurrant.app.client.service.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.board.entity.Article;
import co.dalicious.domain.board.entity.Board;
import co.kurrant.app.client.dto.ArticleDetailResponseDto;
import co.kurrant.app.client.dto.ArticleListRequestDto;
import co.kurrant.app.client.dto.ArticleListResponseDto;
import co.kurrant.app.client.mapper.ArticleMapper;
import co.kurrant.app.client.repository.ArticleRepository;
import co.kurrant.app.client.repository.BoardRepository;
import co.kurrant.app.client.service.BoardService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

  private final BoardRepository boardRepository;
  private final ArticleRepository articleRepository;

  private final ArticleMapper articleMapper;

  @Override
  public ListItemResponseDto<ArticleListResponseDto> findAll(ArticleListRequestDto query,
      String boardName, Pageable pageable) {

    Board foundBoard = boardRepository.findOneByName(boardName)
        .orElseThrow(() -> new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND));

    Page<Article> foundArticles = articleRepository.findAllByBoardId(foundBoard.getId(), pageable);

    List<ArticleListResponseDto> items = foundArticles.get()
        .map((foundArticle) -> articleMapper.toListDto(foundArticle)).collect(Collectors.toList());

    return ListItemResponseDto.<ArticleListResponseDto>builder().items(items)
        .total(foundArticles.getTotalElements()).count(foundArticles.getNumberOfElements())
        .limit(pageable.getPageSize()).offset(pageable.getOffset()).build();
  }

  @Override
  public ArticleDetailResponseDto getOne(String boardName, BigInteger articleId) {
    Board foundBoard = boardRepository.findOneByName(boardName)
        .orElseThrow(() -> new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND));

    Article foundArticle = articleRepository.findOneById(articleId)
        .orElseThrow(() -> new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND));

    return articleMapper.toDetailDto(foundArticle);
  }

}
