package co.dalicious.domain.user.entity;

import co.dalicious.domain.user.converter.MembershipSubscriptionTypeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "membershipInfo")
public class MembershipInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @NotNull
    @Column(name = "e_subscription_type")
    @Convert(converter = MembershipSubscriptionTypeConverter.class)
    @Comment("멤버십 구독 타입(월간/연간)")
    private MembershipSubscriptionType membershipSubscriptionType;


    @Column(name = "start_date", columnDefinition = "DATE")
    @Comment("멤버십 시작날짜")
    private LocalDate startDate;

    @Column(name = "end_date", columnDefinition = "DATE")
    @Comment("멤버십 종료날짜")
    private LocalDate endDate;

    @Column(name = "auto_payment", columnDefinition = "BIT(1)")
    @Comment("멤버십 자동 결제 여부")
    private Boolean auto_payment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}