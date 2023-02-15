package co.kurrant.app.client_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "게시글 상세DTO")
@Setter
@Getter
public class ArticleDetailResponseDto {
  @Schema(description = "PK")
  private String id;

  @Schema(description = "카테고리")
  private String category;

  @Schema(description = "제목")
  private String title;

  @Schema(description = "본문내용")
  private String content;

  @Schema(description = "생성일")
  private String createdDateTime;
}
