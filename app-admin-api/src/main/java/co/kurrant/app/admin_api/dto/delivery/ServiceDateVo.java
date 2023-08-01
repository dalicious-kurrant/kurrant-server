package co.kurrant.app.admin_api.dto.delivery;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
public class ServiceDateVo {
    private LocalDate serviceDate;
    private LocalTime deliveryTime;

    @Builder
    public ServiceDateVo(LocalDate serviceDate, LocalTime deliveryTime) {
        this.serviceDate = serviceDate;
        this.deliveryTime = deliveryTime;
    }

    public boolean equals(Object obj) {
        if(obj instanceof ServiceDateVo tmp) {
            return serviceDate.equals(tmp.serviceDate) && deliveryTime.equals(tmp.deliveryTime);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(serviceDate, deliveryTime);
    }
}