package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QCorporationRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.system.enums.DiningType;
import co.kurrant.app.admin_api.mapper.GroupMapper;
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
    public void saveCorporationList(List<GroupListDto.GroupInfoList> groupListDtoList) {
        // 그룹이 있는지 찾아보기
        for(GroupListDto.GroupInfoList groupInfoList : groupListDtoList) {
            Group group = groupRepository.findById(groupInfoList.getId()).orElse(null);

            if(group == null) {
                BigInteger managerId = null;
                if(groupInfoList.getManagerName() != null || !groupInfoList.getManagerName().isEmpty() || !groupInfoList.getManagerName().isBlank()) {
                    User manager = userRepository.findByName(groupInfoList.getManagerName());
                    managerId = manager.getId();
                }

                // 기업인지 - code 가 있으면 기업
                if(groupInfoList.getCode() != null || !groupInfoList.getCode().isEmpty() || !groupInfoList.getCode().isBlank()) {
                    Corporation corporation = groupMapper.groupInfoListToCorporationEntity(groupInfoList, managerId);
                    Spot spot =
                }
                // 아파트 인지 확인 - code 가 없으면 기업
                else{
                    Apartment apartment =
                }
            }
        }

    }
}
