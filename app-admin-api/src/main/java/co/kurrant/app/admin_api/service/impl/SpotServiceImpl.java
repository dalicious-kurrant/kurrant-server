package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.client.dto.SpotResponseDto;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.mapper.MealInfoMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DiningTypesUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.client.SaveSpotList;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.SpotMapper;
import co.kurrant.app.admin_api.service.SpotService;
import com.sun.xml.bind.v2.TODO;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SpotServiceImpl implements SpotService {

    private final SpotRepository spotRepository;
    private final QSpotRepository qSpotRepository;
    private final SpotMapper spotMapper;
    private final QGroupRepository qGroupRepository;
    private final GroupMapper groupMapper;
    private final MealInfoMapper mealInfoMapper;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CorporationRepository corporationRepository;
    private final MealInfoRepository mealInfoRepository;
    private final QMealInfoRepository qMealInfoRepository;
    private final CorporaionMealInfoRepository corporaionMealInfoRepository;
    private final QCorporationRepository qCorporationRepository;


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

            MealInfo morningMealInfo = (spotDiningTypes.contains(DiningType.MORNING)) ? spotMapper.toMealInfo(spot.getGroup(), DiningType.MORNING, spotMap.get(spot).getBreakfastLastOrderTime(), spotMap.get(spot).getBreakfastDeliveryTime(), spotMap.get(spot).getBreakfastUseDays(), spotMap.get(spot).getBreakfastSupportPrice(), spotMap.get(spot).getBreakfastMembershipBenefitTime()) : null;
            MealInfo lunchMealInfo = (spotDiningTypes.contains(DiningType.LUNCH)) ? spotMapper.toMealInfo(spot.getGroup(), DiningType.LUNCH, spotMap.get(spot).getLunchLastOrderTime(), spotMap.get(spot).getLunchDeliveryTime(), spotMap.get(spot).getLunchUseDays(), spotMap.get(spot).getLunchSupportPrice(), spotMap.get(spot).getLunchMembershipBenefitTime()) : null;
            MealInfo dinnerMealInfo = (spotDiningTypes.contains(DiningType.DINNER)) ? spotMapper.toMealInfo(spot.getGroup(), DiningType.DINNER, spotMap.get(spot).getDinnerLastOrderTime(), spotMap.get(spot).getDinnerDeliveryTime(), spotMap.get(spot).getDinnerUseDays(), spotMap.get(spot).getDinnerSupportPrice(), spotMap.get(spot).getDinnerMembershipBenefitTime()) : null;
            // CASE 1: 스팟에 식사 정보가 존재하지 않을 경우
            if (spot.getMealInfos().isEmpty()) {
                if (morningMealInfo != null) {
                    MealInfo mealInfo = spot.getGroup().getMealInfo(DiningType.MORNING);
                    mealInfo.updateMealInfo(morningMealInfo);
                }
                if (lunchMealInfo != null) {
                    MealInfo mealInfo = spot.getGroup().getMealInfo(DiningType.LUNCH);
                    mealInfo.updateMealInfo(lunchMealInfo);
                }
                if (dinnerMealInfo != null) {
                    MealInfo mealInfo = spot.getGroup().getMealInfo(DiningType.DINNER);
                    mealInfo.updateMealInfo(dinnerMealInfo);
                }
                spot.updateSpot(spotMap.get(spot));
                spot.updatedDiningTypes(morningMealInfo, lunchMealInfo, dinnerMealInfo);
            }
            // CASE 2: 스팟에 식사 정보가 존재하지만, 요청값에 없는 경우
            else if (morningMealInfo == null && lunchMealInfo == null && dinnerMealInfo == null) {
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
                            if (updatedMealInfo instanceof CorporationMealInfo corporationMealInfo) {
                                ((CorporationMealInfo) mealInfo).updateMealInfo(corporationMealInfo);
                            } else {
                                mealInfo.updateMealInfo(updatedMealInfo);
                            }
                        }
                        updatedMealInfos.remove(diningType);
                    }
                }

                for (Map.Entry<DiningType, MealInfo> entry : updatedMealInfos.entrySet()) {
                    DiningType diningType = entry.getKey();
                    MealInfo updatedMealInfo = entry.getValue();
                    if (updatedMealInfo != null) {
                        switch (diningType) {
                            case MORNING -> {
                                if (morningMealInfo != null) {
                                    MealInfo mealInfo = spot.getGroup().getMealInfo(DiningType.MORNING);
                                    mealInfo.updateMealInfo(morningMealInfo);
                                }
                            }
                            case LUNCH -> {
                                if (lunchMealInfo != null) {
                                    MealInfo mealInfo = spot.getGroup().getMealInfo(DiningType.LUNCH);
                                    mealInfo.updateMealInfo(lunchMealInfo);
                                }
                            }
                            case DINNER -> {
                                if (dinnerMealInfo != null) {
                                    MealInfo mealInfo = spot.getGroup().getMealInfo(DiningType.MORNING);
                                    mealInfo.updateMealInfo(dinnerMealInfo);
                                }
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
            if (Group.getGroup(groups, createSpot.getGroupId()) == null) {
                throw new IllegalIdentifierException("(상세스팟 아이디:" + createSpot.getSpotId().toString() + ") 등록되어있지 않은 그룹입니다.");
            }
            spotRepository.save(spot);

            spot.updateDiningTypes(spot.getDiningTypes());
        }

    }

    @Override
    public void deleteSpot(List<BigInteger> spotIdList) {
        //요청받은 spot을 비활성한다.
        qSpotRepository.deleteSpots(spotIdList);
    }

    @Override
    public List<GroupDto.Group> getGroupList() {
        List<Group> groups = groupRepository.findAll();
        return groupMapper.groupsToDtos(groups);
    }

    private CreateAddressRequestDto makeCreateAddressRequestDto(String zipCode, String address1, String address2) {
        CreateAddressRequestDto createAddressRequestDto = new CreateAddressRequestDto();
        createAddressRequestDto.setAddress1(address1);
        createAddressRequestDto.setAddress2(address2);
        createAddressRequestDto.setZipCode(zipCode);
        return createAddressRequestDto;

    }

    @Override
    public Object getSpotDetail(Integer spotId) {

        //spotId로 spot 조회
        Spot spot = spotRepository.findById(BigInteger.valueOf(spotId))
                .orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));


        if (spot instanceof CorporationSpot){
            Optional<Corporation> corporation = corporationRepository.findById(spot.getGroup().getId());
            List<CorporationMealInfo> corporationMealInfo = corporaionMealInfoRepository.findAllByGroupId(spot.getGroup().getId());


            if (spot.getGroup().getManagerId() != null) {
                User manager = userRepository.findById(spot.getGroup().getManagerId()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MANAGER));
                return spotMapper.toDetailDto(spot, manager, corporation.get(), corporationMealInfo);
            }
            return spotMapper.toDetailDto(spot, User.builder().id(BigInteger.valueOf(0)).phone("없음").name("없음").build(), corporation.get(), corporationMealInfo);
        }

        return spotMapper.toDetailDto(spot, User.builder().id(BigInteger.valueOf(0)).phone("없음").name("없음").build(), null, null);
    }

    @Override
    @Transactional
    public void updateSpotDetail(UpdateSpotDetailRequestDto updateSpotDetailRequestDto) throws ParseException {

        //manager가 존재하는 user인지 체크
        User manager = null;
        if (updateSpotDetailRequestDto.getManagerId() != null){
            manager = userRepository.findById(updateSpotDetailRequestDto.getManagerId())
                    .orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));
        }
        //groupId 가져오기
        BigInteger groupId = qSpotRepository.getGroupId(updateSpotDetailRequestDto.getSpotId());

        qGroupRepository.updateSpotDetail(updateSpotDetailRequestDto, groupId);


        qSpotRepository.updateSpotDetail(updateSpotDetailRequestDto);


        //mealInfo
        //지원금 수정
        qMealInfoRepository.updateSpotDetailSupportPrice(groupId, updateSpotDetailRequestDto);

        //식사타입, 요일 수정
        List<MealInfo> mealInfoList = mealInfoRepository.findAllByGroupId(groupId);
        List<DiningType> mealInfoDiningTypeList = new ArrayList<>();
        for (MealInfo mealInfo : mealInfoList){
            //그룹에 해당되는 다이닝타입만을 추출
            mealInfoDiningTypeList.add(mealInfo.getDiningType());
        }


       //diningType
        Optional<Spot> spot = spotRepository.findById(updateSpotDetailRequestDto.getSpotId());
       List<String> split = Arrays.stream(updateSpotDetailRequestDto.getDiningTypes().split(",")).toList();
        for (String diningType : split){
            if (mealInfoDiningTypeList.contains(DiningType.ofCode(Integer.valueOf(diningType))) && mealInfoDiningTypeList.size() > split.size()){
                //기존 mealInfo에 존재하면서 dto에 요청한 diningType의 size가 기존 mealInfoList의 size보다 작은 경우는 해당 diningtype을 제외하고 제거한다.
                if (split.size() == 1){ //이때 split의 size는 1 또는 2이다.
                    qMealInfoRepository.updateSpotDetailDelete1(split.get(0), groupId, updateSpotDetailRequestDto.getServiceDays());
                } else {
                    qMealInfoRepository.updateSpotDetailDelete2(split.get(0), split.get(1), groupId, updateSpotDetailRequestDto.getServiceDays());
                }
            } else if (mealInfoDiningTypeList.contains(DiningType.ofCode(Integer.valueOf(diningType)))) {

                //기존 MealInfo에 해당하는 diningType이라면 요일만 수정
                //다이닝 타입에 맞는 서비스요일을 수정
                qMealInfoRepository.updateSpotDetailServiceDays(groupId, updateSpotDetailRequestDto, Integer.valueOf(diningType));
            } else { //기존 MealInfo에 해당하지 않는 diningType이라면 diningType에 맞는 mealInfo 생성
                CorporationMealInfo updateMealInfo = mealInfoMapper.toEntityUpdateSpotDetail(mealInfoList.get(0), updateSpotDetailRequestDto.getServiceDays(), diningType, updateSpotDetailRequestDto);
                mealInfoRepository.save(updateMealInfo);
            }
            //spot에도 바뀐 diningType으로 적용
            spot.get().updateDiningType(updateSpotDetailRequestDto.getDiningTypes());
        }

        //corporation 수정
        qCorporationRepository.updateSpotDetail(updateSpotDetailRequestDto, groupId);




    }
}