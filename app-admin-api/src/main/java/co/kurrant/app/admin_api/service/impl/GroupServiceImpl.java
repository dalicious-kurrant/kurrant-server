package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.QCorporationRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.admin_api.dto.client.GroupListDto;
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


    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<GroupListDto> getGroupList(BigInteger groupId, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        Page<Group> groupList = qGroupRepository.findAll(groupId, limit, page, pageable);

        // 기업 정보 dto 맵핑하기
        List<GroupListDto> groupListDtoList = new ArrayList<>();
        if(groupList != null && !groupList.isEmpty()) {
            for(Group group : groupList) {
                User managerUser = null;
                if(group.getManagerId() != null) { managerUser = userRepository.findById(group.getManagerId()).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));}
                if(group instanceof Corporation corporation) {
                    GroupListDto corporationListDto = groupMapper.toCorporationListDto(corporation, managerUser);
                    groupListDtoList.add(corporationListDto);
                }
                if(group instanceof Apartment apartment) {
                    GroupListDto apartmentListDto = groupMapper.toApartmentListDto(apartment, managerUser);
                    groupListDtoList.add(apartmentListDto);
                }
            }
        }

        return ListItemResponseDto.<GroupListDto>builder().items(groupListDtoList)
                .limit(pageable.getPageSize()).total((long) (Objects.requireNonNull(groupList).getTotalPages()))// + Objects.requireNonNull(apartmentList).getTotalPages()) - 1)
                .offset(pageable.getOffset()).count(groupList.getNumberOfElements()).build();
    }

    @Override
    @Transactional
    public void saveCorporationList(GroupListDto groupListDto) {

    }
}
