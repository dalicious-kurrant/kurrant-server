package co.kurrant.app.public_api.dto.order;

import co.dalicious.system.enums.DiningType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class OrderByServiceDateNotyDto {
    private DiningType type;
    private List<String> serviceDays;
    private LocalTime lastOrderTime;

    @Builder
    public OrderByServiceDateNotyDto(DiningType type, List<String> serviceDays, LocalTime lastOrderTime) {
        this.type = type;
        this.lastOrderTime = lastOrderTime;
        this.serviceDays = serviceDays;
    }
}
