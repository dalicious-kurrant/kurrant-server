package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.SpotResponseDto;
import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.mapper.MealInfoMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.system.enums.DiningType;
import co.kurrant.app.admin_api.dto.client.DeleteSpotRequestDto;
import co.kurrant.app.admin_api.dto.client.SaveSpotList;
import co.kurrant.app.admin_api.mapper.SpotMapper;
import co.kurrant.app.admin_api.service.SpotService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpotServiceImpl implements SpotService {

    private final SpotRepository spotRepository;
    private final MealInfoRepository mealInfoRepository;
    private final QCorporationMealInfoRepository qCorporationMealInfoRepository;
    private final QSpotRepository qSpotRepository;
    private final QMealInfoRepository qMealInfoRepository;
    private final SpotMapper spotMapper;
    private final MealInfoMapper mealInfoMapper;

    @Override
    public List<SpotResponseDto> getAllSpotList() {

        List<Spot> spotList = qSpotRepository.findAll();

        List<SpotResponseDto> resultList = new ArrayList<>();
        for (Spot spot : spotList){
            BigInteger spotId = spot.getId();
            //spotId로 mealInfo 찾기
            List<MealInfo> mealInfoList = mealInfoRepository.findAllBySpotId(spotId);

            String diningTypeTemp = null;
            String breakfastUseDays = null;
            String lunchUseDays = null;
            String dinnerUseDays = null;
            String breakfastDeliveryTime = null;
            String lunchDeliveryTime = null;
            String dinnerDeliveryTime = null;
            BigDecimal breakfastSupportPrice = null;
            BigDecimal lunchSupportPrice = null;
            BigDecimal dinnerSupportPrice = null;
            String lastOrderTime = null;

            for (int i = 0; i < mealInfoList.size(); i++) {
                DiningType diningType = mealInfoList.get(i).getDiningType();
                if (diningType.getCode() == 1){
                    diningTypeTemp = "아침";
                    breakfastUseDays = mealInfoList.get(i).getServiceDays();
                    breakfastDeliveryTime = mealInfoList.get(i).getDeliveryTime().toString();
                    MealInfo mealInfo = qMealInfoRepository.findBySpotId(spotId, diningType.getCode());
                    lastOrderTime = mealInfo.getLastOrderTime().toString();
                        CorporationMealInfo corporationMealInfo = qCorporationMealInfoRepository.findOneById(mealInfo.getId());
                        if (corporationMealInfo != null) {
                            breakfastSupportPrice = corporationMealInfo.getSupportPrice();
                        }
                } else if (diningType.getCode() == 2) {
                    diningTypeTemp = "점심";
                    lunchUseDays = mealInfoList.get(i).getServiceDays();
                    lunchDeliveryTime = mealInfoList.get(i).getDeliveryTime().toString();
                    MealInfo mealInfo = qMealInfoRepository.findBySpotId(spotId, diningType.getCode());
                    lastOrderTime = mealInfo.getLastOrderTime().toString();

                    CorporationMealInfo corporationMealInfo = qCorporationMealInfoRepository.findOneById(mealInfo.getId());
                    if (corporationMealInfo != null) {
                        lunchSupportPrice = corporationMealInfo.getSupportPrice();
                    }
                } else {
                    diningTypeTemp = "저녁";
                    dinnerUseDays = mealInfoList.get(i).getServiceDays();
                    dinnerDeliveryTime = mealInfoList.get(i).getDeliveryTime().toString();
                    MealInfo mealInfo = qMealInfoRepository.findBySpotId(spotId, diningType.getCode());
                    lastOrderTime = mealInfo.getLastOrderTime().toString();

                    CorporationMealInfo corporationMealInfo = qCorporationMealInfoRepository.findOneById(mealInfo.getId());
                    if (corporationMealInfo != null){
                        dinnerSupportPrice = corporationMealInfo.getSupportPrice();
                    }
                }
            }


            SpotResponseDto spotResponseDto = spotMapper.toDto(spot, diningTypeTemp,
                    breakfastUseDays, breakfastDeliveryTime, breakfastSupportPrice,
                    lunchUseDays, lunchDeliveryTime, lunchSupportPrice,
                    dinnerUseDays, dinnerDeliveryTime, dinnerSupportPrice, lastOrderTime);
            resultList.add(spotResponseDto);
        }


        return resultList;
    }

    @Override
    public void saveSpotList(SaveSpotList saveSpotList) {

        for (SpotResponseDto spotInfo : saveSpotList.getSaveSpotList()){


            BigDecimal supportPrice = getSupportPrice(spotInfo);

            //deliveryTime, diningType, lastOrderTime
            LocalTime lastOrderTime = LocalTime.parse(spotInfo.getLastOrderTime());
            String serviceDays = getServieDays(spotInfo);

            //address 생성
            CreateAddressRequestDto createAddressRequestDto = makeCreateAddressRequestDto(spotInfo.getZipCode(), spotInfo.getAddress1(), spotInfo.getAddress2());
            Address address = new Address(createAddressRequestDto);
            String[] split = spotInfo.getDiningType().split(",");
            List<DiningType> diningTypes = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                diningTypes.add(DiningType.ofCode(Integer.parseInt(split[i])));
            }


            Spot spot = spotRepository.save(spotMapper.toEntity(spotInfo, address, diningTypes));


            //mealinfo 저장
            List<MealInfo> mealInfoList = new ArrayList<>();
            if (spotInfo.getDiningType().length() != 1 && spotInfo.getDiningType().length() != 0){
                String[] diningTypeArray = spotInfo.getDiningType().split(",");
                //spot 저장
                //mealInfo는 diningType마다 존재하므로 하나씩 넣어준다.
                for (int i = 0; i < diningTypeArray.length; i++) {
                    String deliveryTime = getDeliveryTime(spotInfo);
                    mealInfoList.add(mealInfoRepository.save(mealInfoMapper.toEntity(spotInfo, deliveryTime, serviceDays, diningTypeArray[i], lastOrderTime, spot)));
                    /*corporationMealInfoRepository.save(corporationMealInfoMapper.toEntity(DiningType.valueOf(spotInfo.getDiningType()), LocalTime.parse(deliveryTime),
                            lastOrderTime, serviceDays, spot, supportPrice));*/
                }
            }
        }

    }

    @Override
    public void deleteSpot(DeleteSpotRequestDto deleteSpotRequestDto) {
        //요청받은 spot을 비활성한다.
        for (BigInteger spotId : deleteSpotRequestDto.getSpotIdList()){
            long result = qSpotRepository.deleteSpot(spotId);
            if (result != 1){
                throw new ApiException(ExceptionEnum.SPOT_PATCH_ERROR);
            }
        }
    }

    private CreateAddressRequestDto makeCreateAddressRequestDto(String zipCode, String address1, String address2) {
        CreateAddressRequestDto createAddressRequestDto = new CreateAddressRequestDto();
        createAddressRequestDto.setAddress1(address1);
        createAddressRequestDto.setAddress2(address2);
        createAddressRequestDto.setZipCode(zipCode);
        return  createAddressRequestDto;

    }

    private BigDecimal getSupportPrice(SpotResponseDto spotInfo) {

        BigDecimal result = null;

        if (spotInfo.getBreakfastSupportPrice() != null){
            result = spotInfo.getBreakfastSupportPrice();
        }
        if (spotInfo.getLunchSupportPrice() != null){
            result = spotInfo.getBreakfastSupportPrice();
        }
        if (spotInfo.getDinnerSupportPrice() != null){
            result = spotInfo.getBreakfastSupportPrice();
        }
        return result;
    }

    private String getServieDays(SpotResponseDto spotInfo) {
        String result = null;
        if (spotInfo.getBreakfastUseDays() != null){
            result = spotInfo.getBreakfastUseDays();
        }
        if (spotInfo.getBreakfastUseDays() != null){
            result = spotInfo.getLunchUseDays();
        }
        if (spotInfo.getBreakfastUseDays() != null){
            result = spotInfo.getDinnerUseDays();
        }
        return  result;
    }

    private String getDeliveryTime(SpotResponseDto spotInfo) {
        if (spotInfo.getBreakfastDeliveryTime() != null){
            return spotInfo.getBreakfastDeliveryTime();
        }
        if (spotInfo.getBreakfastDeliveryTime() != null){
            return spotInfo.getLunchDeliveryTime();
        }
        if (spotInfo.getBreakfastDeliveryTime() != null){
            return spotInfo.getDinnerDeliveryTime();
        }
        return "null";
    }
}
