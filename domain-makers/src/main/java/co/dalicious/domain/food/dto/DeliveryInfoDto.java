package co.dalicious.domain.food.dto;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.DiningType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class DeliveryInfoDto {
    private LocalDate serviceDate;
    private DiningType diningType;
    private Group group;
    private Makers makers;
    private LocalTime deliveryTime;

    public DeliveryInfoDto(LocalDate serviceDate, DiningType diningType, Group group, Makers makers, LocalTime deliveryTime) {
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.group = group;
        this.makers = makers;
        this.deliveryTime = deliveryTime;
    }

    @Getter
    @Setter
    public static class Key {
        private LocalDate serviceDate;
        private DiningType diningType;
        private Group group;
        private LocalTime deliveryTime;

        public Key(DeliveryInfoDto deliveryInfoDto) {
            this.serviceDate = deliveryInfoDto.getServiceDate();
            this.diningType = deliveryInfoDto.getDiningType();
            this.group = deliveryInfoDto.getGroup();
            this.deliveryTime = deliveryInfoDto.getDeliveryTime();
        }

        public Key(LocalDate serviceDate, DiningType diningType, Group group, LocalTime deliveryTime) {
            this.serviceDate = serviceDate;
            this.diningType = diningType;
            this.group = group;
            this.deliveryTime = deliveryTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return serviceDate.equals(key.serviceDate) &&
                    diningType == key.diningType &&
                    group.equals(key.group) &&
                    deliveryTime.equals(key.deliveryTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceDate, diningType, group, deliveryTime);
        }
    }

    public Boolean hasSameValue(LocalDate serviceDate, DiningType diningType, Group group, Makers makers, LocalTime deliveryTime) {
        return this.serviceDate.equals(serviceDate) &&
                this.diningType.equals(diningType) &&
                this.group.equals(group) &&
                this.makers.equals(makers) &&
                this.deliveryTime.equals(deliveryTime);
    }
}
