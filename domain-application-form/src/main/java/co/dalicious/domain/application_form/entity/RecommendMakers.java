package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.converter.ProgressStatusConverter;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import co.dalicious.system.converter.IdListConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_form__recommend_makers")
public class RecommendMakers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("추천 메이커스 PK")
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

    @Column(name = "user_Id")
    @Convert(converter = IdListConverter.class)
    @Comment("유저 Id")
    private List<BigInteger> userIds;

    @NotNull
    @Column(name = "emb_address", nullable = false)
    @Comment("메이커스 주소")
    private Address address;

    @Size(max = 32)
    @NotNull
    @Column(name = "name", nullable = false, length = 32)
    @Comment("메이커스 이름")
    private String name;

    @Column(name = "phone")
    @Comment("매장 번호")
    private String phone;

    @Column(name = "group_id")
    @Comment("고객사 Id")
    private BigInteger groupId;

    @Convert(converter = ProgressStatusConverter.class)
    @Comment("진행 상황")
    @Column(name = "e_status")
    private ProgressStatus progressStatus;

    @Builder
    public RecommendMakers(List<BigInteger> userId, Address address, String name, String phone, BigInteger groupId, ProgressStatus progressStatus) {
        this.userIds = userId;
        this.address = address;
        this.name = name;
        this.phone = phone;
        this.groupId = groupId;
        this.progressStatus = progressStatus;
    }
}
