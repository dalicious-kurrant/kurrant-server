package co.dalicious.domain.board.entity;

import co.dalicious.domain.board.converter.BoardTypeConverter;
import co.dalicious.domain.board.converter.GroupIdListConverter;
import co.dalicious.domain.board.entity.enums.BoardType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
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

    @Column(name = "group_ids")
    @Convert(converter = GroupIdListConverter.class)
    @Comment("스팟 공지일 경우 그룹ID")
    private List<BigInteger> groupIds;

    @Column(name="status")
    @Comment("상태 0:비활성 / 1:활성")
    private Boolean isStatus;

    @Column(name="e_type")
    @Comment("상태 0:전체공지/1:스팟공지/2:팝업/3:이벤트 공지")
    @Convert(converter = BoardTypeConverter.class)
    private BoardType boardType;

    @ColumnDefault(value = "0")
    @Column(name="is_push_alarm")
    @Comment("상태 0:비활성 / 1:활성")
    private Boolean isPushAlarm;

    @Column(name = "active_date")
    @Comment("상태변경 일")
    private LocalDate activeDate;

    @Builder
    public Notice(String title, String content, List<BigInteger> groupIds, Boolean isStatus, BoardType boardType, Boolean isPushAlarm, LocalDate activeDate) {
        this.title = title;
        this.content = content;
        this.groupIds = groupIds;
        this.isStatus = isStatus;
        this.boardType = boardType;
        this.isPushAlarm = isPushAlarm;
        this.activeDate = activeDate;
    }

    public void updatePushAlarm(Boolean pushAlarm) {
        this.isPushAlarm = pushAlarm;
    }
    public void updateActiveDate(LocalDate activeDate) { this.activeDate = activeDate;}
}
