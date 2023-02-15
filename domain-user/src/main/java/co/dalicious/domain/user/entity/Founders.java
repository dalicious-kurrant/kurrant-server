package co.dalicious.domain.user.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user__founders", indexes = @Index(name = "i_founders_number", columnList = "foundersNumber"))
public class Founders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("파운더스 id")
    private BigInteger id;

    @Comment("파운더스 유지 여부")
    private Boolean isActive;

    @Column(unique = true)
    @Comment("파운더스 멤버십 번호")
    private Integer foundersNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @Comment("유저")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @Comment("멤버십")
    private Membership membership;

    public Founders(Boolean isActive, Integer foundersNumber, User user, Membership membership) {
        this.isActive = isActive;
        this.foundersNumber = foundersNumber;
        this.user = user;
        this.membership = membership;
    }

    public void updateIsActive(Boolean active) {
        isActive = active;
    }
}
