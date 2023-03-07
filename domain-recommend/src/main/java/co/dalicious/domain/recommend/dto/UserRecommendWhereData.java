package co.dalicious.domain.recommend.dto;

import co.dalicious.system.enums.DiningType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserRecommendWhereData {
    private BigInteger userId;
    private BigInteger groupId;
    private List<BigInteger> foodId;
    private LocalDate serviceDate;

    public static UserRecommendWhereData createUserRecommendWhereData(BigInteger userId, BigInteger groupId, List<BigInteger> foodIds, LocalDate serviceDate) {
        return UserRecommendWhereData.builder()
                .userId(userId)
                .groupId(groupId)
                .foodId(foodIds)
                .serviceDate(serviceDate)
                .build();
    }
}
