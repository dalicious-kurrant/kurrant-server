package co.dalicious.domain.order.dto;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.DiningType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CapacityDto {

    @Getter
    @Setter
    public static class MakersCapacity {
        private LocalDate serviceDate;
        private DiningType diningType;
        private Makers makers;
        private Integer capacity;

        public MakersCapacity(LocalDate serviceDate, DiningType diningType, Makers makers, Integer capacity) {
            this.serviceDate = serviceDate;
            this.diningType = diningType;
            this.makers = makers;
            this.capacity = capacity;
        }
    }
}
