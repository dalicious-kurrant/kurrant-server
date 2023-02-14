package co.kurrant.app.client.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.client.repository.QCorporationRepository;
import co.dalicious.domain.client.repository.QEmployeeRepository;
import co.dalicious.domain.client.repository.QSpotRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.domain.user.repository.QUserSpotRepository;
import co.kurrant.app.client.dto.MemberListResponseDto;
import co.kurrant.app.client.dto.MemberWaitingListResponseDto;
import co.kurrant.app.client.mapper.MemberMapper;
import co.kurrant.app.client.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final QUserSpotRepository qUserSpotRepository;
    private final QCorporationRepository qCorporationRepository;
    private final QSpotRepository qSpotRepository;
    private final QUserGroupRepository qUserGroupRepository;
    private final MemberMapper memberMapper;
    private final QEmployeeRepository qEmployeeRepository;

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
        //userSpotId로 userList찾기
        List<User> userList = new ArrayList<>();
        for (BigInteger userSpotId : userSpotIdList){
            if (userSpotId == null){
                break;
            }
            userList.add(qUserSpotRepository.findOneById(userSpotId));
        }
        List<MemberListResponseDto> memberListResponseList = new ArrayList<>();
        for (User user : userList){
            memberListResponseList.add(memberMapper.toMemberListDto(user, userGroupName));
        }

        return memberListResponseList;
    }

    @Override
    public List<MemberWaitingListResponseDto> getWaitingUserList(String code, OffsetBasedPageRequest pageable) {

        //code로 CorporationId 찾기 (=GroupId)
        BigInteger corporationId = qCorporationRepository.findOneByCode(code);
        //corpId로 employee 대기유저 목록 조회
        List<Employee> employeeList = qEmployeeRepository.findAllByCorporationId(corporationId);

        List<MemberWaitingListResponseDto> waitingListResponseDtoList = new ArrayList<>();
        for (Employee employee : employeeList){
            waitingListResponseDtoList.add(memberMapper.toMemberWaitingListDto(employee));
        }

        return waitingListResponseDtoList;
    }
}
