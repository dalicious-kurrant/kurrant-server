package co.dalicious.domain.user.repository.union;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CustomPointHistory {
    private LocalDate rewardDate;
    private BigDecimal point;
    @Schema(description = "포인트 사용 - 1. 적립, 2. 사용")
    private Integer pointStatus;
    private BigInteger reviewId;
    private BigInteger orderId;
    private BigInteger boardId;
}
