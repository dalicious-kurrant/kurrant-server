package co.kurrant.app.public_api.dto.food;

import co.dalicious.domain.food.dto.DailyFoodDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter

public class DailyFoodResDto {
    List<DailyFoodResDto.ServiceInfo> diningTypes;
    List<DailyFoodResDto.DailyFoodByDate> dailyFoodsByDate;

    public DailyFoodResDto(List<ServiceInfo> diningTypes, List<DailyFoodByDate> dailyFoodsByDate) {
        this.diningTypes = diningTypes;
        this.dailyFoodsByDate = dailyFoodsByDate;
    }

    @Getter
    @Setter
    public static class ServiceInfo {
        private Integer diningType;
        private List<String> serviceDays;
        private List<String> times;

        public ServiceInfo(Integer diningType, List<String> serviceDays, List<String> times) {
            this.diningType = diningType;
            this.serviceDays = serviceDays;
            this.times = times;
        }
    }

    @Getter
    @Setter
    public static class DailyFoodByDate {
        private String serviceDate;
        private Integer diningType;
        private BigDecimal supportPrice;
        private List<DailyFoodDto> dailyFoodDtos;
    }
}
