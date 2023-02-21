package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.*;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.client.SpotResponseDto;
import co.kurrant.app.admin_api.mapper.SpotMapper;
import co.kurrant.app.admin_api.service.SpotService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SpotServiceImpl implements SpotService {

    private final QSpotRepository  qSpotRepository;
    private final SpotRepository spotRepository;
    private final MealInfoRepository mealInfoRepository;
    private final CorporationMealInfoRepository corporationMealInfoRepository;
    private final GroupRepository groupRepository;
    private final QCorporationMealInfoRepository qCorporationMealInfoRepository;
    private final QMealInfoRepository qMealInfoRepository;
    private final SpotMapper spotMapper;

    @Override
    public List<SpotResponseDto> getAllSpotList() {

        List<Spot> spotList = spotRepository.findAll();

        List<SpotResponseDto> resultList = new ArrayList<>();
        for (Spot spot : spotList){
            BigInteger spotId = spot.getId();
            //spotId로 mealInfo 찾기
            List<MealInfo> mealInfoList = mealInfoRepository.findAllBySpotId(spotId);

            String diningTypeTemp = null;
            String morningUseDays = null;
            String lunchUseDays = null;
            String dinnerUseDays = null;
            String morningDeliveryTime = null;
            String lunchDeliveryTime = null;
            String dinnerDeliveryTime = null;
            BigDecimal morningSupportPrice = null;
            BigDecimal lunchSupportPrice = null;
            BigDecimal dinnerSupportPrice = null;


            for (int i = 0; i < mealInfoList.size(); i++) {
                DiningType diningType = mealInfoList.get(i).getDiningType();
                if (diningType.getCode() == 1){
                    diningTypeTemp = "아침";
                    morningUseDays = mealInfoList.get(i).getServiceDays();
                    morningDeliveryTime = mealInfoList.get(i).getDeliveryTime().toString();
                    MealInfo mealInfo = qMealInfoRepository.findBySpotId(spotId, diningType.getCode());
                        CorporationMealInfo corporationMealInfo = qCorporationMealInfoRepository.findOneById(mealInfo.getId());
                        if (corporationMealInfo != null) {
                            morningSupportPrice = corporationMealInfo.getSupportPrice();
                        }
                } else if (diningType.getCode() == 2) {
                    diningTypeTemp = "점심";
                    lunchUseDays = mealInfoList.get(i).getServiceDays();
                    lunchDeliveryTime = mealInfoList.get(i).getDeliveryTime().toString();
                    MealInfo mealInfo = qMealInfoRepository.findBySpotId(spotId, diningType.getCode());

                    CorporationMealInfo corporationMealInfo = qCorporationMealInfoRepository.findOneById(mealInfo.getId());
                    if (corporationMealInfo != null) {
                        lunchSupportPrice = corporationMealInfo.getSupportPrice();
                    }
                } else {
                    diningTypeTemp = "저녁";
                    dinnerUseDays = mealInfoList.get(i).getServiceDays();
                    dinnerDeliveryTime = mealInfoList.get(i).getDeliveryTime().toString();
                    MealInfo mealInfo = qMealInfoRepository.findBySpotId(spotId, diningType.getCode());

                    CorporationMealInfo corporationMealInfo = qCorporationMealInfoRepository.findOneById(mealInfo.getId());
                    if (corporationMealInfo != null){
                        dinnerSupportPrice = corporationMealInfo.getSupportPrice();
                    }
                }
            }

            SpotResponseDto spotResponseDto = spotMapper.toDto(spot, diningTypeTemp,
                    morningUseDays, morningDeliveryTime, morningSupportPrice,
                    lunchUseDays, lunchDeliveryTime, lunchSupportPrice,
                    dinnerUseDays, dinnerDeliveryTime, dinnerSupportPrice);
            resultList.add(spotResponseDto);
        }


        return resultList;
    }
}
