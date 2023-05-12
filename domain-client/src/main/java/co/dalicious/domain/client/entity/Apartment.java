package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
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
    @Comment("세대수")
    private Integer familyCount;


    @Builder
    public Apartment(Address address, List<DiningType> diningTypes, String name, BigInteger managerId, Integer familyCount, String memo) {
        super(address, diningTypes, name, managerId, memo);
        this.familyCount = familyCount;
    }

    public void updateApartment(Address address, List<DiningType> diningTypeList, String name, BigInteger managerId, Integer familyCount) {
        updateGroup(address, diningTypeList, name, managerId);
        this.familyCount = familyCount;
    }
}