package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenGroupSpot extends Spot {

    @Column(name = "access_restricted")
    @Comment("출입 제한 여부")
    private Boolean isRestriction;

    @Builder
    public OpenGroupSpot(String name, Address address, List<DiningType> diningTypes, Group group, String memo, Boolean isRestriction) {
        super(name, address, diningTypes, group, memo);
        this.isRestriction = isRestriction;
    }
}
