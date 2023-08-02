package co.dalicious.domain.recommend.dto;

import co.dalicious.system.util.PeriodDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserRecommendByPeriodWhereData {
    private BigInteger userId;
    private BigInteger groupId;
    private Set<BigInteger> foodId;
    private PeriodDto periodDto;

    public UserRecommendByPeriodWhereData(BigInteger userId, BigInteger groupId, Set<BigInteger> foodId, PeriodDto periodDto) {
        this.userId = userId;
        this.groupId = groupId;
        this.foodId = foodId;
        this.periodDto = periodDto;
    }
}
