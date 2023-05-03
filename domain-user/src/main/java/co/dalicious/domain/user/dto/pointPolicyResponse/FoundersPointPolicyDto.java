package co.dalicious.domain.user.dto.pointPolicyResponse;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FoundersPointPolicyDto {

    private String value;
    private BigDecimal minPoint;
    private BigDecimal maxPoint;
}
