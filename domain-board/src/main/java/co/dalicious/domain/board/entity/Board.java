package co.dalicious.domain.board.entity;

import java.math.BigInteger;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board__board")
public class Board {
  @Comment("게시글 PK")
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "BIGINT UNSIGNED")
  private BigInteger id;

  @Comment("생성일")
  @CreationTimestamp
  @Column(name = "created_datetime", nullable = false, insertable = false, updatable = false,
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
  private Timestamp createdDateTime;

  @Comment("게시판 명")
  @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(32)")
  private String name;
}
