package co.kurrant.app.client.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.client.repository.QCorporationRepository;
import co.dalicious.domain.client.repository.QSpotRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.domain.user.repository.QUserSpotRepository;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.kurrant.app.client.dto.MemberListResponseDto;
import co.kurrant.app.client.mapper.MemberMapper;
import co.kurrant.app.client.service.MemberService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final QUserSpotRepository qUserSpotRepository;
    private final QCorporationRepository qCorporationRepository;
    private final QSpotRepository qSpotRepository;
    private final QUserGroupRepository qUserGroupRepository;
    private final MemberMapper memberMapper;

    @Override
    public List<MemberListResponseDto> getUserList(String code, OffsetBasedPageRequest pageable) {

        //code로 CorporationId 찾기 (=GroupId)
        BigInteger corporationId = qCorporationRepository.findOneByCode(code);

        //corporationId로 GroupName 가져오기
        String userGroupName = qUserGroupRepository.findNameById(corporationId);


        //GroupId에 해당하는 ClientSpotId 찾기
        List<BigInteger> spotIdList = qSpotRepository.findAllByGroupId(corporationId);
        //spotId로 userSpotId찾기
        List<BigInteger> userSpotIdList = new ArrayList<>();
        for (BigInteger spotId : spotIdList){
           userSpotIdList.add(qUserSpotRepository.findOneBySpotId(spotId));
        }
        System.out.println(userSpotIdList.get(0) + " = userSpotIdList 0 ");  //18
        //userSpotId로 userList찾기
        List<User> userList = new ArrayList<>();
        for (BigInteger userSpotId : userSpotIdList){
            if (userSpotId == null){
                break;
            }
            System.out.println(userSpotId +" for문내에 uesrSpotId");
            userList.add(qUserSpotRepository.findOneById(userSpotId));
        }
        System.out.println(userList.get(0) + " = userList 0 ");
        List<MemberListResponseDto> memberListResponseList = new ArrayList<>();
        System.out.println(userGroupName + "groupName");
        for (User user : userList){
            memberListResponseList.add(memberMapper.toMemberListDto(user, userGroupName));
        }

        return memberListResponseList;
    }
}
