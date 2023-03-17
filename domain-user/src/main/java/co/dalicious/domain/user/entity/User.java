package co.dalicious.domain.user.entity;

import co.dalicious.domain.user.converter.UserStatusConverter;
import co.dalicious.domain.user.entity.enums.*;
import co.dalicious.system.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.user.converter.GourmetTypeConverter;
import co.dalicious.domain.user.converter.RoleConverter;
import lombok.AccessLevel;
import lombok.Builder;
import org.hibernate.annotations.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Convert(converter = UserStatusConverter.class)
    @Column(name = "e_user_status")
    @ColumnDefault("1")
    @Comment("유저 타입 0. 탈퇴 유저 1. 활성 유저 2. 탈퇴 요청 유저")
    private UserStatus userStatus = UserStatus.ACTIVE;

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
            columnDefinition = "BIT(1) DEFAULT 0")
    @Comment("이메일 동의 여부")
    private Boolean marketingAgree;

    @Column(name = "marketing_alarm",
            columnDefinition = "BIT(1) DEFAULT 0")
    @Comment("혜택 및 소식 알림")
    private Boolean marketingAlarm;

    @Column(name = "order_alarm",
            columnDefinition = "BIT(1) DEFAULT 0")
    @Comment("주문 알림")
    private Boolean orderAlarm;

    @Size(max = 64)
    @Column(name = "email", nullable = false, unique = true, length = 64,
            columnDefinition = "VARCHAR(64)")
    private String email;

    @Column(name = "point", precision = 15, nullable = false,
            columnDefinition = "DECIMAL(15, 2)")
    @ColumnDefault("0.00")
    private BigDecimal point;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @JsonBackReference(value = "user_spot_fk")
    private List<UserSpot> userSpots;

    @Size(max = 16)
    @Column(name = "phone", length = 16,
            columnDefinition = "VARCHAR(16)")
    private String phone;

    @Column(name = "is_membership", columnDefinition = "BIT(1) DEFAULT 0")
    @ColumnDefault("false")
    private Boolean isMembership;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference(value = "user-fk")
    List<ProviderEmail> providerEmails;

    @OneToMany(orphanRemoval = true, mappedBy = "user")
    @JsonBackReference(value = "user_fk")
    @Comment("스팟 기업 정보")
    private List<UserGroup> groups;

    @Builder
    public User(BigInteger id, String password, String name, Role role, String email, String phone) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }

    @Builder
    public User(BigInteger id, String password, String name, Role role,
                UserStatus userStatus, String phone, String email,
                BigDecimal point, GourmetType gourmetType, Boolean isMembership, Boolean marketingAgree,
                Timestamp marketingAgreedDateTime, Boolean marketingAlarm, Boolean orderAlarm, Timestamp recentLoginDateTime,
                Timestamp createdDateTime, Timestamp updatedDateTime){
        this.id = id;
        this.password = password;
        this.name = name;
        this.role = role;
        this.userStatus = userStatus;
        this.phone = phone;
        this.email = email;
        this.point = point;
        this.gourmetType = gourmetType;
        this.isMembership = isMembership;
        this.marketingAgree = marketingAgree;
        this.marketingAgreedDateTime = marketingAgreedDateTime;
        this.marketingAlarm = marketingAlarm;
        this.orderAlarm = orderAlarm;
        this.recentLoginDateTime = recentLoginDateTime;
        this.createdDateTime = createdDateTime;
        this.updatedDateTime = updatedDateTime;
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

    public void changeMarketingAgreement(Boolean marketingAgree, Boolean marketingAlarm, Boolean orderAlarm) {

        // 마케팅 정보 수신 동의/철회
        if (marketingAgree != null) {
            this.marketingAgree = marketingAgree;
            this.marketingAlarm = marketingAgree;
            this.orderAlarm = marketingAgree;
        }
        // 혜택 및 소식 알림 동의/철회
        if (marketingAlarm != null) {
            // 주문 알림이 활성화 되어 있을 경우
            if (this.orderAlarm) {
                this.marketingAlarm = marketingAlarm;
            }
            // 주문 알림이 활성화 되어 있지 않을 경우
            else {
                this.marketingAgree = false;
                this.marketingAlarm = marketingAlarm;
            }
        }
        // 주문 알림 동의/철회
        if (orderAlarm != null) {
            // 혜택 및 소식 알림이 활성화 되어 있을 경우
            if (this.marketingAlarm) {
                this.orderAlarm = orderAlarm;
            }
            // 혜택 및 소식 알림이 활성화 되어 있지 않을 경우
            else {
                this.marketingAgree = false;
                this.orderAlarm = orderAlarm;
            }
        }
        this.marketingAgreedDateTime = Timestamp.valueOf(LocalDateTime.now());
    }

    public void setMarketingAlarm(Boolean marketingAlarm) {
        this.marketingAlarm = marketingAlarm;
    }
    public void updateRole(Role role) {
        this.role = role;
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

    public void updatePoint(BigDecimal point) {
        this.point = point;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public void updateIsMembership(Boolean isMembership) {
        this.isMembership = isMembership;
    }

    public UserSpot getDefaultUserSpot() {
        return this.userSpots.stream()
                .filter(v -> v.getIsDefault().equals(true))
                .findAny()
                .orElse(null);
    }

    public void userSpotSetNull() {
        List<UserSpot> userSpots = this.getUserSpots();
        for (UserSpot userSpot : userSpots) {
            userSpot.updateDefault(false);
        }
    }

    public List<UserGroup> getActiveUserGroup() {
        return this.getGroups().stream()
                .filter(v -> v.getClientStatus().equals(ClientStatus.BELONG))
                .collect(Collectors.toList());
    }

    public String getActiveUserGrouptoString() {
        List<String> groupNames = getActiveUserGroup().stream()
                .map(v -> v.getGroup().getName())
                .toList();
        if(groupNames.isEmpty()) {
            return null;
        }
        return StringUtils.StringListToString(groupNames);
    }

    public String getProviderEmail(Provider provider) {
        return getProviderEmails().stream()
                .filter(v -> v.getProvider().equals(provider))
                .map(ProviderEmail::getEmail)
                .findFirst()
                .orElse(null);
    }

    public void withdrawUser() {
        // 이메일 개인정보 수정
        if (email != null && email.contains("@") && email.indexOf("@") >= 2) {
            // extract the email prefix and domain
            String prefix = email.substring(0, email.indexOf("@") - 2);
            String domain = email.substring(email.indexOf("@") + 1);
            // replace the prefix with asterisks
            String maskedPrefix = prefix.replaceAll(".", "*");
            // set the modified email address
            this.email = maskedPrefix + "@@" + domain;
        } else {
            this.email = email; // use the original email if it doesn't meet the criteria
        }
        this.userStatus = UserStatus.INACTIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
}
