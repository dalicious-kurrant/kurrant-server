package co.dalicious.domain.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Getter
@NoArgsConstructor
public class SpotListResponseDto {
    private Integer clientType;
    private BigInteger clientId;
    private String clientName;
    private List<Spot> spots;

    @Builder
    public SpotListResponseDto(Integer clientType, BigInteger clientId, String clientName, List<Spot> spots) {
        this.clientType = clientType;
        this.clientId = clientId;
        this.clientName = clientName;
        this.spots = spots;
    }

    @Getter
    @NoArgsConstructor
    public static class Spot {
        private BigInteger spotId;
        private String spotName;

        @Builder
        public Spot(BigInteger spotId, String spotName) {
            this.spotId = spotId;
            this.spotName = spotName;
        }
    }
}
