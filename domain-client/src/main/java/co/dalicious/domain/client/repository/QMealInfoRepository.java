package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.types.Path;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static co.dalicious.domain.client.entity.QCorporationMealInfo.corporationMealInfo;
import static co.dalicious.domain.client.entity.QMealInfo.mealInfo;

@Repository
@RequiredArgsConstructor
public class QMealInfoRepository {

    private final JPAQueryFactory queryFactory;


    public void updateSpotDetailSupportPrice(BigInteger groupId, UpdateSpotDetailRequestDto updateSpotDetailRequestDto) {
        //아침 지원금 수정
        if (updateSpotDetailRequestDto.getBreakfastSupportPrice() != null){
            queryFactory.update(corporationMealInfo)
                    .set(corporationMealInfo.supportPrice, updateSpotDetailRequestDto.getBreakfastSupportPrice())
                    .where(corporationMealInfo.diningType.eq(DiningType.MORNING),
                            corporationMealInfo.group.id.eq(groupId))
                    .execute();
        }
        //점심 지원금 수정
        if (updateSpotDetailRequestDto.getLunchSupportPrice() != null){
            queryFactory.update(corporationMealInfo)
                    .set(corporationMealInfo.supportPrice, updateSpotDetailRequestDto.getLunchSupportPrice())
                    .where(corporationMealInfo.diningType.eq(DiningType.LUNCH),
                            corporationMealInfo.group.id.eq(groupId))
                    .execute();
        }
        //저녁 지원금 수정
        if (updateSpotDetailRequestDto.getDinnerSupportPrice() != null) {
            queryFactory.update(corporationMealInfo)
                    .set(corporationMealInfo.supportPrice, updateSpotDetailRequestDto.getDinnerSupportPrice())
                    .where(corporationMealInfo.diningType.eq(DiningType.DINNER),
                            corporationMealInfo.group.id.eq(groupId))
                    .execute();
        }
    }

    public void updateSpotDetailServiceDays(BigInteger groupId, UpdateSpotDetailRequestDto updateSpotDetailRequestDto, Integer diningType) {

        queryFactory.update(corporationMealInfo)
                .set(corporationMealInfo.serviceDays, updateSpotDetailRequestDto.getServiceDays())
                .where(corporationMealInfo.diningType.eq(DiningType.ofCode(diningType)),
                        corporationMealInfo.group.id.eq(groupId))
                .execute();
    }

    public void updateSpotDetailDelete1(String diningType, BigInteger groupId, String serviceDays) {
        queryFactory.delete(corporationMealInfo)
                .where(corporationMealInfo.diningType.ne(DiningType.ofCode(Integer.valueOf(diningType))),
                        corporationMealInfo.group.id.eq(groupId))
                .execute();

        if (!serviceDays.isEmpty()){
            queryFactory.update(corporationMealInfo)
                    .set(corporationMealInfo.serviceDays, serviceDays)
                    .where(corporationMealInfo.group.id.eq(groupId))
                    .execute();
        }
    }

    public void updateSpotDetailDelete2(String diningType, String diningType2, BigInteger groupId, String serviceDays) {

        DiningType targetDiningType = null;
        DiningType[] diningTypes = DiningType.values();
        for (DiningType dt : diningTypes) {
            if (!dt.equals(DiningType.ofCode(Integer.valueOf(diningType))) && !dt.equals(DiningType.ofCode(Integer.valueOf(diningType2)))) {
                targetDiningType = dt;
            }
        }

        queryFactory.delete(corporationMealInfo)
                .where(corporationMealInfo.diningType.eq(targetDiningType),
                        corporationMealInfo.group.id.eq(groupId))
                .execute();

        if (!serviceDays.isEmpty()) {
            queryFactory.update(corporationMealInfo)
                    .set(corporationMealInfo.serviceDays, serviceDays)
                    .where(corporationMealInfo.group.id.eq(groupId))
                    .execute();
        }
    }
}
