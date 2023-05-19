package co.dalicious.domain.user.entity;

import co.dalicious.domain.user.converter.MembershipStatusConverter;
import co.dalicious.domain.user.converter.MembershipSubscriptionTypeConverter;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "user__membership")
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @NotNull
    @Column(name = "e_membership_status")
    @Convert(converter = MembershipStatusConverter.class)
    @Comment("멤버십 구독 상태")
    private MembershipStatus membershipStatus;

    @NotNull
    @Column(name = "e_subscription_type")
    @Convert(converter = MembershipSubscriptionTypeConverter.class)
    @Comment("멤버십 구독 타입(1. 월간 2.연간)")
    private MembershipSubscriptionType membershipSubscriptionType;


    @Column(name = "start_date", columnDefinition = "DATE")
    @Comment("멤버십 시작날짜")
    private LocalDate startDate;

    @Column(name = "end_date", columnDefinition = "DATE")
    @Comment("멤버십 종료날짜")
    private LocalDate endDate;

    @Column(name = "auto_payment", columnDefinition = "BIT(1) DEFAULT 0")
    @Comment("멤버십 자동 결제 여부")
    private Boolean autoPayment;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "membership", orphanRemoval = true)
    @JsonBackReference(value = "membership_fk")
    private List<MembershipDiscountPolicy> membershipDiscountPolicyList;


    public Membership(MembershipStatus membershipStatus, MembershipSubscriptionType membershipSubscriptionType, LocalDate startDate, LocalDate endDate, Boolean autoPayment, User user) {
        this.membershipStatus = membershipStatus;
        this.membershipSubscriptionType = membershipSubscriptionType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.autoPayment = autoPayment;
        this.user = user;
    }

    public void changeAutoPaymentStatus(Boolean autoPayment) {
       this.autoPayment = autoPayment;
    }

    public void changeMembershipStatus(MembershipStatus membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public void updateEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}