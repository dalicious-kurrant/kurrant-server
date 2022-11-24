package co.kurrant.app.admin_api.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import co.dalicious.domain.file.dto.ImageCreateRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import co.dalicious.domain.banner.enums.BannerSection;
import co.dalicious.domain.banner.enums.BannerType;

@Schema(description = "배너 생성 쿼리")
@Getter
@NoArgsConstructor
public class BannerCreateRequestDto {

  @Schema(description = "리다이렉트 경로")
  @NotBlank
  private String moveTo;

  @Schema(description = "배너 타입", required = true)
  @NotNull
  private BannerType type;

  @Schema(description = "배너 구역", required = true)
  @NotNull
  private BannerSection section;

  @Schema(description = "S3 이미지 location", required = true)
  @Valid
  private ImageCreateRequestDto image;

}
