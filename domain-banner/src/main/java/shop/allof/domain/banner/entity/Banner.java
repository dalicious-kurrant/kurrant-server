package shop.allof.domain.banner.entity;

import java.math.BigInteger;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import io.corretto.domain.file.entity.embeddable.Image;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.allof.domain.banner.enums.BannerSection;
import shop.allof.domain.banner.enums.BannerType;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cms__banner")
public class Banner {
  @Id
  @GeneratedValue
  @Column(columnDefinition = "BIGINT UNSIGNED COMMENT '배너 PK'")
  private BigInteger id;

  @CreationTimestamp
  @Column(name = "created_datetime", nullable = false, insertable = false, updatable = false,
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) COMMENT '생성일'")
  private Timestamp createdDateTime;

  @UpdateTimestamp
  @Column(name = "updated_datetime", nullable = false, insertable = false, updatable = false,
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) ON UPDATE NOW(6) COMMENT '수정일'")
  private Timestamp updatedDateTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, columnDefinition = "VARCHAR(16) COMMENT '배너유형'")
  private BannerType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "section", nullable = false, columnDefinition = "VARCHAR(16) COMMENT '배너 구역'")
  private BannerSection section;

  @Column(name = "move_to", nullable = false, columnDefinition = "VARCHAR(32) COMMENT '이동 대상'")
  private String moveTo;

  @Embedded
  private Image image;

  @Builder
  public Banner(BannerType type, BannerSection section, String moveTo, Image image) {
    this.type = type;
    this.section = section;
    this.moveTo = moveTo;
    this.image = image;
  }
}
