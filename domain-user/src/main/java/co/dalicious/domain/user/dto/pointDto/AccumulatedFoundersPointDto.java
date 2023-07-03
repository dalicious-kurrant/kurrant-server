package co.dalicious.domain.user.dto.pointDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
public class AccumulatedFoundersPointDto {
    private BigInteger userId;
    private String userName;
    private String foundersStartDate;
    private int count;
    private BigDecimal point;
    private BigDecimal totalPoint;

    @Builder
    public AccumulatedFoundersPointDto(BigInteger userId, String userName, String foundersStartDate, int count, BigDecimal point, BigDecimal totalPoint) {
        this.userId = userId;
        this.userName = userName;
        this.foundersStartDate = foundersStartDate;
        this.count = count;
        this.point = point;
        this.totalPoint = totalPoint;
    }

}
