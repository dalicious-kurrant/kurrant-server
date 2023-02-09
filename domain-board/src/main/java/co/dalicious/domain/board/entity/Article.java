package co.dalicious.domain.board.entity;

import java.math.BigInteger;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
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
@Table(name = "board__article")
public class Article {
  @Comment("게시글 PK")
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "BIGINT UNSIGNED")
  private BigInteger id;

  @ColumnDefault("NOW(6)")
  @Comment("생성일")
  @CreationTimestamp
  @Column(name = "created_datetime", nullable = false, insertable = false, updatable = false,
      columnDefinition = "TIMESTAMP(6)")
  private Timestamp createdDateTime;

  @Comment("카테고리")
  @Column(name = "category", nullable = false, columnDefinition = "VARCHAR(32)")
  private String category;

  @Comment("제목")
  @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(32)")
  private String title;

  @Comment("내용HTML")
  @Column(name = "content_html", nullable = false, columnDefinition = "TEXT")
  private String contentHtml;

  @Comment("내용STRING")
  @Column(name = "content_string", nullable = false, columnDefinition = "TEXT")
  private String contentString;

  @Comment("게시판 FK")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "board_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
  private Board board;
  @Column(name = "board_id", nullable = false, insertable = false, updatable = false,
      columnDefinition = "BIGINT UNSIGNED")
  private BigInteger boardId;
}
