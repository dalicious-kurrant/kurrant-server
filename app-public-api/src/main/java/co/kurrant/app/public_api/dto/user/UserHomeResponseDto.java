package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Schema(description = "대시보드에서 필요한 유저 정보를 가져온다.")
@Getter
@NoArgsConstructor
public class UserHomeResponseDto {
    private String name;
    private Boolean isMembership;
    private BigInteger spotId;
    private String spot;
    private BigDecimal point;

    @Builder
    public UserHomeResponseDto(String name, Boolean isMembership, BigInteger spotId, String spot, BigDecimal point) {
        this.name = name;
        this.isMembership = isMembership;
        this.spotId = spotId;
        this.spot = spot;
        this.point = point;
    }
}
