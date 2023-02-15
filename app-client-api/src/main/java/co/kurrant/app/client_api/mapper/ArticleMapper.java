package co.kurrant.app.client_api.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
  /*
  @Mappings({@Mapping(source = "contentHtml", target = "content")})
  ArticleDetailResponseDto toDetailDto(Article article);

  ArticleListResponseDto toListDto(Article article);
   */
}
