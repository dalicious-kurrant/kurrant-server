package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RequestedMakers extends RequestedPartnership {

    @Column(name = "makers_name")
    @Comment("신청한 메이커스 이름")
    private String makersName;

    @Column(name = "main_product")
    @Comment("메인 상품")
    private String mainProduct;

    @Builder
    public RequestedMakers(String username, String address, String phone, String memo, ProgressStatus progressStatus, String makersName, String mainProduct) {
        super(username, address, phone, memo, progressStatus);
        this.makersName = makersName;
        this.mainProduct = mainProduct;
    }
}
