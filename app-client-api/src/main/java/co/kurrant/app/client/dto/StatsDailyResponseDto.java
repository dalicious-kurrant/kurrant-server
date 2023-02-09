package co.kurrant.app.client.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "게시글 상세DTO")
@Setter
@Getter
public class StatsDailyResponseDto {
  private String id;

  private String makersName;

  private String productName;

  private Integer quantity;

  private String ordererName;

  private BigDecimal totalAmount;

  private String createdDateTime;
}
