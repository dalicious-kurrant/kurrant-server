package co.kurrant.app.client_api.service.impl;

import co.kurrant.app.client_api.mapper.ArticleMapper;
import co.kurrant.app.client_api.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

 // private final BoardRepository boardRepository;
  //private final ArticleRepository articleRepository;

  private final ArticleMapper articleMapper;
/*
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
*/
}
