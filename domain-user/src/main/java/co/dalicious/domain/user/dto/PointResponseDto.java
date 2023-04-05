package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class PointResponseDto {
    private BigDecimal userPoint;
    private List<PointHistoryDto> pointHistoryDtos;

    @Getter
    @Setter
    public static class PointHistoryDto {
        private String rewardDate;
        private String makersName;
        private String name;
        private BigDecimal point;
        private Integer pointStatus;
        private BigDecimal leftPoint;
        private BigInteger contentId;
    }


    public static PointResponseDto create(BigDecimal userPoint, List<PointHistoryDto> pointHistoryDtos) {
        return PointResponseDto.builder()
                .userPoint(userPoint)
                .pointHistoryDtos(pointHistoryDtos)
                .build();
    }
}
