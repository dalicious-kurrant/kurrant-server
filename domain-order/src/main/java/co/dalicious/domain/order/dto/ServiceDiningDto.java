package co.dalicious.domain.order.dto;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.system.enums.DiningType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class ServiceDiningDto {
    LocalDate serviceDate;
    DiningType diningType;

    public ServiceDiningDto(LocalDate serviceDate, DiningType diningType) {
        this.serviceDate = serviceDate;
        this.diningType = diningType;
    }

    public ServiceDiningDto(DailyFood dailyFood) {
        this.serviceDate = dailyFood.getServiceDate();
        this.diningType = dailyFood.getDiningType();
    }

    public boolean equals(Object obj) {
        if(obj instanceof ServiceDiningDto tmp) {
            return serviceDate.equals(tmp.serviceDate) && diningType.equals(tmp.diningType);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(serviceDate, diningType);
    }
}
