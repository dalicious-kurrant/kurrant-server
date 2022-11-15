package shop.allof.app.adminapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "배너 목록 조회 응답 값")
@Builder
@Getter
public class BannerListResponseDto {

  @Schema(description = "배너 ID")
  private String id;

  @Schema(description = "배너 타입")
  private String type;

  @Schema(description = "배너 구역")
  private String section;

  @Schema(description = "배너 이미지 주소")
  private String location;

  @Schema(description = "배너 클릭시 이동되는 주소")
  private String moveTo;

}
