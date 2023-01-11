package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.dto.ApartmentResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.mapper.ApartmentListMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.ClientType;
import co.dalicious.domain.user.entity.enums.SpotStatus;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.repository.UserSpotRepository;
import co.dalicious.system.util.DiningType;
import co.kurrant.app.public_api.dto.client.ClientSpotDetailReqDto;
import co.kurrant.app.public_api.dto.client.ClientSpotDetailResDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserClientService;
import co.kurrant.app.public_api.service.CommonService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserClientServiceImpl implements UserClientService {
    private final ApartmentListMapper apartmentMapper;
    private final CommonService commonService;
    private final ApartmentRepository apartmentRepository;
    private final ApartmentSpotRepository apartmentSpotRepository;
    private final ApartmentMealInfoRepository apartmentMealInfoRepository;
    private final CorporationRepository corporationRepository;
    private final CorporationSpotRepository corporationSpotRepository;
    private final CorporationMealInfoRepository corporationMealInfoRepository;
    private final UserGroupRepository userGroupRepository;
    private final SpotRepository spotRepository;
    private final UserSpotRepository userSpotRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public ClientSpotDetailResDto getSpotDetail(SecurityUser securityUser, Integer clientType, BigInteger clientId, BigInteger spotId) {
        // 유저 정보 가져오기
        User user = commonService.getUser(securityUser);
        // 그룹 분류 가져오기
        ClientType client = ClientType.ofCode(clientType);
        List<DiningType> diningTypes = null;

        switch (client) {
            case APARTMENT -> {
                // 해당 아파트의 식사정보와 스팟 정보 가져오기
                Apartment apartment = apartmentRepository.findById(clientId).orElseThrow(
                        () -> new ApiException(ExceptionEnum.CLIENT_NOT_FOUND)
                );
                List<ApartmentMealInfo> apartmentMealInfos = apartmentMealInfoRepository.findAllByGroup(apartment);
                ApartmentSpot apartmentSpot = apartmentSpotRepository.findById(spotId).orElseThrow(
                        () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
                );
                // 식사 정보 가져오기
                diningTypes = apartmentSpot.getDiningTypes();
                List<ClientSpotDetailResDto.MealTypeInfo> mealTypeInfoList = new ArrayList<>();
                for (DiningType diningType : diningTypes) {
                    ApartmentMealInfo mealInfo = apartmentMealInfos.stream()
                            .filter(v -> v.getDiningType().equals(diningType))
                            .findAny()
                            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

                    ClientSpotDetailResDto.MealTypeInfo mealTypeInfo = ClientSpotDetailResDto.MealTypeInfo.builder()
                            .diningType(diningType.getDiningType() + " 식사")
                            .lastOrderTime(mealInfo.getLastOrderTime().toString())
                            .deliveryTime(mealInfo.getDeliveryTime().toString())
                            .build();
                    mealTypeInfoList.add(mealTypeInfo);
                }
                return ClientSpotDetailResDto.builder()
                        .clientType(ClientType.APARTMENT.getClient())
                        .spotId(apartmentSpot.getId())
                        .spotName(apartmentSpot.getName())
                        .address(apartmentSpot.getAddress().addressToString())
                        .mealTypeInfoList(mealTypeInfoList)
                        .clientName(apartment.getName())
                        .build();
            }
            case CORPORATION -> {
                Corporation corporation = corporationRepository.findById(clientId).orElseThrow(
                        () -> new ApiException(ExceptionEnum.CLIENT_NOT_FOUND)
                );
                List<CorporationMealInfo> corporationMealInfos = corporationMealInfoRepository.findAllByGroup(corporation);
                CorporationSpot corporationSpot = corporationSpotRepository.findById(spotId).orElseThrow(
                        () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
                );
                // 식사 정보 가져오기
                diningTypes = corporationSpot.getDiningTypes();
                List<ClientSpotDetailResDto.MealTypeInfo> mealTypeInfos = new ArrayList<>();
                for (DiningType diningType : diningTypes) {
                    CorporationMealInfo mealInfo = corporationMealInfos.stream()
                            .filter(v -> v.getDiningType().equals(diningType))
                            .findAny()
                            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

                    ClientSpotDetailResDto.MealTypeInfo mealTypeInfo = ClientSpotDetailResDto.MealTypeInfo.builder()
                            .diningType(diningType.getDiningType() + " 식사")
                            .lastOrderTime(mealInfo.getLastOrderTime().toString())
                            .deliveryTime(mealInfo.getDeliveryTime().toString())
                            .build();
                    mealTypeInfos.add(mealTypeInfo);
                }
                return ClientSpotDetailResDto.builder()
                        .clientType(ClientType.APARTMENT.getClient())
                        .spotId(corporationSpot.getId())
                        .spotName(corporationSpot.getName())
                        .address(corporationSpot.getAddress().addressToString())
                        .mealTypeInfoList(mealTypeInfos)
                        .clientName(corporation.getName())
                        .build();
            }
            default -> throw new ApiException(ExceptionEnum.SPOT_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public BigInteger saveUserSpot(SecurityUser securityUser, ClientSpotDetailReqDto clientSpotDetailReqDto, Integer clientType, BigInteger clientId, BigInteger spotId) {
        // 유저를 조회한다.
        User user = commonService.getUser(securityUser);
        // 그룹 구분을 가져온다.
        ClientType client = ClientType.ofCode(clientType);
        // 스팟을 가져온다.
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        // 유저가 스팟 그룹에 등록되었는지 검사한다.
        Group group = spot.getGroup();
        List<UserGroup> groups = userGroupRepository.findAllByUser(user);
        UserGroup userGroup = groups.stream().filter(v -> v.getGroup().equals(group))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.CLIENT_NOT_FOUND));
        // 유저 Default 스팟을 저장한다.
        UserSpot userSpot = userSpotRepository.save(UserSpot.builder()
                .user(user)
                .clientType(ClientType.APARTMENT)
                .spot(spot)
                .build());
        // 상세 주소 저장 및 업데이트
        userGroup.updateHo(clientSpotDetailReqDto.getHo());
        user.updateUserSpot(userSpot);
        return userSpot.getSpot().getId();

    }

    @Override
    @Transactional
    public Integer withdrawClient(SecurityUser securityUser, Integer clientType, BigInteger clientId) {
        // 유저를 조회한다.
        User user = commonService.getUser(securityUser);
        // 그룹 구분을 가져온다.
        ClientType client = ClientType.ofCode(clientType);
        List<UserGroup> groups = userGroupRepository.findAllByUserAndClientStatus(user, ClientStatus.BELONG);
        // 유저가 해당 아파트 스팟 그룹에 등록되었는지 검사한다.
        Group group = groupRepository.findById(clientId).orElseThrow(
                () -> new ApiException(ExceptionEnum.CLIENT_NOT_FOUND)
        );
        UserGroup userGroup = groups.stream().filter(v -> v.getGroup().equals(group))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.CLIENT_NOT_FOUND));
        // 유저 그룹 상태를 탈퇴로 만든다.
        userGroup.updateStatus(ClientStatus.WITHDRAWAL);

        // 다른 그룹이 존재하는지 여부에 따라 Return값 결정(스팟 선택 화면 || 그룹 신청 화면)
        return (groups.size() - 1 > 0) ? SpotStatus.NO_SPOT_BUT_HAS_CLIENT.getCode() : SpotStatus.NO_SPOT_AND_CLIENT.getCode();
    }

    @Override
    @Transactional
    public List<ApartmentResponseDto> getApartments() {
        List<Apartment> apartments = apartmentRepository.findAll();
        List<ApartmentResponseDto> apartmentResponseDtos = new ArrayList<>();
        for (Apartment apartment : apartments) {
            apartmentResponseDtos.add(apartmentMapper.toDto(apartment));
        }
        return apartmentResponseDtos;
    }
}
