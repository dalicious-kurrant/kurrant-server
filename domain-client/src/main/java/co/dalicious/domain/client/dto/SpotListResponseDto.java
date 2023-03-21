package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "스팟 정보 응답 DTO")
public class SpotListResponseDto {
    private BigInteger clientId;
    private String clientName;
    private List<Spot> spots;
    private Integer spotType;

    @Builder
    public SpotListResponseDto(BigInteger clientId, String clientName, List<Spot> spots, Integer spotType) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.spots = spots;
        this.spotType = spotType;
    }

    @Getter
    @Setter
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
