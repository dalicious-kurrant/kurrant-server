package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.util.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigInteger;

import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "client__apartment")
public class Apartment extends Group{

    @Column(name = "family_count")
    private Integer familyCount;


    @Builder
    public Apartment(Address address, List<DiningType> diningTypes, String name, BigInteger managerId, Integer familyCount) {
        super(address, diningTypes, name, managerId);
        this.familyCount = familyCount;
    }
}