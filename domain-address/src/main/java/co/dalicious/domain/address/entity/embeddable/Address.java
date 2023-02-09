package co.dalicious.domain.address.entity.embeddable;

import java.math.BigInteger;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import lombok.Getter;

@Getter
@Embeddable
public class Address {
  @Id
  @Column(columnDefinition = "BIGINT UNSIGNED COMMENT '주소 PK'")
  private BigInteger id;

  @Column(name = "created_datetime", nullable = false,
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) COMMENT '생성일'")
  private Timestamp createdDateTime;

  @Column(name = "zip_code", nullable = false, columnDefinition = "MEDIUMINT COMMENT '우편번호, 다섯자리'")
  private Integer zipCode;

  @Column(name = "basic", nullable = false, columnDefinition = "VARCHAR(255) COMMENT '기본주소'")
  private String basic;

  @Column(name = "rest", nullable = true, columnDefinition = "VARCHAR(255) COMMENT '상세주소'")
  private String rest;
}
