package co.dalicious.domain.user.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import co.dalicious.domain.group.entity.ClientApartment;
import co.dalicious.domain.group.entity.ClientCorporation;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import co.dalicious.domain.file.entity.embeddable.Image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user__user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "BIGINT UNSIGNED")
  @Comment("사용자 PK")
  private Long id;

  @Column(name = "created_datetim", nullable = false,
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
  @Comment("생성일")
  private Timestamp createdDateTime;

  @Column(name = "updated_datetime", nullable = false,
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
  @Comment("수정일")
  private Timestamp updatedDateTime;


  @ColumnDefault("false")
  @Column(name = "email_address_verified", nullable = false,
      columnDefinition = "BIT(1)")
  @Comment("사용자 이메일 인증여부")
  private Boolean emailAddressVerified;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "password", nullable = false,
      columnDefinition = "BINARY(144)")
  @Comment("사용자 비밀번호, PBKDF2 180000")
  private byte[] password;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "salt", nullable = false, columnDefinition = "BINARY(64)")
  @Comment("비밀번호 SALT")
  private byte[] salt;

  @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(32)")
  @Comment("사용자 명")
  private String name;

  @Embedded
  private Image avatar;

  @Column(name = "email_marketing_agreed_datetime", nullable = false,
      columnDefinition = "TIMESTAMP(6)")
  @Comment("이메일 동의 여부")
  private Timestamp emailMarketingAgreedDateTime;

  @Size(max = 8)
  @NotNull
  @Column(name = "e_role", nullable = false, length = 8)
  private String eRole;

  @Size(max = 64)
  @NotNull
  @Column(name = "email", nullable = false, length = 64)
  private String email;

  @Column(name = "point", precision = 15)
  private BigDecimal point;

  @Size(max = 16)
  @Column(name = "phone", length = 16)
  private String phone;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "corporation_id", nullable = false)
  private ClientCorporation corporation;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "apartment_id", nullable = false)
  private ClientApartment apartment;

  @Column(name = "is_membership")
  private Boolean isMembership;
}
