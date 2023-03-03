package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.SpotResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.mapper.MealInfoMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DiningTypesUtils;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.dto.client.DeleteSpotRequestDto;
import co.kurrant.app.admin_api.dto.client.SaveSpotList;
import co.kurrant.app.admin_api.mapper.SpotMapper;
import co.kurrant.app.admin_api.service.SpotService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SpotServiceImpl implements SpotService {

    private final SpotRepository spotRepository;
    private final MealInfoRepository mealInfoRepository;
    private final QSpotRepository qSpotRepository;
    private final SpotMapper spotMapper;
    private final QGroupRepository qGroupRepository;

    @Override
    public List<SpotResponseDto> getAllSpotList(Integer status) {

        List<Spot> spotList = qSpotRepository.findAllByStatus(status);

        List<SpotResponseDto> resultList = new ArrayList<>();
        for (Spot spot : spotList) {
            SpotResponseDto spotResponseDto = spotMapper.toDto(spot);
            resultList.add(spotResponseDto);
        }

        return resultList;
    }

    @Override
    @Transactional
    public void saveSpotList(SaveSpotList saveSpotList) throws ParseException {
        List<SpotResponseDto> spotResponseDtos = saveSpotList.getSaveSpotList();
        List<BigInteger> spotIds = spotResponseDtos.stream()
                .map(SpotResponseDto::getSpotId)
                .toList();
        Set<BigInteger> groupIds = spotResponseDtos.stream()
                .map(SpotResponseDto::getGroupId)
                .collect(Collectors.toSet());
        List<Group> groups = qGroupRepository.findAllByIds(groupIds);

        // FIXME 스팟 수정
        List<Spot> updateSpots = qSpotRepository.findAllByIds(spotIds);
        List<BigInteger> updateSpotIds = updateSpots.stream()
                .map(Spot::getId)
                .toList();
        Map<Spot, SpotResponseDto> spotMap = new HashMap<>();
        for (Spot updateSpot : updateSpots) {
            spotResponseDtos.stream()
                    .filter(v -> v.getSpotId().equals(updateSpot.getId()))
                    .findAny().ifPresent(spotResponseDto -> spotMap.put(updateSpot, spotResponseDto));
        }
        // TODO: 그룹이 가지고 있지 않은 스팟이면 생성금지
        for (Spot spot : spotMap.keySet()) {
            List<DiningType> spotDiningTypes = DiningTypesUtils.stringToDiningTypes(spotMap.get(spot).getDiningType());

            MealInfo morningMealInfo = (spotDiningTypes.contains(DiningType.MORNING)) ? spotMapper.toMealInfo(spot, DiningType.MORNING, spotMap.get(spot).getBreakfastLastOrderTime(), spotMap.get(spot).getBreakfastDeliveryTime(), spotMap.get(spot).getBreakfastUseDays(), spotMap.get(spot).getBreakfastSupportPrice()) : null;
            MealInfo lunchMealInfo = (spotDiningTypes.contains(DiningType.LUNCH)) ? spotMapper.toMealInfo(spot, DiningType.LUNCH, spotMap.get(spot).getLunchLastOrderTime(), spotMap.get(spot).getLunchDeliveryTime(), spotMap.get(spot).getLunchUseDays(), spotMap.get(spot).getLunchSupportPrice()) : null;
            MealInfo dinnerMealInfo = (spotDiningTypes.contains(DiningType.DINNER)) ? spotMapper.toMealInfo(spot, DiningType.DINNER, spotMap.get(spot).getDinnerLastOrderTime(), spotMap.get(spot).getDinnerDeliveryTime(), spotMap.get(spot).getDinnerUseDays(), spotMap.get(spot).getDinnerSupportPrice()) : null;
            // CASE 1: 스팟에 식사 정보가 존재하지 않을 경우
            if (spot.getMealInfos().isEmpty()) {
                if (morningMealInfo != null) {
                    mealInfoRepository.save(morningMealInfo);
                }
                if (lunchMealInfo != null) {
                    mealInfoRepository.save(lunchMealInfo);
                }
                if (dinnerMealInfo != null) {
                    mealInfoRepository.save(dinnerMealInfo);
                }
                spot.updateSpot(spotMap.get(spot));
                spot.updatedDiningTypes(morningMealInfo, lunchMealInfo, dinnerMealInfo);
            }
            // CASE 2: 스팟에 식사 정보가 존재하지만, 요청값에 없는 경우
            else if (morningMealInfo == null && lunchMealInfo == null && dinnerMealInfo == null) {
                mealInfoRepository.deleteAll(spot.getMealInfos());
                spot.updateSpot(spotMap.get(spot));
                spot.updatedDiningTypes(null, null, null);
            }
            // CASE 3: 스팟에 식사 정보가 존재하며 요청값에도 존재할 경우
            else {
                List<MealInfo> mealInfos = spot.getMealInfos();
                Map<DiningType, MealInfo> updatedMealInfos = new HashMap<>();
                updatedMealInfos.put(DiningType.MORNING, morningMealInfo);
                updatedMealInfos.put(DiningType.LUNCH, lunchMealInfo);
                updatedMealInfos.put(DiningType.DINNER, dinnerMealInfo);

                for (MealInfo mealInfo : mealInfos) {
                    DiningType diningType = mealInfo.getDiningType();
                    if (updatedMealInfos.containsKey(diningType)) {
                        MealInfo updatedMealInfo = updatedMealInfos.get(diningType);
                        if (updatedMealInfo != null) {
                            if(updatedMealInfo instanceof CorporationMealInfo corporationMealInfo) {
                                ((CorporationMealInfo) mealInfo).updateMealInfo(corporationMealInfo);
                            } else {
                                mealInfo.updateMealInfo(updatedMealInfo);
                            }
                        } else {
                            mealInfoRepository.delete(mealInfo);
                        }
                        updatedMealInfos.remove(diningType);
                    } else {
                        mealInfoRepository.delete(mealInfo);
                    }
                }

                for (Map.Entry<DiningType, MealInfo> entry : updatedMealInfos.entrySet()) {
                    DiningType diningType = entry.getKey();
                    MealInfo updatedMealInfo = entry.getValue();
                    if (updatedMealInfo != null) {
                        switch (diningType) {
                            case MORNING -> {
                                if (morningMealInfo != null) mealInfoRepository.save(morningMealInfo);
                            }
                            case LUNCH -> {
                                if (lunchMealInfo != null) mealInfoRepository.save(lunchMealInfo);
                            }
                            case DINNER -> {
                                if (dinnerMealInfo != null) mealInfoRepository.save(dinnerMealInfo);
                            }
                        }
                    }
                }
                spot.updateSpot(spotMap.get(spot));
                spot.updatedDiningTypes(morningMealInfo, lunchMealInfo, dinnerMealInfo);
            }
        }

        // FIXME 스팟 생성
        List<SpotResponseDto> createSpots = spotResponseDtos.stream()
                .filter(v -> !updateSpotIds.contains(v.getSpotId()))
                .toList();
        for (SpotResponseDto createSpot : createSpots) {
            Spot spot = spotMapper.toEntity(createSpot, Group.getGroup(groups, createSpot.getGroupId()), DiningTypesUtils.stringToDiningTypes(createSpot.getDiningType()));
            spotRepository.save(spot);
            List<DiningType> groupDiningTypes = spot.getGroup().getDiningTypes();

            MealInfo morningMealInfo = (groupDiningTypes.contains(DiningType.MORNING)) ? spotMapper.toMealInfo(spot, DiningType.MORNING, createSpot.getBreakfastLastOrderTime(), createSpot.getBreakfastDeliveryTime(), createSpot.getBreakfastUseDays(), createSpot.getBreakfastSupportPrice()) : null;
            MealInfo lunchMealInfo = (groupDiningTypes.contains(DiningType.LUNCH)) ? spotMapper.toMealInfo(spot, DiningType.LUNCH, createSpot.getLunchLastOrderTime(), createSpot.getLunchDeliveryTime(), createSpot.getLunchUseDays(), createSpot.getLunchSupportPrice()) : null;
            MealInfo dinnerMealInfo = (groupDiningTypes.contains(DiningType.DINNER)) ? spotMapper.toMealInfo(spot, DiningType.DINNER, createSpot.getDinnerLastOrderTime(), createSpot.getDinnerDeliveryTime(), createSpot.getDinnerUseDays(), createSpot.getDinnerSupportPrice()) : null;
            if (morningMealInfo != null) mealInfoRepository.save(morningMealInfo);
            if (lunchMealInfo != null) mealInfoRepository.save(lunchMealInfo);
            if (dinnerMealInfo != null) mealInfoRepository.save(dinnerMealInfo);

            spot.updateDiningTypes(spot.getDiningTypes());
        }
    }

    @Override
    public void deleteSpot(DeleteSpotRequestDto deleteSpotRequestDto) {
        //요청받은 spot을 비활성한다.
        for (BigInteger spotId : deleteSpotRequestDto.getSpotIdList()) {
            long result = qSpotRepository.deleteSpot(spotId);
            if (result != 1) {
                throw new ApiException(ExceptionEnum.SPOT_PATCH_ERROR);
            }
        }
    }

    private CreateAddressRequestDto makeCreateAddressRequestDto(String zipCode, String address1, String address2) {
        CreateAddressRequestDto createAddressRequestDto = new CreateAddressRequestDto();
        createAddressRequestDto.setAddress1(address1);
        createAddressRequestDto.setAddress2(address2);
        createAddressRequestDto.setZipCode(zipCode);
        return createAddressRequestDto;

    }

    private BigDecimal getSupportPrice(SpotResponseDto spotInfo) {

        BigDecimal result = null;

        if (spotInfo.getBreakfastSupportPrice() != null) {
            result = spotInfo.getBreakfastSupportPrice();
        }
        if (spotInfo.getLunchSupportPrice() != null) {
            result = spotInfo.getBreakfastSupportPrice();
        }
        if (spotInfo.getDinnerSupportPrice() != null) {
            result = spotInfo.getBreakfastSupportPrice();
        }
        return result;
    }

    private String getServieDays(SpotResponseDto spotInfo) {
        String result = null;
        if (spotInfo.getBreakfastUseDays() != null) {
            result = spotInfo.getBreakfastUseDays();
        }
        if (spotInfo.getBreakfastUseDays() != null) {
            result = spotInfo.getLunchUseDays();
        }
        if (spotInfo.getBreakfastUseDays() != null) {
            result = spotInfo.getDinnerUseDays();
        }
        return result;
    }

    private String getDeliveryTime(SpotResponseDto spotInfo) {
        if (spotInfo.getBreakfastDeliveryTime() != null) {
            return spotInfo.getBreakfastDeliveryTime();
        }
        if (spotInfo.getBreakfastDeliveryTime() != null) {
            return spotInfo.getLunchDeliveryTime();
        }
        if (spotInfo.getBreakfastDeliveryTime() != null) {
            return spotInfo.getDinnerDeliveryTime();
        }
        return "null";
    }
}