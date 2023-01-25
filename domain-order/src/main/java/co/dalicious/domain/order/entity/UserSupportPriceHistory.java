package co.dalicious.domain.order.entity;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.converter.DiningTypeConverter;
import co.dalicious.system.util.enums.DiningType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user__support_price_history")
public class  UserSupportPriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("사용자 PK")
    private BigInteger id;

    @ManyToOne(optional = false)
    @JoinColumn
    @Comment("유저")
    private User user;

    @ManyToOne
    @JoinColumn
    @Comment("그룹")
    private Group group;

    @Column(columnDefinition = "Decimal(15,2) DEFAULT '0.00'")
    @Comment("사용 지원금")
    private BigDecimal usingSupportPrice;

    @Comment("서비스일")
    private LocalDate serviceDate;

    @Convert(converter = DiningTypeConverter.class)
    @Comment("식사 타입")
    private DiningType diningType;

    @OneToOne(optional = false)
    @JoinColumn
    @Comment("지원금 사용 아이템")
    private OrderItem orderItem;

    @Column(columnDefinition = "tinyint(1) default 1")
    @Comment("지원금 사용 취소 유무")
    private Boolean status;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;
}
