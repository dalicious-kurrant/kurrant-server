package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenGroupSpot extends Spot {
    @Builder
    public OpenGroupSpot(String name, Address address, List<DiningType> diningTypes, Group group) {
        super(name, address, diningTypes, group);
    }
}