package co.dalicious.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class SelectUserSpotResDto {
    private BigInteger spotId;
    private Integer clientType;
    private Boolean hasHo;

    @Builder
    public SelectUserSpotResDto(BigInteger spotId, Integer clientType, Boolean hasHo) {
        this.spotId = spotId;
        this.clientType = clientType;
        this.hasHo = hasHo;
    }
}
