package co.dalicious.domain.recommend.dto;

import co.dalicious.domain.recommend.entity.GroupRecommends;
import co.dalicious.system.enums.DiningType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor
public class RecommendScheduleDto {
    private LocalDate serviceDate;
    private BigInteger makersId;
    private DiningType diningType;

    public static RecommendScheduleDto createDto(GroupRecommends data) {
        return RecommendScheduleDto.builder()
                .serviceDate(data.getServiceDate())
                .diningType(data.getDiningType())
                .makersId(data.getMakersId()).build();
    }

    public boolean equals(Object obj) {
        if(obj instanceof RecommendScheduleDto tmp) {
            return serviceDate.equals(tmp.serviceDate) && diningType.equals(tmp.diningType) && makersId.equals(tmp.makersId);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(serviceDate, diningType, makersId);
    }
}
