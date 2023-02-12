package co.dalicious.domain.review.entity;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review__review")
public class Reviews{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("리뷰 PK")
    private BigInteger id;

    @Column(name = "content" ,nullable = false)
    @Comment("리뷰 내용-최소 10자 이상")
    private String content;

    @Column(name = "content_origin")
    @Comment("원본 리뷰 내용")
    private String contentOrigin;

    @Embedded
    private Image image;

    @Column(name = "image_origin")
    @Comment("원본 리뷰 이미지 경로")
    private String imageOrigin;

    @Column(name = "satisfaction", nullable = false)
    @Comment("만족도")
    private Integer satisfaction;

    @Column(name = "satisfaction_origin")
    @Comment("원본 만족도")
    private Integer satisfactionOrigin;

    @Column(name = "for_makers")
    @Comment("사장님에게만 보이기")
    private Boolean forMakers;

    @Column(name = "is_delete")
    @Comment("삭제 여부")
    private Boolean isDelete;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "updated_datetime",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @Comment("유저 ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id")
    @Comment("주문상품 ID")
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id")
    @Comment("푸드 ID")
    private Food food;

    @Builder
    public Reviews(String content, String contentOrigin, String imageOrigin, Integer satisfaction, Integer satisfactionOrigin, Boolean forMakers, User user, OrderItem orderItem, Food food) {
        this.content = content;
        this.contentOrigin = contentOrigin;
        this.imageOrigin = imageOrigin;
        this.satisfaction = satisfaction;
        this.satisfactionOrigin = satisfactionOrigin;
        this.forMakers = forMakers;
        this.user = user;
        this.orderItem = orderItem;
        this.food = food;
    }

}
