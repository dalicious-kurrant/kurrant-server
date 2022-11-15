package io.corretto.domain.file.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "이미지 업로드 경로 요청 DTO")
@Getter
public class RequestImageUploadUrlRequestDto {
  @Schema(description = "파일 경로", required = true)
  @NotBlank()
  private String filename;

  @Schema(description = "파일 인덱싱 갯수, 1이면 1개를 준다.", required = true)
  @Min(1)
  @NotNull()
  private Integer parts;
}
