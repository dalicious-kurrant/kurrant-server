package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "client__open_group")
public class OpenGroup extends Group{

    @Comment("오픈 그룹 사용자 수")
    private Integer openGroupUserCount;

    @Builder
    public OpenGroup(Address address, List<DiningType> diningTypes, String name, Integer openGroupUserCount, String memo) {
        super(address, diningTypes, name, memo);
        this.openGroupUserCount = openGroupUserCount;
    }

    public void updateOpenSpot(Address address, List<DiningType> diningTypeList, String name, Integer openGroupUserCount, Boolean isActive) {
        updateGroup(address, diningTypeList, name, isActive);
        this.openGroupUserCount = openGroupUserCount;
    }
}
