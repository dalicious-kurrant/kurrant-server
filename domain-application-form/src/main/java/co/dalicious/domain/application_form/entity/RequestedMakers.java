package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
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
@Table(name = "application_form__requested_makers")
public class RequestedMakers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("신청한 메이커스 PK")
    private BigInteger id;

    @Column(name = "user_name")
    @Comment("신청한 유저 이름")
    private String username;

    @Column(name = "makers_name")
    @Comment("신청한 메이커스 이름")
    private String makersName;

    @NotNull
    @Column(name = "emb_address", nullable = false)
    @Comment("메이커스 주소")
    private Address address;

    @Column(name = "phone")
    @Comment("번호")
    private String phone;

    @Column(name = "memo")
    @Comment("메모")
    private String memo;

    @Column(name = "main_product")
    @Comment("메인 상품")
    private String mainProduct;

    @Convert(converter = ProgressStatusConverter.class)
    @Comment("진행 상황")
    private ProgressStatus progressStatus;

    @Builder
    public RequestedMakers(String username, String makersName, Address address, String phone, String memo, String mainProduct, ProgressStatus progressStatus) {
        this.username = username;
        this.makersName = makersName;
        this.address = address;
        this.phone = phone;
        this.memo = memo;
        this.mainProduct = mainProduct;
        this.progressStatus = progressStatus;
    }
}
