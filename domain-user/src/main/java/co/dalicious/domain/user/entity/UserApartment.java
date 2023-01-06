package co.dalicious.domain.user.entity;

import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.user.converter.ClientStatusConverter;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user__user_apartment")
public class UserApartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("유저 아파트 정보 PK")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonManagedReference(value = "apartment_fk")
    @Comment("아파트 정보 FK")
    private Apartment apartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonManagedReference(value = "user_fk")
    @Comment("유저 정보 FK")
    private User user;

    @Convert(converter = ClientStatusConverter.class)
    @Comment("가입/탈퇴 상태")
    @Column(nullable = false)
    private ClientStatus clientStatus = ClientStatus.BELONG;

    @NotNull
    @Comment("아파트 유저의 세부주소 (호수)")
    private Integer ho;

    @Builder
    public UserApartment(Apartment apartment, User user, Integer ho) {
        this.apartment = apartment;
        this.user = user;
        this.ho = ho;
    }

    public void updateStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    public void updateHo(Integer ho) {
        this.ho = ho;
    }
}
