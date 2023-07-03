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

    @Getter
    @Setter
    public static class Spot {
        private BigInteger spotId;
        private String spotName;
        private Boolean isRestriction;

        @Builder
        public Spot(BigInteger spotId, String spotName, Boolean isRestriction) {
            this.spotId = spotId;
            this.spotName = spotName;
            this.isRestriction = isRestriction;
        }
    }
}
