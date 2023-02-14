package co.kurrant.app.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "일간통계 SUMMARY")
@Getter
@Builder
public class StatsDailySummaryResponseDto {
  @Schema(description = "총 구매 유저수")
  private Long totalOrderUserCount;

  @Schema(description = "총 식사 개수")
  private Long totalOrderItemCount;

  @Schema(description = "총 사용 금액")
  private Long totalOrderAmount;
}
