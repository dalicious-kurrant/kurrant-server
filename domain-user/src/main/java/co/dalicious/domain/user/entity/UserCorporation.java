package co.dalicious.domain.user.entity;

import co.dalicious.domain.client.entity.Corporation;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@NoArgsConstructor
@Entity
@Getter
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

    @Builder
    public UserCorporation(Corporation corporation, User user) {
        this.corporation = corporation;
        this.user = user;
    }
}
