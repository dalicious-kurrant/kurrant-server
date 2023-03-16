package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.MealInfoRepository;
import co.dalicious.domain.client.repository.QCorporationRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.system.enums.DiningType;
import co.kurrant.app.admin_api.mapper.CorporationMealInfoMapper;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    public final QCorporationRepository qCorporationRepository;
    public final QUserRepository qUserRepository;
    public final GroupMapper groupMapper;
    public final QGroupRepository qGroupRepository;
    public final GroupRepository groupRepository;
    private final CorporationMealInfoMapper mealInfoMapper;
    private final MealInfoRepository mealInfoRepository;

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<GroupListDto> getGroupList(BigInteger groupId, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        Page<Group> groupList = qGroupRepository.findAll(groupId, limit, page, pageable);

        // 기업 정보 dto 맵핑하기
        List<GroupListDto.GroupInfoList> groupListDtoList = new ArrayList<>();
        if(groupList != null && !groupList.isEmpty()) {
            List<BigInteger> managerIds = groupList.stream().map(Group::getManagerId).filter(Objects::nonNull).toList();
            List<User> users = (managerIds.isEmpty()) ? null : qUserRepository.getUserAllById(managerIds);
            for(Group group : groupList) {
                User managerUser = null;
                if(group.getManagerId() != null) {
                    managerUser = (users != null) ? users.stream().filter(user -> user.getId().equals(group.getManagerId())).findFirst().orElse(null) : null;
                }
                GroupListDto.GroupInfoList corporationListDto = groupMapper.toCorporationListDto(group, managerUser);
                groupListDtoList.add(corporationListDto);
            }
        }

        List<Group> groups = groupRepository.findAll();
        GroupListDto groupListDto = GroupListDto.createGroupListDto(groups, groupListDtoList);

        return ItemPageableResponseDto.<GroupListDto>builder().items(groupListDto)
                .limit(pageable.getPageSize()).total(Objects.requireNonNull(groupList).getTotalPages())
                .count(groupList.getNumberOfElements()).build();
    }

    @Override
    @Transactional
    public void saveCorporationList(List<GroupExcelRequestDto> groupListDtoList) throws ParseException {
        Set<BigInteger> groupIdList = new HashSet<>();
        for(GroupExcelRequestDto groupExcelRequestDto : groupListDtoList) {
            groupIdList.add(groupExcelRequestDto.getId());
        }
        List<Group> groupList = qGroupRepository.findAllByIds(groupIdList);

        List<Group> newGroupList = new ArrayList<>();
        List<MealInfo> newMealInfoList = new ArrayList<>();
        // 그룹이 있는지 찾아보기
        for(GroupExcelRequestDto groupInfoList : groupListDtoList) {
            Group group = groupList.stream().filter(groupMatch -> groupMatch.getId().equals(groupInfoList.getId())).findFirst().orElse(null);
            Address address = new Address(groupInfoList.getZipCode(), groupInfoList.getAddress1(), groupInfoList.getAddress2(), groupInfoList.getLocation());

            // group 없으면
            if(group == null) {
                if(GroupDataType.CORPORATION.equals(GroupDataType.ofCode(groupInfoList.getGroupType()))) {
                    Corporation corporation = groupMapper.groupInfoListToCorporationEntity(groupInfoList, address);
                    newGroupList.add(corporation);

                    List<DiningType> diningTypeList = corporation.getDiningTypes();
                    for(DiningType diningType : diningTypeList) {
                        CorporationMealInfo mealInfo = mealInfoMapper.toCorporationMealInfoEntity(groupInfoList, corporation,diningType, "00:00");
                        newMealInfoList.add(mealInfo);
                    }
                }
                else if(GroupDataType.APARTMENT.equals(GroupDataType.ofCode(groupInfoList.getGroupType()))){
                    Apartment apartment = groupMapper.groupInfoListToApartmentEntity(groupInfoList, address);
                    newGroupList.add(apartment);

                    List<DiningType> diningTypeList = apartment.getDiningTypes();
                    for(DiningType diningType : diningTypeList) {
                        ApartmentMealInfo mealInfo = mealInfoMapper.toApartmentMealInfoEntity(groupInfoList, apartment, diningType, "00:00");
                        newMealInfoList.add(mealInfo);
                    }
                }
                else if(GroupDataType.OPEN_GROUP.equals(GroupDataType.ofCode(groupInfoList.getGroupType()))){
                    OpenGroup openGroup = groupMapper.groupInfoListToOpenGroupEntity(groupInfoList, address);
                    newGroupList.add(openGroup);

                    List<DiningType> diningTypeList = openGroup.getDiningTypes();
                    for(DiningType diningType : diningTypeList) {
                        OpenGroupMealInfo mealInfo = mealInfoMapper.toOpenGroupMealInfoEntity(groupInfoList, openGroup, diningType, "00:00");
                        newMealInfoList.add(mealInfo);
                    }

                }
            }
            // group 있으면
            else {
                List<DiningType> diningTypeList = new ArrayList<>();
                List<String> integerList = groupInfoList.getDiningTypes();
                for(String code : integerList) {
                    diningTypeList.add(DiningType.ofString(code));
                }

                if(group instanceof Corporation corporation) {
                    Boolean isMembership = null;
                    if(groupInfoList.getIsMembershipSupport().equals("미지원")) isMembership = false;
                    else if(groupInfoList.getIsMembershipSupport().equals("지원")) isMembership = true;

                    corporation.updateCorporation(groupInfoList, address, diningTypeList, isMembership, useOrNotUse(groupInfoList.getIsSetting()), useOrNotUse(groupInfoList.getIsGarbage()), useOrNotUse(groupInfoList.getIsHotStorage()));
                    newGroupList.add(corporation);

                    List<MealInfo> mealInfoList = corporation.getMealInfos();
                    diningTypeList.forEach(type -> {
                        CorporationMealInfo corporationMealInfo = mealInfoList.stream()
                                .filter(mealInfo -> mealInfo instanceof CorporationMealInfo && mealInfo.getDiningType().equals(type))
                                .map(mealInfo -> (CorporationMealInfo) mealInfo)
                                .findFirst().orElse(null);
                        if(corporationMealInfo == null) {
                            corporationMealInfo = mealInfoMapper.toCorporationMealInfoEntity(groupInfoList, corporation, type,"00:00");
                            newMealInfoList.add(corporationMealInfo);
                        }
                        corporationMealInfo.updateCorporationMealInfo(groupInfoList);
                        newMealInfoList.add(corporationMealInfo);
                    });
                }
                else if(group instanceof Apartment apartment) {
                    apartment.updateApartment(groupInfoList, address, diningTypeList);
                    newGroupList.add(apartment);

                    List<MealInfo> mealInfoList = apartment.getMealInfos();
                    diningTypeList.forEach(type -> {
                        ApartmentMealInfo apartmentMealInfo = mealInfoList.stream()
                                .filter(mealInfo -> mealInfo instanceof ApartmentMealInfo && mealInfo.getDiningType().equals(type))
                                .map(mealInfo -> (ApartmentMealInfo) mealInfo)
                                .findFirst().orElse(null);
                        if(apartmentMealInfo == null) {
                            apartmentMealInfo = mealInfoMapper.toApartmentMealInfoEntity(groupInfoList, apartment, type,"00:00");
                            newMealInfoList.add(apartmentMealInfo);
                        }
                        apartmentMealInfo.updateApartmentMealInfo(groupInfoList);
                        newMealInfoList.add(apartmentMealInfo);
                    });
                }
                else if(group instanceof OpenGroup openGroup) {
                    openGroup.updateOpenSpot(groupInfoList, address, diningTypeList);
                    newGroupList.add(openGroup);

                    List<MealInfo> mealInfoList = openGroup.getMealInfos();
                    diningTypeList.forEach(type -> {
                        OpenGroupMealInfo openGroupMealInfo = mealInfoList.stream()
                                .filter(mealInfo -> mealInfo instanceof OpenGroupMealInfo && mealInfo.getDiningType().equals(type))
                                .map(mealInfo -> (OpenGroupMealInfo) mealInfo)
                                .findFirst().orElse(null);
                        if(openGroupMealInfo == null) {
                            openGroupMealInfo = mealInfoMapper.toOpenGroupMealInfoEntity(groupInfoList, openGroup, type,"00:00");
                            newMealInfoList.add(openGroupMealInfo);
                        }
                        openGroupMealInfo.updateOpenGroupMealInfo(groupInfoList);
                        newMealInfoList.add(openGroupMealInfo);
                    });
                }
            }
        }

        groupRepository.saveAll(newGroupList);
        mealInfoRepository.saveAll(newMealInfoList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupListDto.GroupInfoList> getAllGroupForExcel() {
        List<Group> groupAllList = groupRepository.findAll();
        // 기업 정보 dto 맵핑하기
        List<GroupListDto.GroupInfoList> groupListDtoList = new ArrayList<>();

        if(groupAllList.isEmpty()) { return groupListDtoList; }

        List<BigInteger> managerIds = groupAllList.stream().map(Group::getManagerId).filter(Objects::nonNull).toList();
        List<User> users = (managerIds.isEmpty()) ? null : qUserRepository.getUserAllById(managerIds);
        for(Group group : groupAllList) {
            User managerUser = null;
            if(group.getManagerId() != null) {
                managerUser = (users != null) ? users.stream().filter(user -> user.getId().equals(group.getManagerId())).findFirst().orElse(null) : null;
            }
            GroupListDto.GroupInfoList corporationListDto = groupMapper.toCorporationListDto(group, managerUser);
            groupListDtoList.add(corporationListDto);
        }

        return groupListDtoList;
    }

    private Boolean useOrNotUse(String data) {
        Boolean use = null;
        if(data.equals("미사용")) use = false;
        else if(data.equals("사용")) use = true;
        return use;
    }

}
