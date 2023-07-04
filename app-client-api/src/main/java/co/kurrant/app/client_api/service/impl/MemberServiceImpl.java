package co.kurrant.app.client_api.service.impl;

import co.dalicious.domain.client.dto.*;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.client.entity.EmployeeHistory;
import co.dalicious.domain.client.entity.enums.EmployeeHistoryType;
import co.dalicious.domain.client.mapper.EmployeeHistoryMapper;
import co.dalicious.domain.client.mapper.EmployeeMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.repository.*;
import co.kurrant.app.client_api.dto.MemberIdListDto;
import co.kurrant.app.client_api.dto.DeleteWaitingMemberRequestDto;
import co.kurrant.app.client_api.dto.MemberListResponseDto;
import co.kurrant.app.client_api.dto.MemberWaitingListResponseDto;
import co.kurrant.app.client_api.mapper.MemberMapper;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.MemberService;
import co.kurrant.app.client_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final QCorporationRepository qCorporationRepository;
    private final QUserGroupRepository qUserGroupRepository;
    private final MemberMapper memberMapper;
    private final EmployeeRepository employeeRepository;
    private final QEmployeeRepository qEmployeeRepository;
    private final QUserRepository qUserRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeeHistoryMapper employeeHistoryMapper;
    private final EmployeeHistoryRepository employeeHistoryRepository;
    private final QProviderEmailRepository qProviderEmailRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserUtil userUtil;

    @Override
    @Transactional
    public List<MemberListResponseDto> getUserList(SecurityUser securityUser) {
        //code로 CorporationId 찾기 (=GroupId)
        Corporation corporation = userUtil.getCorporation(securityUser);

        //corporationId로 GroupName 가져오기
        String userGroupName = qUserGroupRepository.findNameById(corporation.getId());
        //groupID로 user목록 조회
        List<User> groupUserList = qUserGroupRepository.findAllByGroupId(corporation.getId());


        Optional<User> any = groupUserList.stream().filter(u -> u.getUserStatus().getCode() != 0)
                .findAny();

        return groupUserList.stream()
                .map((user) -> memberMapper.toMemberListDto(user, userGroupName)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<MemberWaitingListResponseDto> getWaitingUserList(SecurityUser securityUser) {
        //code로 CorporationId 찾기 (=GroupId)
        Corporation corporation = userUtil.getCorporation(securityUser);
        //corpId로 employee 대기유저 목록 조회
        List<Employee> employeeList = qEmployeeRepository.findAllByCorporationId(corporation.getId());

        List<String> emails = employeeList.stream()
                .map(Employee::getEmail)
                .toList();

        List<ProviderEmail> joinedUser = qProviderEmailRepository.getProviderEmails(emails);
        Map<String, User> joinedEmails = joinedUser.stream()
                .collect(Collectors.toMap(
                        ProviderEmail::getEmail,
                        ProviderEmail::getUser,
                        (existingValue, newValue) -> newValue
                ));

        List<UserGroup> userGroups = userGroupRepository.findAllByGroup(corporation);
        Set<User> usersInGroup = userGroups.stream()
                .filter(v -> v.getClientStatus().equals(ClientStatus.BELONG))
                .map(UserGroup::getUser)
                .collect(Collectors.toSet());

        List<MemberWaitingListResponseDto> memberWaitingListResponseDtos = new ArrayList<>();

        for (Employee employee : employeeList) {
            boolean status = false;
            Optional<User> optionalUser = Optional.ofNullable(joinedEmails.get(employee.getEmail()));
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                status = usersInGroup.contains(user);
            }
            memberWaitingListResponseDtos.add(memberMapper.toMemberWaitingListDto(employee, status));
        }
        return memberWaitingListResponseDtos;
    }

    @Override
    @Transactional
    public void deleteMember(SecurityUser securityUser, MemberIdListDto deleteMemberRequestDto) {
        Corporation corporation = userUtil.getCorporation(securityUser);

        List<BigInteger> userIdList = deleteMemberRequestDto.getUserIdList();

        if (userIdList.isEmpty()) {
            throw new ApiException(ExceptionEnum.BAD_REQUEST);
        }

        userIdList.forEach(userId -> {
            User deleteUser = qUserRepository.findByUserId(userId);
            EmployeeHistoryType type = EmployeeHistoryType.USER;
            EmployeeHistory employeeHistory = employeeHistoryMapper.toEntity(userId, deleteUser.getName(), deleteUser.getEmail(), deleteUser.getPhone(), type);
            employeeHistoryRepository.save(employeeHistory);

            qUserGroupRepository.findAllByUserIdAndGroupId(userId, corporation.getId())
                    .forEach(userGroup -> userGroup.updateStatus(ClientStatus.WITHDRAWAL));
        });
    }

    @Override
    public void deleteWaitingMember(SecurityUser securityUser, DeleteWaitingMemberRequestDto deleteWaitingMemberRequestDto) {
        //받아온 Employee ID를 삭제한다
        for (BigInteger userId : deleteWaitingMemberRequestDto.getWaitMemberIdList()) {
            //삭제 전에 기록 남기기
            Employee employee = employeeRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
            EmployeeHistoryType type = EmployeeHistoryType.WAIT_USER;
            EmployeeHistory employeeHistory = employeeHistoryMapper.toEntity(userId, employee.getName(), employee.getEmail(), employee.getPhone(), type);
            employeeHistoryRepository.save(employeeHistory);

            long result = qEmployeeRepository.deleteWaitingMember(userId);
            if (result != 1) {
                throw new ApiException(ExceptionEnum.USER_PATCH_ERROR);
            }
        }

    }

    @Override
    @Transactional
    public void insertMemberListByExcel(SecurityUser securityUser, ClientUserWaitingListSaveRequestDtoList dtoList) {
        //code로 CorporationId 찾기 (=GroupId)
        Corporation corporation = userUtil.getCorporation(securityUser);

        List<String> emails = dtoList.getSaveUserList().stream()
                .map(ClientUserWaitingListSaveRequestDto::getEmail)
                .toList();

        // 삭제
        List<Employee> employees = employeeRepository.findAllByCorporation(corporation);

        Set<String> employEmails = employees.stream()
                .map(Employee::getEmail)
                .collect(Collectors.toSet());

        List<Employee> deleteEmployee = employees.stream()
                .filter(v -> !emails.contains(v.getEmail()))
                .toList();
        employeeRepository.deleteAll(deleteEmployee);

        List<ProviderEmail> providerEmails = qProviderEmailRepository.getProviderEmails(emails);

        Map<String, ProviderEmail> providerEmailMap = providerEmails.stream()
                .collect(Collectors.toMap(
                        ProviderEmail::getEmail,
                        Function.identity(),
                        (existingValue, newValue) -> newValue
                ));

        Set<String> joinedEmails = providerEmails.stream()
                .map(ProviderEmail::getEmail)
                .collect(Collectors.toSet());

        if (!providerEmails.isEmpty()) {
            // 기존에 그룹에 포함된 인원인지 체크한다.
            for (ProviderEmail providerEmail : providerEmailMap.values()) {
                User user = providerEmail.getUser();
                List<UserGroup> userGroups = user.getGroups();

                // 이미 그룹에 포함된 유저인지 체크한다.
                boolean alreadyMember = userGroups.stream()
                        .anyMatch(ug -> ug.getGroup().equals(corporation));

                if (!alreadyMember) {
                    UserGroup userGroup = UserGroup.builder()
                            .group(corporation)
                            .user(user)
                            .clientStatus(ClientStatus.BELONG)
                            .build();
                    userGroupRepository.save(userGroup);
                } else {
                    userGroups.stream()
                            .filter(ug -> ug.getGroup().equals(corporation))
                            .forEach(ug -> ug.updateStatus(ClientStatus.BELONG));
                }

            }
        }

        // 생성
        List<ClientUserWaitingListSaveRequestDto> employeeDtos = dtoList.getSaveUserList();

        employeeDtos = employeeDtos.stream()
                .filter(v -> !employEmails.contains(v.getEmail()))
                .toList();

        for (ClientUserWaitingListSaveRequestDto employeeDto : employeeDtos) {
            Optional<Employee> employee = employees.stream()
                    .filter(v -> v.getEmail().equals(employeeDto.getEmail()))
                    .findAny();
            String email = employeeDto.getEmail();
            String phone = employeeDto.getPhone();
            String name = employeeDto.getName();

            if(email == null || email.isBlank()) throw new ApiException(ExceptionEnum.NOT_VALID_EMAIL);

            if (employee.isPresent()) {
                qEmployeeRepository.patchEmployee(employeeDto.getId(), phone, email, name);
            } else {
                Employee newEmployee = employeeMapper.toEntity(email, name, phone, corporation);
                employeeRepository.save(newEmployee);
            }
        }

        // TODO: 프라이빗 스팟 초대 시 푸시알림 추가
    }
}
