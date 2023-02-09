package co.kurrant.app.client.dto;

import java.math.BigDecimal;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatsWeeklyDto {
  // YYYY-MM-DD
  private String date;

  // 총 멤버 수
  private Long totalMemberCount;

  // 총 주문자 수
  private Long totalOrdererCount;

  // 총 취소 수
  private Long totalCancelCount;

  // 총 금액
  private Long totalOrderAmount;

  @QueryProjection
  public StatsWeeklyDto(String date, Long totalMemberCount, Long totalOrdererCount,
      Long totalCancelCount, Long totalOrderAmount) {
    this.date = date;
    this.totalMemberCount = totalMemberCount;
    this.totalOrdererCount = totalOrdererCount;
    this.totalCancelCount = totalCancelCount;
    this.totalOrderAmount = totalOrderAmount;
  }

  @QueryProjection
  public StatsWeeklyDto(BigDecimal date, Long totalMemberCount, Long totalOrdererCount) {}
}
