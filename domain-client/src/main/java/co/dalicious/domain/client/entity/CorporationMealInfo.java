package co.dalicious.domain.client.entity;

import co.dalicious.domain.client.entity.embeddable.DeliverySchedule;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CorporationMealInfo extends MealInfo{

    @ElementCollection
    @Comment("서비스 일자 및 지원금")
    @CollectionTable(name = "client__service_days_and_support_price")
    private List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPrices;

    @Builder
    public CorporationMealInfo(DiningType diningType, List<LocalTime> deliveryTimes, DayAndTime membershipBenefitTime, DayAndTime lastOrderTime, List<Days> serviceDays, Group group, List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPrices) {
        super(diningType, deliveryTimes, membershipBenefitTime, lastOrderTime, serviceDays, group);
        this.serviceDaysAndSupportPrices = serviceDaysAndSupportPrices;
    }

    public void updateCorporationMealInfo(CorporationMealInfo mealInfo) {
        super.updateMealInfo(mealInfo);
        this.serviceDaysAndSupportPrices = mealInfo.getServiceDaysAndSupportPrices();
    }

    public void updateServiceDaysAndSupportPrice(List<Days> serviceDays, List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPrices) {
        super.updateMealInfo(serviceDays);
        this.serviceDaysAndSupportPrices = serviceDaysAndSupportPrices;
    }

    public void updateSupportDays(List<Days> serviceDays) {
        // 변경된 서비스 요일이 지원 요일에 포함되어 있다면 변경
        List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = this.serviceDaysAndSupportPrices;
        if(serviceDaysAndSupportPriceList == null || serviceDaysAndSupportPriceList.isEmpty()) return;

        List<ServiceDaysAndSupportPrice> updateServiceDaysAndSupportDays = new ArrayList<>();

        for(ServiceDaysAndSupportPrice serviceDaysAndSupportPrice : serviceDaysAndSupportPriceList) {
            List<Days> supportDays = serviceDaysAndSupportPrice.getSupportDays();
            supportDays.retainAll(serviceDays);

            if(!supportDays.isEmpty()){
                serviceDaysAndSupportPrice.updateServiceDays(supportDays);
                updateServiceDaysAndSupportDays.add(serviceDaysAndSupportPrice);
            }
        }

        this.serviceDaysAndSupportPrices = updateServiceDaysAndSupportDays;
    }
}
