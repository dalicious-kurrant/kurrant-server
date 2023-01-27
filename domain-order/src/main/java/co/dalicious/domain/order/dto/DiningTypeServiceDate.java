package co.dalicious.domain.order.dto;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.util.enums.DiningType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class DiningTypeServiceDate {
    LocalDate serviceDate;
    DiningType diningType;

    public DiningTypeServiceDate(LocalDate serviceDate, DiningType diningType) {
        this.serviceDate = serviceDate;
        this.diningType = diningType;
    }

    public boolean equals(Object obj) {
        if(obj instanceof DiningTypeServiceDate tmp) {
            return serviceDate.equals(tmp.serviceDate) && diningType.equals(tmp.diningType);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(serviceDate, diningType);
    }
}
