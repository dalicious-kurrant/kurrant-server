package co.dalicious.domain.user.entity;

import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.user.converter.ClientStatusConverter;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Getter
public class UserApartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("유저 아파트 정보 PK")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "apartment_fk")
    @Comment("아파트 정보 FK")
    private Apartment apartment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonManagedReference(value = "user_fk")
    @Comment("유저 정보 FK")
    private User user;

}
