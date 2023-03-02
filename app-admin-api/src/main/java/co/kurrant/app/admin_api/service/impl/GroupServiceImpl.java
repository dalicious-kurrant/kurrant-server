package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.system.enums.DiningType;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.kurrant.app.admin_api.mapper.CorporationMealInfoMapper;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.SpotMapper;
import co.kurrant.app.admin_api.service.GroupService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    public final QCorporationRepository qCorporationRepository;
    public final UserRepository userRepository;
    public final GroupMapper groupMapper;
    public final QGroupRepository qGroupRepository;
    public final GroupRepository groupRepository;
    public final SpotMapper spotMapper;
    public final SpotRepository spotRepository;
    private final CorporationMealInfoMapper mealInfoMapper;
    private final MealInfoRepository mealInfoRepository;

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<GroupListDto> getGroupList(BigInteger groupId, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        Page<Group> groupList = qGroupRepository.findAll(groupId, limit, page, pageable);

        // 기업 정보 dto 맵핑하기
        List<GroupListDto.GroupInfoList> groupListDtoList = new ArrayList<>();
        if(groupList != null && !groupList.isEmpty()) {
            for(Group group : groupList) {
                User managerUser = null;
                if(group.getManagerId() != null) { managerUser = userRepository.findById(group.getManagerId()).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));}
                GroupListDto.GroupInfoList corporationListDto = groupMapper.toCorporationListDto(group, managerUser);
                groupListDtoList.add(corporationListDto);
            }
        }

        List<Group> groups = groupRepository.findAll();
        GroupListDto groupListDto = GroupListDto.createGroupListDto(groups, groupListDtoList);

        return ItemPageableResponseDto.<GroupListDto>builder().items(groupListDto)
                .limit(pageable.getPageSize()).total(Objects.requireNonNull(groupList).getTotalPages())// + Objects.requireNonNull(apartmentList).getTotalPages()) - 1)
                .count(groupList.getNumberOfElements()).build();
    }

    @Override
    @Transactional
    public void saveCorporationList(List<GroupExcelRequestDto> groupListDtoList) {
        // 그룹이 있는지 찾아보기
        for(GroupExcelRequestDto groupInfoList : groupListDtoList) {
            Group group = groupRepository.findById(groupInfoList.getId()).orElse(null);
            BigInteger managerId = null;
            if(groupInfoList.getManagerName() != null && !groupInfoList.getManagerName().isBlank() && !groupInfoList.getManagerName().isEmpty()) {
                User manager = userRepository.findByName(groupInfoList.getManagerName());
                managerId = manager.getId();
            }
            Address address = Address.builder().createAddressRequestDto(groupMapper.createAddressDto(groupInfoList)).build();

            // group 없으면
            if(group == null) {
                // 기업인지 - code 가 있으면 기업
                if(groupInfoList.getCode() != null && !groupInfoList.getCode().isEmpty() && !groupInfoList.getCode().isBlank()) {
                    Corporation corporation = groupMapper.groupInfoListToCorporationEntity(groupInfoList, managerId, address);
                    groupRepository.save(corporation);

                    // spot 생성
                    Spot spot = spotMapper.toEntity(corporation);
                    spotRepository.save(spot);

                    List<DiningType> spotDiningType = spot.getDiningTypes();
                    for(DiningType diningType : spotDiningType) {
                        // 스팟 식사 일정 생성
                        CorporationMealInfo mealInfo = mealInfoMapper.toCorporationMealInfoEntity(groupInfoList, spot, diningType, "00:00");
                        mealInfoRepository.save(mealInfo);
                    }
                }
                // 아파트 인지 확인 - code 가 없으면 기업
                else{
                    Apartment apartment = groupMapper.groupInfoListToApartmentEntity(groupInfoList, managerId, address);
                    groupRepository.save(apartment);

                    // spot 생성
                    Spot spot = spotMapper.toEntity(apartment);
                    spotRepository.save(spot);

                    List<DiningType> spotDiningType = spot.getDiningTypes();
                    for(DiningType diningType : spotDiningType) {
                        ApartmentMealInfo mealInfo = mealInfoMapper.toApartmentMealInfoEntity(groupInfoList, spot, diningType, "00:00");
                        mealInfoRepository.save(mealInfo);
                    }
                }
            }
            // group 있으면
            else {
                List<DiningType> diningTypeList = new ArrayList<>();
                List<String> integerList = groupInfoList.getDiningTypes();
                for(String code : integerList) diningTypeList.add(DiningType.ofString(code));

                if(group instanceof Corporation corporation) {
                    Boolean isMembership = null;
                    if(groupInfoList.getIsMembershipSupport().equals("미지원")) isMembership = false;
                    else if(groupInfoList.getIsMembershipSupport().equals("지원")) isMembership = true;

                    corporation.updateCorporation(groupInfoList, address, managerId, diningTypeList, isMembership, useOrNotUse(groupInfoList.getIsSetting()), useOrNotUse(groupInfoList.getIsGarbage()), useOrNotUse(groupInfoList.getIsHotStorage()));
                    updateGroupData(groupInfoList, address, corporation);
                }
                else if(group instanceof Apartment apartment) {
                    apartment.updateApartment(groupInfoList, address, managerId, diningTypeList);
                    updateGroupData(groupInfoList, address, apartment);
                }
            }
        }

    }

    private Boolean useOrNotUse(String data) {
        Boolean use = null;
        if(data.equals("미사용")) use = false;
        else if(data.equals("사용")) use = true;
        return use;
    }

    private void updateGroupData(GroupExcelRequestDto groupInfoList, Address address, Group group) {
        groupRepository.save(group);
        // 하위 스팟의 모든 내용을 업데이트
        List<Spot> spotList = group.getSpots();
        for(Spot spot : spotList) {
            spot.updateSpot(groupInfoList, address, group);
            spotRepository.save(spot);

            // service days update
            List<MealInfo> mealInfoList = spot.getMealInfos();
            for(MealInfo mealInfo : mealInfoList) mealInfo.updateServiceDays(groupInfoList.getServiceDays());
        }
    }
}
