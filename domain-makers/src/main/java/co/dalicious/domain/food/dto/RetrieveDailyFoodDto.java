package co.dalicious.domain.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

@Getter
@Setter
@Schema(description = "식단 조회 응답 Dto")
public class RetrieveDailyFoodDto {
    List<DiningType> diningTypes;
    SupportPrice supportPrice;
    ServiceDays serviceDays;
    List<DailyFoodDto> dailyFoodDtos;

    @Builder
    public RetrieveDailyFoodDto(List<DiningType> diningTypes, List<DailyFoodDto> dailyFoodDtos, SupportPrice supportPrice, ServiceDays serviceDays) {
        this.diningTypes = diningTypes;
        this.supportPrice = supportPrice;
        this.dailyFoodDtos = dailyFoodDtos;
        this.serviceDays = serviceDays;
    }

    @Getter
    @Setter
    public static class DiningType {
        private Integer diningType;
        private List<String> times;

        public DiningType(Integer diningType, List<String> times) {
            this.diningType = diningType;
            this.times = times;
        }
    }

    @Getter
    public static class SupportPrice {
        private String morningSupportPrice;
        private String lunchSupportPrice;
        private String dinnerSupportPrice;

        public void setMorningSupportPrice(BigDecimal morningSupportPrice) {
            if(morningSupportPrice.compareTo(BigDecimal.valueOf(62471004L)) == 0 ) {
                this.morningSupportPrice = "금액의 50%";
                return;
            }
            int price = morningSupportPrice.intValue();
            String formattedPrice;
            if (price < 1000) {
                formattedPrice = String.valueOf(price);
            } else {
                formattedPrice = String.format("%,d", price);
            }
            this.morningSupportPrice = formattedPrice;
        }

        public void setLunchSupportPrice(BigDecimal lunchSupportPrice) {
            if(lunchSupportPrice.compareTo(BigDecimal.valueOf(62471004L)) == 0 ) {
                this.lunchSupportPrice = "금액의 50%";
                return;
            }
            int price = lunchSupportPrice.intValue();
            String formattedPrice;
            if (price < 1000) {
                formattedPrice = String.valueOf(price);
            } else {
                formattedPrice = String.format("%,d", price);
            }
            this.lunchSupportPrice = formattedPrice;
        }

        public void setDinnerSupportPrice(BigDecimal dinnerSupportPrice) {
            if(dinnerSupportPrice.compareTo(BigDecimal.valueOf(62471004L)) == 0 ) {
                this.dinnerSupportPrice = "금액의 50%";
                return;
            }
            int price = dinnerSupportPrice.intValue();
            String formattedPrice;
            if (price < 1000) {
                formattedPrice = String.valueOf(price);
            } else {
                formattedPrice = String.format("%,d", price);
            }
            this.dinnerSupportPrice = formattedPrice;
        }
    }

    @Getter
    @Setter
    public static class ServiceDays {
        private List<String> morningServiceDays;
        private List<String> lunchServiceDays;
        private List<String> dinnerServiceDays;
    }
}
