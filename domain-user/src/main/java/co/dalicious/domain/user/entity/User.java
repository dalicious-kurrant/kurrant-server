package co.dalicious.domain.user.entity;

import co.dalicious.domain.client.entity.enums.SpotStatus;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.user.converter.GourmetTypeConverter;
import co.dalicious.domain.user.converter.PushConditionsConverter;
import co.dalicious.domain.user.converter.RoleConverter;
import co.dalicious.domain.user.converter.UserStatusConverter;
import co.dalicious.domain.user.entity.enums.*;
import co.dalicious.system.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @JsonBackReference(value = "user_department_fk")
    private List<UserDepartment> userDepartments;

    @Size(max = 16)
    @Column(name = "phone", length = 16,
            columnDefinition = "VARCHAR(16)")
    private String phone;

    @Comment("FCM 토큰")
    @Column(name = "firebase_token", columnDefinition = "VARCHAR(255)")
    private String firebaseToken;

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

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Comment("결제 비밀번호")
    @Column(name="payment_password", columnDefinition = "VARCHAR(255)")
    private String paymentPassword;

    @Convert(converter = PushConditionsConverter.class)
    @Column(name = "push_condition")
    @Comment("알림 조건")
    private List<PushCondition> pushConditionList;

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
                Timestamp createdDateTime, Timestamp updatedDateTime, List<PushCondition> pushConditionList){
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
        this.pushConditionList = pushConditionList;
    }


    public void changePassword(String password) {
        this.password = password;
    }

    public void changePaymentPassword(String paymentPassword){
        this.paymentPassword = paymentPassword;
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

    public String getActiveUserGrouptoString() {
        if(getActiveUserGroups() == null) return null;
        List<String> groupNames = getActiveUserGroups().stream()
                .map(v -> v.getGroup().getName())
                .toList();
        if(groupNames.isEmpty()) {
            return null;
        }
        return StringUtils.StringListToString(groupNames);
    }

    public String getDepartment(){
        //관련 기획이 없으므로 임시로 부서가 1개라고 가정하고 작성
        if (!getUserDepartments().isEmpty()){
            return getUserDepartments().get(0).getDepartment().getName();
        }

        return "없음";
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
        if (this.email != null && email.contains("@") && email.indexOf("@") >= 2) {
            // extract the email prefix and domain
            String prefix = this.email.substring(0, 2);
            String domain = this.email.substring(this.email.indexOf("@"));
            // replace the prefix with asterisks
            String maskedPrefix = "*".repeat(this.email.substring(2, this.email.indexOf("@")).length());
            // set the modified email address
            double dValue = Math.random();
            int iValue = (int)(dValue * 10000);
            this.email = prefix + maskedPrefix + domain + "(" + iValue + ")";
        } else {
            this.email = email; // use the original email if it doesn't meet the criteria
        }

        if (this.phone != null && this.phone.length() >= 11) {
            this.phone = this.phone.substring(0, 7) + "****";
        }

        // 이름 개인정보 수정
        if(this.name != null && !this.name.equals("이름없음")) {
            String prefix = this.name.substring(0, 1);
            int length = this.name.substring(1).length();
            String maskedPrefix = "*".repeat(length);

            this.name = prefix + maskedPrefix;
        }

        this.providerEmails.forEach(ProviderEmail::withdrawEmail);
        this.userStatus = UserStatus.INACTIVE;
        this.password = null;
        this.marketingAlarm = false;
        this.marketingAgree = false;
        this.orderAlarm = false;
        this.point = BigDecimal.ZERO;
        this.isMembership = false;
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }

    public void updatePaymentPassword(String paymentPassword) {
        this.paymentPassword = paymentPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    public void updatePushCondition(List<PushCondition> pushConditionList) {
        this.pushConditionList = pushConditionList;
    }

    public boolean hasPushCondition(PushCondition condition) {
        return pushConditionList.contains(condition);
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }
    public List<UserGroup> getActiveUserGroups() {
        return this.getGroups().stream()
                .filter(v -> v.getGroup().getIsActive() == null || v.getGroup().getIsActive())
                .filter(v -> v.getClientStatus().equals(ClientStatus.BELONG))
                .toList();
    }

    public List<UserSpot> getActiveUserSpot() {
        return this.getUserSpots().stream()
                .filter(v -> v.getSpot().getStatus().equals(SpotStatus.ACTIVE))
                .toList();
    }
}
