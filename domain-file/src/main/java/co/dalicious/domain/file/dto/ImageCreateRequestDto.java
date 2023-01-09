package co.dalicious.domain.file.dto;

import javax.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "이미지 생성 쿼리")
@Getter
@Setter
public class ImageCreateRequestDto {
  @Schema(description = "S3 이미지 location", required = true)
  @NotBlank(message = "location 값이 비어있습니다.")
  private String location;

  @Schema(description = "S3 이미지 key", required = true)
  @NotBlank(message = "key 값이 비어있습니다.")
  private String key;

  @Schema(description = "파일명", required = true)
  @NotBlank(message = "filename 값이 비어있습니다.")
  private String filename;

}
