package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import java.math.BigInteger;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EatInSpot extends Spot{
    @Comment("메이커스 FK")
    private BigInteger makersId;

    public EatInSpot(String name, Address address, List<DiningType> diningTypes, Group group, String memo, BigInteger makersId) {
        super(name, address, diningTypes, group, memo);
        this.makersId = makersId;
    }
}
