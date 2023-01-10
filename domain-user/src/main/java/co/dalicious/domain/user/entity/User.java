package co.dalicious.domain.user.entity;

import co.dalicious.domain.user.entity.enums.GourmetType;
import co.dalicious.domain.user.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.user.converter.GourmetTypeConverter;
import co.dalicious.domain.user.converter.RoleConverter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import org.hibernate.annotations.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor
@Entity
@Table(name = "user__user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("사용자 PK")
    private BigInteger id;

    @NotNull
    @Convert(converter = RoleConverter.class)
    @Column(name = "e_role")
    @Comment("유저 타입")
    private Role role;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "updated_datetime",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "recent_login_datetime",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("마지막 로그인 날짜")
    private Timestamp recentLoginDateTime;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password",
            columnDefinition = "VARCHAR(255)")
    @Comment("사용자 비밀번호, BCrpypt")
    private String password;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(32)")
    @Comment("사용자 명")
    private String name;

    @Embedded
    private Image avatar;

    @Convert(converter = GourmetTypeConverter.class)
    @Column(name = "e_gourmet_type")
    @Comment("미식가 타입")
    private GourmetType gourmetType = GourmetType.NULL;

    @Column(name = "marketing_agreed_datetime",
            columnDefinition = "TIMESTAMP(6)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Comment("이메일 동의/철회 날짜")
    private Timestamp marketingAgreedDateTime;

    @Column(name = "marketing_agreed",
            columnDefinition = "BIT(1)")
    @Comment("이메일 동의 여부")
    private Boolean marketingAgree;

    @Column(name = "marketing_alarm",
            columnDefinition = "BIT(1)")
    @Comment("혜택 및 소식 알림")
    private Boolean marketingAlarm;

    @Column(name = "order_alarm",
            columnDefinition = "BIT(1)")
    @Comment("주문 알림")
    private Boolean orderAlarm;

    @Size(max = 64)
    @Column(name = "email", nullable = false, unique = true, length = 64,
            columnDefinition = "VARCHAR(64)")
    private String email;

    @Column(name = "point", precision = 15, nullable = false,
            columnDefinition = "DECIMAL(15, 0)")
    @ColumnDefault("0.00")
    private BigDecimal point;

    @OneToOne(mappedBy = "user")
    @JsonBackReference(value = "user_spot_fk")
    private UserSpot userSpot;

    @Size(max = 16)
    @Column(name = "phone", length = 16,
            columnDefinition = "VARCHAR(16)")
    private String phone;

    @Column(name = "is_membership", columnDefinition = "BIT(1)")
    @ColumnDefault("false")
    private Boolean isMembership;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference(value = "user-fk")
    List<ProviderEmail> providerEmails;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "user")
    @JsonBackReference(value = "user_fk")
    @Comment("스팟 기업 정보")
    private List<UserCorporation> corporations;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "user")
    @JsonBackReference(value = "user_fk")
    @Comment("스팟 기업 정보")
    private List<UserApartment> apartments;

    @Builder
    public User(BigInteger id, String password, String name, Role role, String email, String phone) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }


    public void changePassword(String password) {
        this.password = password;
    }

    public void changePhoneNumber(String phone) {
        this.phone = phone;
    }

    public void setEmailAndPassword(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void changeMarketingAgreement(Timestamp marketingAgreedDateTime, Boolean marketingAgree, Boolean marketingAlarm, Boolean orderAlarm) {
        this.marketingAgreedDateTime = marketingAgreedDateTime;
        this.marketingAgree = marketingAgree;
        this.marketingAlarm = marketingAlarm;
        this.orderAlarm = orderAlarm;
    }

    public void setMarketingAlarm(Boolean marketingAlarm) {
        this.marketingAlarm = marketingAlarm;
    }

    public void setOrderAlarm(Boolean orderAlarm) {
        this.orderAlarm = orderAlarm;
    }

    public void updateRecentLoginDateTime(Timestamp recentLoginDateTime) {
        this.recentLoginDateTime = recentLoginDateTime;
    }

    public void changeMembershipStatus(Boolean isMembership) {
      this.isMembership = isMembership;
    }

    public void updateUserSpot(UserSpot userSpot) {
        this.userSpot = userSpot;
    }
}
