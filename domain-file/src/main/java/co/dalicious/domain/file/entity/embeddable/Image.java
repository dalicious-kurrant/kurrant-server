package co.dalicious.domain.file.entity.embeddable;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Image {
  @Column(name = "created_datetime", nullable = false,
      columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) COMMENT '생성일'")
  private Timestamp createdDateTime;

  @Column(name = "s3_key", length = 1024, nullable = false,
      columnDefinition = "VARCHAR(1024) COMMENT 'S3 업로드 키'")
  private String key;

  @Column(name = "location", length = 2048, nullable = false,
      columnDefinition = "VARCHAR(2048) COMMENT 'S3 접근 위치'")
  private String location;

  @Column(name = "filename", length = 1024, nullable = false,
      columnDefinition = "VARCHAR(1024) COMMENT '파일명, S3최대값'")
  private String filename;

  @Builder
  public Image(String key, String location, String filename) {
    this.key = key;
    this.location = location;
    this.filename = filename;
  }

}
