package shop.allof.domain.user.entity;

import java.math.BigInteger;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.corretto.domain.file.entity.embeddable.Image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicInsert
@DynamicUpdate
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "user__user")
public class User {
  @Id
  @GeneratedValue
  @Column(columnDefinition = "BIGINT UNSIGNED COMMENT '사용자 PK'")
  private BigInteger id;

  @CreationTimestamp
  @Column(name = "created_datetime", nullable = false,
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) COMMENT '생성일'")
  private Timestamp createdDateTime;

  @UpdateTimestamp
  @Column(name = "updated_datetime", nullable = false,
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) COMMENT '수정일'")
  private Timestamp updatedDateTime;

  @Column(name = "email_address", nullable = false, unique = true,
      columnDefinition = "VARCHAR(254) COMMENT '사용자 이메일'")
  private String emailAddress;

  @ColumnDefault("false")
  @Column(name = "email_address_verified", nullable = false,
      columnDefinition = "BIT(1) COMMENT '사용자 이메일 인증여부'")
  private Boolean emailAddressVerified;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "password", nullable = false,
      columnDefinition = "BINARY(144) COMMENT '사용자 비밀번호, PBKDF2 180000'")
  private byte[] password;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "salt", nullable = false, columnDefinition = "BINARY(64) COMMENT '비밀번호 SALT'")
  private byte[] salt;

  @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(32) COMMENT '사용자 명'")
  private String name;

  @Embedded
  private Image avatar;

  @Column(name = "email_marketing_agreed_datetime", nullable = false,
      columnDefinition = "TIMESTAMP(6) COMMENT '이메일 동의 여부'")
  private Timestamp emailMarketingAgreedDateTime;

}
