package co.kurrant.app.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import co.kurrant.app.client.dto.ArticleDetailResponseDto;
import co.kurrant.app.client.dto.ArticleListResponseDto;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
  /*
  @Mappings({@Mapping(source = "contentHtml", target = "content")})
  ArticleDetailResponseDto toDetailDto(Article article);

  ArticleListResponseDto toListDto(Article article);
   */
}
