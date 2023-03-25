package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApartmentSpot extends Spot {
    @Builder
    public ApartmentSpot(String name, Address address, List<DiningType> diningTypes, Group group, String memo) {
        super(name, address, diningTypes, group, memo);
    }
}
