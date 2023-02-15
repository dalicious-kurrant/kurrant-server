package co.kurrant.app.client_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "게시글 상세DTO")
@Setter
@Getter
public class ArticleListResponseDto {
  @Schema(description = "게시글 ID")
  public String id;

  @Schema(description = "카테고리")
  public String category;

  @Schema(description = "제목")
  public String title;

  @Schema(description = "생성일")
  private String createdDateTime;
}
