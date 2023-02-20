package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.*;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.client.SpotResponseDto;
import co.kurrant.app.admin_api.service.SpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpotServiceImpl implements SpotService {

    private final QSpotRepository  qSpotRepository;
    private final SpotRepository spotRepository;
    private final MealInfoRepository mealInfoRepository;
    private final CorporationMealInfoRepository corporationMealInfoRepository;
    private final QCorporationMealInfoRepository qCorporationMealInfoRepository;
    private final QMealInfoRepository qMealInfoRepository;

    @Override
    public List<SpotResponseDto> getAllSpotList() {

        List<Spot> spotList = spotRepository.findAll();

        List<SpotResponseDto> resultList = new ArrayList<>();
        SpotResponseDto dto = new SpotResponseDto();
        for (Spot spot : spotList){
            //dto에 스팟정보 넣기
            dto.setSpotId(spot.getId());
            dto.setGroupId(spot.getGroup().getId());
//            dto.setGroupName(spot.getGroup().getName());
            /*spot.getAddress().getAddress1();
            dto.setAddress1(spot.getAddress().getAddress1());
            dto.setAddress2(spot.getAddress().getAddress2());
            dto.setLocation(spot.getAddress().getLocation());
            dto.setZipCode(spot.getAddress().getZipCode());
            */
            String createdDate = DateUtils.format(spot.getCreatedDateTime(), "yyyy-MM-dd");
            dto.setCreatedDateTime(DateUtils.stringToDate(createdDate));
            BigInteger spotId = spot.getId();
            //spotId로 mealInfo 찾기
            List<MealInfo> mealInfoList = mealInfoRepository.findAllBySpotId(spotId);

            //SpotId로 CorporaionMealInfo 찾기
            for (int i = 0; i < mealInfoList.size(); i++) {
                DiningType diningType = mealInfoList.get(i).getDiningType();
                if (diningType.getCode() == 0){
                    dto.setDiningType("아침");
                    dto.setMorningUseDays(mealInfoList.get(i).getServiceDays());
                    dto.setMorningDeliveryTime(mealInfoList.get(i).getDeliveryTime().toString());
                    MealInfo mealInfo = qMealInfoRepository.findBySpotId(spotId, diningType.getCode());
                        CorporationMealInfo corporationMealInfo = qCorporationMealInfoRepository.findOneById(mealInfo.getId());
                        if (corporationMealInfo != null) {
                            dto.setLunchSupportPrice(corporationMealInfo.getSupportPrice());
                        }

                } else if (diningType.getCode() == 1) {
                    dto.setDiningType("점심");
                    dto.setLunchUseDays(mealInfoList.get(i).getServiceDays());
                    dto.setLunchDeliveryTime(mealInfoList.get(i).getDeliveryTime().toString());
                    MealInfo mealInfo = qMealInfoRepository.findBySpotId(spotId, diningType.getCode());

                    CorporationMealInfo corporationMealInfo = qCorporationMealInfoRepository.findOneById(mealInfo.getId());
                    if (corporationMealInfo != null) {
                        dto.setLunchSupportPrice(corporationMealInfo.getSupportPrice());
                    }
                } else {
                    dto.setDiningType("저녁");
                    dto.setDinnerUseDays(mealInfoList.get(i).getServiceDays());
                    dto.setDinnerDeliveryTime(mealInfoList.get(i).getDeliveryTime().toString());
                    MealInfo mealInfo = qMealInfoRepository.findBySpotId(spotId, diningType.getCode());

                    CorporationMealInfo corporationMealInfo = qCorporationMealInfoRepository.findOneById(mealInfo.getId());
                    if (corporationMealInfo != null){
                    dto.setLunchSupportPrice(corporationMealInfo.getSupportPrice());
                    }
                }
            }
            resultList.add(dto);
        }

        return resultList;
    }
}
