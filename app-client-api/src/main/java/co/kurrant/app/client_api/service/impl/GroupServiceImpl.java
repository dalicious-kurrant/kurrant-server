package co.kurrant.app.client_api.service.impl;

import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QCorporationRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.client_api.mapper.GroupInfoMapper;
import co.kurrant.app.client_api.service.GroupService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;


@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    public final QCorporationRepository qCorporationRepository;
    public final UserRepository userRepository;
    public final GroupInfoMapper groupMapper;
    public final QGroupRepository qGroupRepository;
    public final GroupRepository groupRepository;


    @Override
    @Transactional(readOnly = true)
    public GroupListDto.GroupInfoList getGroupInfo(BigInteger groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow( () -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

        // 기업 정보 dto 맵핑하기
        User managerUser = null;
        if(group.getManagerId() != null) { managerUser = userRepository.findById(group.getManagerId()).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));}

        return groupMapper.toCorporationListDto(group, managerUser);
    }

}
