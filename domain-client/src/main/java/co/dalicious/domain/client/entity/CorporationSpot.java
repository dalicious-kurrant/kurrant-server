package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.util.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CorporationSpot extends Spot{
    @Builder
    public CorporationSpot(String name, Address address, List<DiningType> diningTypes, Group group) {
        super(name, address, diningTypes, group);
    }
}
