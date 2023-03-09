package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.order.repository.QOrderRepository;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.dalicious.domain.user.dto.UserDto;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.Provider;
import co.dalicious.domain.user.entity.enums.Role;
import co.dalicious.domain.user.entity.enums.UserStatus;
import co.dalicious.domain.user.mapper.UserHistoryMapper;
import co.dalicious.domain.user.repository.*;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.dto.user.SaveUserListRequestDto;
import co.kurrant.app.admin_api.dto.user.UserInfoResponseDto;
import co.kurrant.app.admin_api.dto.user.UserResetPasswordRequestDto;
import co.kurrant.app.admin_api.mapper.UserMapper;
import co.kurrant.app.admin_api.service.UserService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserHistoryMapper userHistoryMapper;
    private final QGroupRepository qGroupRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserHistoryRepository userHistoryRepository;
    private final QUserRepository qUserRepository;
    private final QUserGroupRepository qUserGroupRepository;
    private final QOrderRepository qOrderRepository;
    private final QProviderEmailRepository qProviderEmailRepository;
    private final UserGroupRepository userGroupRepository;
    private final ProviderEmailRepository providerEmailRepository;
    private final UserSpotRepository userSpotRepository;


    @Override
    public List<UserInfoResponseDto> getUserList(Map<String, Object> parameters) {
        List<User> users = qUserRepository.findAllByParameter(parameters);

        return users.stream().map(userMapper::toDto).toList();
    }

    @Override
    public long deleteMember(DeleteMemberRequestDto deleteMemberRequestDto) {

        List<BigInteger> userIdList = deleteMemberRequestDto.getUserIdList();

        //code로 CorporationId 찾기 (=GroupId)
        BigInteger groupId = deleteMemberRequestDto.getGroupId();

        if (userIdList.size() == 0) throw new ApiException(ExceptionEnum.BAD_REQUEST);

        for (BigInteger userId : userIdList) {
            User deleteUser = userRepository.findById(userId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
            //주문 체크
            long isOrder = qOrderRepository.orderCheck(deleteUser);
            //주문내역이 없다면 해당유저 찐 삭제
            if (isOrder == 0) {
                long deleteReal = qUserRepository.deleteReal(deleteUser);
                if (deleteReal != 1) throw new ApiException(ExceptionEnum.USER_PATCH_ERROR);
            }

            UserHistory userHistory = userHistoryMapper.toEntity(deleteUser, groupId);

            userHistoryRepository.save(userHistory);
            if (isOrder != 0) {
                Long deleteResult = qUserGroupRepository.deleteMember(userId, groupId);
                if (deleteResult != 1) throw new ApiException(ExceptionEnum.USER_PATCH_ERROR);
            }
        }
        return 1;
    }

    @Override
    @Transactional
    public void saveUserList(List<SaveUserListRequestDto> saveUserListRequestDtoList) {
        List<String> emails = saveUserListRequestDtoList.stream()
                .map(SaveUserListRequestDto::getEmail)
                .toList();

        Set<String> groupNames = saveUserListRequestDtoList.stream()
                .flatMap(v -> {
                    if (v.getGroupName() != null) return Arrays.stream(v.getGroupName().split(","));
                    else return Stream.empty();
                })
                .map(String::trim)
                .collect(Collectors.toSet());
        List<Group> groups = qGroupRepository.findAllByNames(groupNames);

        // 동일한 이메일 입력이 있으면 예외처리
        if (emails.size() != emails.stream().distinct().count()) {
            throw new ApiException(ExceptionEnum.EXCEL_EMAIL_DUPLICATION);
        }

        // FIXME 수정 요청
        List<ProviderEmail> providerEmails = qProviderEmailRepository.getProviderEmails(emails);

        Set<String> updateUserEmails = providerEmails.stream()
                .map(ProviderEmail::getEmail)
                .collect(Collectors.toSet());

        Map<User, SaveUserListRequestDto> userUpdateMap = new HashMap<>();
        for (ProviderEmail providerEmail : providerEmails) {
            saveUserListRequestDtoList.stream()
                    .filter(v -> v.getEmail().equals(providerEmail.getEmail()))
                    .findAny().ifPresent(saveUserListRequestDto -> userUpdateMap.put(providerEmail.getUser(), saveUserListRequestDto));
        }

        for (User user : userUpdateMap.keySet()) {
            SaveUserListRequestDto saveUserListRequestDto = userUpdateMap.get(user);
            // 비밀번호에 데이터가 없을 경우
            if(saveUserListRequestDto.getPassword() == null || saveUserListRequestDto.getPassword().isEmpty()) {
                user.changePassword(null);
            }
            // 비밀번호가 변경되었을 경우
            else if (!saveUserListRequestDto.getPassword().equals(user.getPassword())) {
                String password = passwordEncoder.encode(saveUserListRequestDto.getPassword());
                user.changePassword(password);
            }
            // 그룹 변경
            List<String> groupsName = Optional.ofNullable(saveUserListRequestDto.getGroupName())
                    .map(name -> Arrays.stream(name.split(","))
                            .map(String::trim)
                            .toList())
                    .orElse(Collections.emptyList());

            if (groupsName.isEmpty()) {
                // Case 1: 요청의 groupName 값이 null일 경우 기존의 UserGroup 철회
                user.getGroups().forEach(userGroup -> userGroup.updateStatus(ClientStatus.WITHDRAWAL));
            } else if (user.getGroups().isEmpty()) {
                // Case 2: 유저에 포함된 그룹이 없을 때
                List<UserGroup> userGroups = Group.getGroups(groups, groupsName).stream()
                        .map(group -> UserGroup.builder()
                                .group(group)
                                .user(user)
                                .clientStatus(ClientStatus.BELONG)
                                .build())
                        .collect(Collectors.toList());

                userGroupRepository.saveAll(userGroups);
            } else {
                // Case 3: 유저에 포함된 그룹이 존재할 때
                Map<String, Group> nameToGroupMap = groups.stream()
                        .filter(group -> groupsName.contains(group.getName()))
                        .collect(Collectors.toMap(Group::getName, Function.identity()));

                user.getGroups().forEach(userGroup -> {
                    if (nameToGroupMap.containsKey(userGroup.getGroup().getName())) {
                        // 기존에 존재할 경우 상태값 변경(BELONG)
                        userGroup.updateStatus(ClientStatus.BELONG);
                        nameToGroupMap.remove(userGroup.getGroup().getName());
                    } else {
                        // 기존에 존재했지만 요청 값에 없는 경우 철회(WITHDRAWAL) 상태로 변경
                        userGroup.updateStatus(ClientStatus.WITHDRAWAL);
                        List<UserSpot> userSpots = user.getUserSpots();
                        Optional<UserSpot> userSpot = userSpots.stream().filter(v -> v.getSpot().getGroup().equals(userGroup.getGroup()))
                                .findAny();
                        userSpot.ifPresent(userSpotRepository::delete);
                    }
                });
                // 유저 내에 존재하지 않는 그룹은 추가
                List<UserGroup> userGroups = nameToGroupMap.values().stream()
                        .map(group -> UserGroup.builder()
                                .group(group)
                                .user(user)
                                .clientStatus(ClientStatus.BELONG)
                                .build())
                        .collect(Collectors.toList());

                userGroupRepository.saveAll(userGroups);
            }
            user.updateName(saveUserListRequestDto.getName());
            user.changePhoneNumber(saveUserListRequestDto.getPhone());
            user.updateRole(Role.ofRoleName(saveUserListRequestDto.getRole()));
            user.updateUserStatus(UserStatus.ofCode(saveUserListRequestDto.getStatus()));
            user.updatePoint(BigDecimal.valueOf(saveUserListRequestDto.getPoint() == null ? 0 : saveUserListRequestDto.getPoint()));
            user.changeMarketingAgreement(saveUserListRequestDto.getMarketingAgree(), saveUserListRequestDto.getMarketingAlarm(), saveUserListRequestDto.getOrderAlarm());
        }

        // FIXME 신규 생성 요청
        List<SaveUserListRequestDto> createUserDtos = saveUserListRequestDtoList.stream()
                .filter(v -> !updateUserEmails.contains(v.getEmail()))
                .toList();
        for (SaveUserListRequestDto createUserDto : createUserDtos) {
            UserDto userDto = UserDto.builder()
                    .email(createUserDto.getEmail())
                    .password(createUserDto.getPassword() == null ? null : passwordEncoder.encode(createUserDto.getPassword()))
                    .phone(createUserDto.getPhone())
                    .name(createUserDto.getName())
                    .role(createUserDto.getRole() == null ? Role.USER : Role.ofRoleName(createUserDto.getRole())).build();
            User user = userRepository.save(userMapper.toEntity(userDto));
            user.updatePoint(BigDecimal.valueOf(createUserDto.getPoint() == null ? 0 : createUserDto.getPoint()));
            user.changeMarketingAgreement(createUserDto.getMarketingAgree(), createUserDto.getMarketingAlarm(), createUserDto.getOrderAlarm());

            ProviderEmail providerEmail = ProviderEmail.builder().email(createUserDto.getEmail()).provider(Provider.GENERAL).user(user).build();
            providerEmailRepository.save(providerEmail);

            List<String> groupsName = Optional.ofNullable(createUserDto.getGroupName())
                    .map(name -> Arrays.stream(name.split(",")).map(String::trim).toList())
                    .orElse(List.of());

            Group.getGroups(groups, groupsName).stream()
                    .map(group -> UserGroup.builder()
                            .group(group)
                            .user(user)
                            .clientStatus(ClientStatus.BELONG)
                            .build())
                    .forEach(userGroupRepository::save);
        }

    }

    @Override
    public void resetPassword(UserResetPasswordRequestDto passwordResetDto) {
        //리셋할 비밀번호 설정
        String reset = "1234";
        String password = passwordEncoder.encode(reset);

        //수정
        qUserRepository.resetPassword(passwordResetDto.getUserId(), password);

    }
}
