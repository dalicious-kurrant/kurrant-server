package co.dalicious.domain.board.entity;

import co.dalicious.domain.board.entity.enums.BoardStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board__notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;
    
    @Comment("공지 제목")
    private String title;
    
    @Lob
    @Comment("공지 내용")
    private String content;

    @Comment("스팟 공지일 경우 스팟ID")
    @Column(name="spotId", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger spotId;

    @ColumnDefault(value = "1")
    @Comment("상태 0:비활성/1:활성/2:팝업/3:스팟공지")
    @Column(name="status", columnDefinition = "INT")
    private BoardStatus status;


    @Builder
    public Notice(BigInteger id, String title, String content, BigInteger spotId, Integer status){
        this.id = id;
        this.title = title;
        this.content = content;
        this.spotId = spotId;
        this.status = BoardStatus.ofCode(status);
    }
}
