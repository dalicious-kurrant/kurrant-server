package co.dalicious.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user__daily_report")
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("daily report id")
    private BigInteger id;

    @Comment("유저 ID")
    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "BIGINT UNSIGNED")
    private User user;

    @Comment("식사 이름")
    @Column(name ="name", columnDefinition = "VARCHAR(64)")
    private String foodName;

    @Comment("칼로리")
    @Column(name = "calorie", columnDefinition = "INT")
    private Integer calorie;

    @Comment("단백질")
    @Column(name = "protein", columnDefinition = "INT")
    private Integer protein;

    @Comment("지방")
    @Column(name = "fat", columnDefinition = "INT")
    private Integer fat;

    @Comment("탄수화물")
    @Column(name = "carbohydrate", columnDefinition = "INT")
    private Integer carbohydrate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(name ="eat_date", columnDefinition = "DATE DEFAULT NOW()")
    private LocalDate eatDate;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "updated_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @Comment("구분")
    @Column(name="type", columnDefinition = "VARCHAR(8)")
    private String type;


}
