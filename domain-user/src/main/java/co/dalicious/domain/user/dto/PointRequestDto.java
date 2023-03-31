package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
public class PointRequestDto {
    private String rewardDate;
    private String makersName;
    private String name;
    private BigDecimal point;
    private Integer pointStatus;
    private BigDecimal leftPoint;
    private BigInteger contentId;
}
