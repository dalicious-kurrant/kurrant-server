package co.dalicious.domain.logs.entity;

import co.dalicious.domain.logs.entity.converter.LogTypeConverter;
import co.dalicious.domain.logs.entity.enums.LogType;
import co.dalicious.system.converter.ListToStringConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "logs__admin_log")
public class AdminLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("백오피스 로그 id")
    private BigInteger id;

    @Convert(converter = LogTypeConverter.class)
    @Comment("로그 타입 1. 생성 2. 수정")
    private LogType logType;

    @Column(name = "request_url")
    private String baseUrl;

    @Column(name = "end_point")
    private String endPoint;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "user_code")
    private String userCode;

    @Column(name = "logs", columnDefinition = "TEXT")
    @Convert(converter = ListToStringConverter.class)
    private List<String> logs;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @Builder
    public AdminLogs(LogType logType, String baseUrl, String endPoint, String entityName, String userCode, List<String> logs) {
        this.logType = logType;
        this.baseUrl = baseUrl;
        this.endPoint = endPoint;
        this.entityName = entityName;
        this.userCode = userCode;
        this.logs = logs;
    }
}
