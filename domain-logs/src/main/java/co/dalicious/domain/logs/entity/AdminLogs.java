package co.dalicious.domain.logs.entity;

import co.dalicious.system.converter.ListToStringConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Table(name = "logs_admin_log")
public class AdminLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("백오피스 로그 id")
    private BigInteger id;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "user_code")
    private String userCode;

    @Column(name = "로그 이름")
    @Convert(converter = ListToStringConverter.class)
    private List<String> logs;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;
}
