package co.dalicious.domain.user.entity;

import co.dalicious.domain.client.converter.GroupDataTypeConverter;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user__user_spot")
public class UserSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("유저가 기본으로 저장한 스팟을 가져온다")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment("기본 설정된 스팟인지 확인")
    private Boolean isDefault;

    @ManyToOne(optional = false)
    @JoinColumn
    @JsonManagedReference(value = "user_spot_fk")
    @Comment("유저")
    private User user;

    @Convert(converter = GroupDataTypeConverter.class)
    @Comment("그룹 타입(아파트/기업)")
    private GroupDataType groupDataType;

    @OneToOne
    @JoinColumn
    @JsonManagedReference(value = "spot_fk")
    @Comment("기본으로 설정한 스팟 id")
    private Spot spot;

    public void updateSpot(Spot spot) {
        this.spot = spot;
    }

    public void updateDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void updateClientType(GroupDataType clientType) {
        this.groupDataType = clientType;
    }

    public UserSpot(User user, GroupDataType groupDataType, Spot spot, Boolean isDefault) {
        this.user = user;
        this.groupDataType = groupDataType;
        this.spot = spot;
        this.isDefault = isDefault;
    }


}
