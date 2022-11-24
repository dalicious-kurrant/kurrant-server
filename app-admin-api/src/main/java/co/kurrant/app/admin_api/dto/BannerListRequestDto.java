package co.kurrant.app.admin_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "배너 목록 조회 시 쿼리스트링")
@Setter
@Getter
public class BannerListRequestDto {
  @Schema(description = "유형", required = true)
  private String type;
}
