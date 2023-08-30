package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.application_form.converter.ProgressStatusConverter;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_form__requested_corporation")
public class RequestedCorporation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("신청한 고객사 PK")
    private BigInteger id;

    @Column(name = "user_name")
    @Comment("신청한 유저 이름")
    private String username;

    @NotNull
    @Column(name = "region", nullable = false)
    @Comment("스팟 주소")
    private String address;

    @Column(name = "user_phone")
    @Comment("유저 핸드폰 번호")
    private String phone;

    @Column(name = "memo")
    @Comment("메모")
    private String memo;

    @Convert(converter = ProgressStatusConverter.class)
    @Comment("진행 상황")
    private ProgressStatus progressStatus;

    @Builder
    public RequestedCorporation(String username, String address, String phone, String memo, ProgressStatus progressStatus) {
        this.username = username;
        this.address = address;
        this.phone = phone;
        this.memo = memo;
        this.progressStatus = progressStatus;
    }
}
