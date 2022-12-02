package co.dalicious.domain.user.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

  @Enumerated(value = EnumType.STRING)
  private Role role;

  @CreationTimestamp
  @Column(name = "created_datetime", nullable = false,
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
  @Comment("생성일")
  private Timestamp createdDateTime;

  @UpdateTimestamp
  @Column(name = "updated_datetime",
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
  @Comment("수정일")
  private Timestamp updatedDateTime;


  @Column(name = "email_address_verified", nullable = false,
      columnDefinition = "BIT(1)")
  @ColumnDefault("false")
  @Comment("사용자 이메일 인증여부")
  private Boolean emailAddressVerified;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "password", nullable = false,
      columnDefinition = "VARCHAR(128)")
  @Comment("사용자 비밀번호, BCrpypt")
  private String password;

  @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(32)")
  @Comment("사용자 명")
  private String name;

//  @Embedded
//  private Image avatar;

  @Column(name = "marketing_agreed_datetime",
      columnDefinition = "TIMESTAMP(6)")
  @Comment("이메일 동의 여부")
  private Timestamp marketingAgreedDateTime;

  @Column(name = "marketing_alarm",
    columnDefinition = "BIT(1)")
  @Comment("혜택 및 소식 알림")
  private Boolean marketingAlarm;

  @Column(name = "order_alarm",
          columnDefinition = "BIT(1)")
  @Comment("주문 알림")
  private Boolean orderAlarm;

  @Size(max = 64)
  @Column(name = "email", nullable = false, length = 64,
          columnDefinition = "VARCHAR(64)")
  private String email;

  @Column(name = "point", precision = 15, nullable = false,
          columnDefinition = "DECIMAL(15, 0)")
  @ColumnDefault("0.00")
  private BigDecimal point;

  @Size(max = 16)
  @Column(name = "phone", length = 16,
          columnDefinition = "VARCHAR(16)")
  private String phone;

  @Column(name = "is_membership", columnDefinition = "BIT(1)")
  @ColumnDefault("false")
  private Boolean isMembership;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  @JsonManagedReference(value = "user-fk")
  List<ProviderEmail> providerEmails;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "corporation_id")
  @JsonBackReference(value = "corporation-fk")
  private Corporation corporation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "apartment_id")
  @JsonBackReference(value = "apartment-fk")
  private Apartment apartment;

  @Builder
  public User(String password, String name, Role role, String email, String phone, Corporation corporation, Apartment apartment) {
    this.password = password;
    this.name = name;
    this.role = role;
    this.email = email;
    this.phone = phone;
    this.corporation = corporation;
    this.apartment = apartment;
  }

  public void changePassword(String password) {
    this.password = password;
  }

  public void changePhoneNumber(String phone) {
    this.phone = phone;
  }
}
