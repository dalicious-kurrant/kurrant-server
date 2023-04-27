package co.dalicious.domain.client.entity.embeddable;

import co.dalicious.system.converter.DaysListConverter;
import co.dalicious.system.enums.Days;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ServiceDaysAndSupportPrice {

    @Convert(converter = DaysListConverter.class)
    @Column(name = "emb_support_days")
    @Comment("서비스 이용일")
    private List<Days> supportDays;

    @NotNull
    @Column(name = "daily_support_price", nullable = false,columnDefinition = "DECIMAL(15, 2)")
    @Comment("식사 일정별(아침, 점심, 저녁) 식사 지원금")
    private BigDecimal supportPrice;

    @Builder
    public ServiceDaysAndSupportPrice(List<Days> supportDays, BigDecimal supportPrice) {
        this.supportDays = supportDays;
        this.supportPrice = supportPrice;
    }

    public void updateSupportPrice(BigDecimal supportPrice) {
        this.supportPrice = supportPrice;
    }

    public void updateServiceDays(List<Days> supportDays) {
        this.supportDays = supportDays;
    }
}
