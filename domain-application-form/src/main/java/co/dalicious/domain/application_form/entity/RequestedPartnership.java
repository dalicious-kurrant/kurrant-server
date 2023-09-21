package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.application_form.converter.ProgressStatusConverter;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.sql.Timestamp;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@Getter
@Setter
@Table(name = "application_form__requested_partnership")
public class RequestedPartnership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("홈페이지 신청 PK")
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

    @Column(name = "user_name")
    @Comment("신청한 유저 이름")
    private String username;

    @NotNull
    @Column(name = "address", nullable = false)
    @Comment("주소")
    private String address;

    @Column(name = "user_phone")
    @Comment("유저 핸드폰 번호")
    private String phone;

    @Column(name = "memo")
    @Comment("메모")
    private String memo;

    @Convert(converter = ProgressStatusConverter.class)
    @Comment("진행 상황")
    @Column(name = "e_status")
    private ProgressStatus progressStatus;

    public RequestedPartnership(String username, String address, String phone, String memo, ProgressStatus progressStatus) {
        this.username = username;
        this.address = address;
        this.phone = phone;
        this.memo = memo;
        this.progressStatus = progressStatus;
    }
}
