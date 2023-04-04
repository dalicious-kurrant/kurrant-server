package co.kurrant.app.client_api.service.impl;

import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QCorporationRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.order.dto.GroupDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.client_api.mapper.GroupInfoMapper;
import co.kurrant.app.client_api.mapper.GroupMapper;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.GroupService;
import co.kurrant.app.client_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;


@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    public final QCorporationRepository qCorporationRepository;
    public final UserRepository userRepository;
    public final GroupInfoMapper groupInfoMapper;
    public final QGroupRepository qGroupRepository;
    public final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    public final UserUtil userUtil;


    @Override
    @Transactional(readOnly = true)
    public GroupListDto.GroupInfoList getGroupInfo(SecurityUser securityUser) {
        BigInteger groupId = userUtil.getGroupId(securityUser);
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

        // 기업 정보 dto 맵핑하기
        User managerUser = null;
        if(group.getManagerId() != null) { managerUser = userRepository.findById(group.getManagerId()).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));}
        return groupInfoMapper.toCorporationListDto(group, managerUser);
    }

    @Override
    @Transactional
    public List<GroupDto.Spot> getSpots(BigInteger groupId, SecurityUser securityUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

        // TODO: 추후 모회사 자회사 사용시 groupId를 통해 조회
        if(!group.getId().equals(securityUser.getId())) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        List<Spot> spots = group.getSpots();
        return groupMapper.spotsToDtos(spots);
    }

}
