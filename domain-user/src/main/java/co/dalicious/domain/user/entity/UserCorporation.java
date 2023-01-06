package co.dalicious.domain.user.entity;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.user.converter.ClientStatusConverter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "user__user_corporation")
public class UserCorporation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("유저 아파트 정보 PK")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonManagedReference(value = "corporation_fk")
    @Comment("기업 정보 FK")
    private Corporation corporation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonManagedReference(value = "user_fk")
    @Comment("유저 정보 FK")
    private User user;

    @Convert(converter = ClientStatusConverter.class)
    @Comment("가입/탈퇴 상태")
    private ClientStatus clientStatus = ClientStatus.BELONG;

    @Builder
    public UserCorporation(Corporation corporation, User user) {
        this.corporation = corporation;
        this.user = user;
    }

    public void updateStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }
}
