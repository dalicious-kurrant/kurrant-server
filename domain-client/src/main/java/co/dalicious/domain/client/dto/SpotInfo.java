package co.dalicious.domain.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Builder
public class SpotInfo {
    private BigInteger spotId;
    private String spotName;

    public static SpotInfo create(BigInteger spotId, String spotName) {
        return SpotInfo.builder()
                .spotId(spotId)
                .spotName(spotName)
                .build();
    }
}
