package co.kurrant.app.public_api.dto.food;

import co.dalicious.domain.food.dto.DailyFoodDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter

public class DailyFoodByDateDto {
    List<DailyFoodByDateDto.ServiceInfo> diningTypes;
    List<DailyFoodByDateDto.DailyFoodGroupByDate> dailyFoodGroupByDate;

    public DailyFoodByDateDto(List<ServiceInfo> diningTypes, List<DailyFoodGroupByDate> dailyFoodGroupByDate) {
        this.diningTypes = diningTypes;
        this.dailyFoodGroupByDate = dailyFoodGroupByDate;
    }

    @Getter
    @Setter
    public static class ServiceInfo {
        private Integer diningType;
        private List<String> serviceDays;
        private List<String> times;
        private List<SupportPriceByDay> supportPriceByDays;

        public ServiceInfo(Integer diningType, List<String> serviceDays, List<String> times) {
            this.diningType = diningType;
            this.serviceDays = serviceDays;
            this.times = times;
        }
    }

    @Getter
    @Setter
    public static class DailyFoodGroupByDate {
        private String serviceDate;
        private List<DailyFoodByDate> dailyFoodDtos;
    }

    @Getter
    @Setter
    public static class DailyFoodByDate {
        private Integer diningType;
        private BigDecimal supportPrice;
        private List<DailyFoodDto> dailyFoodDtos;
    }
    @Getter
    @Setter
    public static class SupportPriceByDay {
        private String day;
        private String supportPrice;

        public SupportPriceByDay(String day, BigDecimal supportPrice) {
            this.day = day;
            if(supportPrice.compareTo(BigDecimal.valueOf(62471004L)) == 0 ) {
                this.supportPrice = "금액의 50%";
                return;
            }
            int price = supportPrice.intValue();
            String formattedPrice;
            if (price < 1000) {
                formattedPrice = String.valueOf(price);
            } else {
                formattedPrice = String.format("%,d", price);
            }
            this.supportPrice = formattedPrice;
        }
    }
}
