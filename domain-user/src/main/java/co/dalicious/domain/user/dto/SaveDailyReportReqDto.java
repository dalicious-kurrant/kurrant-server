package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "조회한 식단을 추가하는 Dto")
public class SaveDailyReportReqDto {

    private BigInteger dailyFoodId;

}
