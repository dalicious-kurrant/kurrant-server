package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "식단 리포트에 주문한 음식을 추가 요청하는 Dto")
public class SaveDailyReportFoodReqDto {
    private BigInteger userId;
    private String startDate;
    private String endDate;
}
