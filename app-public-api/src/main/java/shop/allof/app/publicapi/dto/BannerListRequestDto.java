package shop.allof.app.publicapi.dto;

import io.corretto.client.core.annotation.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import shop.allof.domain.banner.enums.BannerType;

@Schema(description = "배너 목록 조회 시 쿼리스트링")
@Setter
@Getter
public class BannerListRequestDto {
  @Schema(description = "유형", required = true)
  @ValidEnum(enumClass = BannerType.class)
  private BannerType type;
}
