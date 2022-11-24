package co.kurrant.app.admin_api.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import co.dalicious.domain.file.dto.ImageCreateRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import co.dalicious.domain.banner.enums.BannerSection;
import co.dalicious.domain.banner.enums.BannerType;

@Schema(description = "배너 수정 쿼리")
@ToString
@Getter
@NoArgsConstructor
public class BannerUpdateRequestDto {
  @Schema(description = "type", required = false)
  @NotNull
  private BannerType type;

  @Schema(description = "moveTo", required = false)
  @NotBlank
  private String moveTo;

  @Schema(description = "배너 구역", required = true)
  @NotNull
  private BannerSection section;

  @Schema(description = "S3 이미지 location", required = false)
  @Valid
  private ImageCreateRequestDto imageCreateRequestDto;

}
