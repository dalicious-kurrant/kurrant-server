package co.kurrant.app.client_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "게시글 상세DTO")
@Setter
@Getter
public class StatsWeeklyResponseDto {
  @Schema(description = "YYYY-MM-DD")
  private String date;

  @Schema(description = "총 멤버 수")
  private Long totalMemberCount;

  @Schema(description = "총 주문자 수")
  private Long totalOrdererCount;

  @Schema(description = "총 취소 수")
  private Long totalCancelCount;

  @Schema(description = "총 금액")
  private Long totalOrderAmount;
}
