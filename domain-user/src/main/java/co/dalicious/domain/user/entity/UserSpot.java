package co.dalicious.domain.user.entity;

import co.dalicious.domain.client.entity.ApartmentSpot;
import co.dalicious.domain.client.entity.CorporationSpot;
import co.dalicious.domain.user.converter.ClientConverter;
import co.dalicious.domain.user.entity.enums.ClientType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user__user_spot")
public class UserSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("유저가 기본으로 저장한 스팟을 가져온다")
    @NotNull
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;


    @OneToOne(orphanRemoval = true, optional = false)
    @JsonManagedReference(value = "user_spot_fk")
    @Comment("유저")
    private User user;

    @Convert(converter = ClientConverter.class)
    @Comment("그룹 타입(아파트/기업)")
    private ClientType clientType;

    @OneToOne
    @JsonManagedReference(value = "corporation_spot_fk")
    @Comment("기본으로 설정한 스팟 id")
    private CorporationSpot corporationSpot;

    @OneToOne
    @JsonManagedReference(value = "apartment_spot_fk")
    @Comment("기본으로 설정한 스팟 id")
    private ApartmentSpot apartmentSpot;

    @Builder
    public UserSpot(User user, ClientType clientType, CorporationSpot corporationSpot, ApartmentSpot apartmentSpot) {
        this.user = user;
        this.clientType = clientType;
        this.corporationSpot = corporationSpot;
        this.apartmentSpot = apartmentSpot;
    }


}
