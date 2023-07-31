package co.dalicious.domain.board.entity;

import co.dalicious.domain.board.converter.GroupIdListConverter;
import co.dalicious.domain.board.entity.enums.BoardType;
import co.dalicious.domain.file.entity.embeddable.Image;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "board__back_office_notice")
public class BackOfficeNotice {

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

    @ElementCollection
    @Comment("이미지 경로")
    @CollectionTable(name = "reviews__images")
    private List<Image> images;

    @Column(name="status")
    @Comment("상태 0:비활성 / 1:활성")
    private Boolean isStatus;

    @Column(name="e_type")
    private BoardType boardType;

    @ColumnDefault(value = "0")
    @Column(name="is_alarm_talk")
    @Comment("상태 0:비활성 / 1:활성")
    private Boolean isAlarmTalk;

    @Builder
    public BackOfficeNotice(String title, String content, List<Image> images, Boolean isStatus, BoardType boardType, Boolean isAlarmTalk) {
        this.title = title;
        this.content = content;
        this.images = images;
        this.isStatus = isStatus;
        this.boardType = boardType;
        this.isAlarmTalk = isAlarmTalk;
    }
}
