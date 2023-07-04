package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.enums.AlarmType;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Department;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.mapper.DepartmentMapper;
import co.dalicious.domain.client.repository.DepartmentRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.order.repository.QOrderRepository;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.dalicious.domain.user.dto.UserDto;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.*;
import co.dalicious.domain.user.mapper.UserDepartmentMapper;
import co.dalicious.domain.user.mapper.UserHistoryMapper;
import co.dalicious.domain.user.repository.*;
import co.dalicious.domain.user.util.PointUtil;
import co.dalicious.domain.user.validator.UserValidator;
import co.kurrant.app.admin_api.dto.user.*;
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
    private final DepartmentMapper departmentMapper;
    private final UserDepartmentMapper userDepartmentMapper;
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
    private final UserValidator userValidator;
    private final UserDepartmentRepository userDepartmentRepository;
    private final FoodRepository foodRepository;
    private final DepartmentRepository departmentRepository;
    private final UserTasteTestDataRepository userTasteTestDataRepository;
    private final QUserTasteTestDataRepository qUserTasteTestDataRepository;
    private final PointUtil pointUtil;
    private final PushUtil pushUtil;
    private final PushService pushService;


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
        saveUserListRequestDtoList = saveUserListRequestDtoList.stream()
                .peek(dto -> dto.setEmail(dto.getEmail().trim()))
                .filter(dto -> dto.getStatus() != null && dto.getStatus() != 0)
                .collect(Collectors.toList());

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

        // FIXME: 수정 요청
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

        Set<User> pushAlarmForCorporationUser = new HashSet<>();

        for (User user : userUpdateMap.keySet()) {
            SaveUserListRequestDto saveUserListRequestDto = userUpdateMap.get(user);
            // 비밀번호에 데이터가 없을 경우
            if (saveUserListRequestDto.getPassword() == null || saveUserListRequestDto.getPassword().isEmpty()) {
                user.changePassword(null);
            }
            // 비밀번호가 변경되었을 경우
            else if (!saveUserListRequestDto.getPassword().equals(user.getPassword())) {
                String password = passwordEncoder.encode(saveUserListRequestDto.getPassword());
                user.changePassword(password);
            }

            //결제 비밀번호에 데이터가 없을 경우
            if (saveUserListRequestDto.getPaymentPassword() == null || saveUserListRequestDto.getPaymentPassword().isEmpty()){
                user.changePaymentPassword(null);
            }
            // 결제 비밀번호가 변경 되었을 경우
            else if ( !saveUserListRequestDto.getPaymentPassword().equals(user.getPaymentPassword())){
                String paymentPassword = passwordEncoder.encode(saveUserListRequestDto.getPaymentPassword());
                user.changePaymentPassword(paymentPassword);
            }

            //부서명이 변경 되었을 경우
//            if (!saveUserListRequestDto.getDepartmentName().equals(user.getDepartment())){
//                Department department = departmentRepository.findByName(saveUserListRequestDto.getDepartmentName());
//                //존재하지 않는 부서면 생성
//                if (department == null || department.getName().isEmpty()){
//                    System.out.println("부서 수정");
//                    Department saveDepartment = departmentMapper.toEntity(user.getGroups().get(0).getGroup(), saveUserListRequestDto.getDepartmentName());
//                    departmentRepository.save(saveDepartment);
//                    userDepartmentRepository.save(userDepartmentMapper.toEntity(user, saveDepartment));
//                } else {    //존재하면 해당 유저를 등록한다.
//                    System.out.println("부서수정22");
//                    userDepartmentRepository.save(userDepartmentMapper.toEntity(user,department));
//                }
//            }


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
                if(userGroups.stream().anyMatch(v -> v.getGroup() instanceof Corporation)) {
                    pushAlarmForCorporationUser.add(user);
                }
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
                        if(userGroup.getGroup() instanceof Corporation) {
                            pushAlarmForCorporationUser.add(user);
                        }
                    } else {
                        // 기존에 존재했지만 요청 값에 없는 경우 철회(WITHDRAWAL) 상태로 변경
                        userGroup.updateStatus(ClientStatus.WITHDRAWAL);
                        List<UserSpot> deleteUserSpots = user.getUserSpots().stream()
                                    .filter(v -> v.getSpot().getGroup().equals(userGroup.getGroup()))
                                    .toList();
                        userSpotRepository.deleteAll(deleteUserSpots);
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
                if(userGroups.stream().anyMatch(v -> v.getGroup() instanceof Corporation)) {
                    pushAlarmForCorporationUser.add(user);
                }
            }
            if (saveUserListRequestDto.getName() != null && !user.getName().equals(saveUserListRequestDto.getName()))
                user.updateName(saveUserListRequestDto.getName());
            if (saveUserListRequestDto.getPhone() != null && !user.getPhone().equals(saveUserListRequestDto.getPhone()))
                user.changePhoneNumber(saveUserListRequestDto.getPhone());
            if (saveUserListRequestDto.getRole() != null && !user.getRole().equals(Role.ofRoleName(saveUserListRequestDto.getRole())))
                user.updateRole(Role.ofRoleName(saveUserListRequestDto.getRole()));
            if (saveUserListRequestDto.getStatus() != null && !user.getUserStatus().equals(UserStatus.ofCode(saveUserListRequestDto.getStatus())))
                user.updateUserStatus(UserStatus.ofCode(saveUserListRequestDto.getStatus()));
            if (saveUserListRequestDto.getPoint() != null) {
                BigDecimal point = BigDecimal.valueOf(saveUserListRequestDto.getPoint());
                if (!user.getPoint().equals(point)) {

                    BigDecimal differencePoint = point.subtract(user.getPoint());
                    // 차액이 플러스면
                    if(differencePoint.compareTo(BigDecimal.valueOf(0)) > 0) {
                        pointUtil.createPointHistoryByOthers(user, null, PointStatus.ADMIN_REWARD, differencePoint);
                    }
                    // 차액이 마이너스면
                    else if(differencePoint.compareTo(BigDecimal.valueOf(0)) < 0) {
                        differencePoint = differencePoint.multiply(BigDecimal.valueOf(-1));
                        pointUtil.createPointHistoryByOthers(user, null, PointStatus.ADMIN_POINTS_RECOVERED, differencePoint);
                    }

                    user.updatePoint(point);
                }
            }
            if (saveUserListRequestDto.getMarketingAgree() != null && saveUserListRequestDto.getMarketingAlarm() != null && saveUserListRequestDto.getOrderAlarm() != null &&
                    (!user.getMarketingAgree().equals(saveUserListRequestDto.getMarketingAgree()) ||
                            !user.getMarketingAlarm().equals(saveUserListRequestDto.getMarketingAgree()) ||
                            !user.getOrderAlarm().equals(saveUserListRequestDto.getOrderAlarm()))) {
                user.changeMarketingAgreement(saveUserListRequestDto.getMarketingAgree(), saveUserListRequestDto.getMarketingAlarm(), saveUserListRequestDto.getOrderAlarm());
            }

        }

        // FIXME: 신규 생성 요청
        List<SaveUserListRequestDto> createUserDtos = saveUserListRequestDtoList.stream()
                .filter(v -> !updateUserEmails.contains(v.getEmail()))
                .toList();
        for (SaveUserListRequestDto createUserDto : createUserDtos) {
            // 이미 있는 핸드폰 번호인지 확인
            if(userValidator.isPhoneValidBoolean(createUserDto.getPhone())) continue;

            UserDto userDto = UserDto.builder()
                    .email(createUserDto.getEmail())
                    .password((createUserDto.getPassword() == null) ? null : passwordEncoder.encode(createUserDto.getPassword()))
                    .phone(createUserDto.getPhone())
                    .name(createUserDto.getName())
                    .role(createUserDto.getRole() == null ? Role.USER : Role.ofRoleName(createUserDto.getRole()))
                    .paymentPassword((createUserDto.getPaymentPassword() == null) ? null : passwordEncoder.encode(createUserDto.getPaymentPassword())).build();

            User user = userRepository.save(userMapper.toEntity(userDto));
            if (user.isAdmin() && userValidator.adminExists()) {
                throw new ApiException(ExceptionEnum.ADMIN_USER_SHOULD_BE_UNIQUE);
            }
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

        // TODO: 프라이빗 스팟 초대 시 푸시알림 추가
        List<PushRequestDtoByUser> pushRequestDtoByUsers = pushAlarmForCorporationUser.stream()
                .map(user -> {
                    PushCondition pushCondition = PushCondition.NEW_SPOT;
                    String message = pushUtil.getContextCorporationSpot(user.getName(), pushCondition);
                    pushUtil.savePushAlarmHash(pushCondition.getTitle(), message, user.getId(), AlarmType.SPOT_NOTICE, null);
                    return pushUtil.getPushRequest(user, pushCondition, message);
                }).toList();
        if(pushRequestDtoByUsers.size() > 500) {
            List<List<PushRequestDtoByUser>> slicePushRequestDtoByUsers = pushUtil.sliceByChunkSize(pushRequestDtoByUsers);

            for(List<PushRequestDtoByUser> list : slicePushRequestDtoByUsers) {
                pushService.sendToPush(list);
            }
        }
        else pushService.sendToPush(pushRequestDtoByUsers);

    }

    @Override
    public void resetPassword(UserResetPasswordRequestDto passwordResetDto) {
        //리셋할 비밀번호 설정
        String reset = "12345678";
        String password = passwordEncoder.encode(reset);

        //수정
        qUserRepository.resetPassword(passwordResetDto.getUserId(), password);

    }

    @Override
    @Transactional
    public String saveTestData(SaveTestDataRequestDto saveTestDataRequestDto) {

        List<TestData> testData = saveTestDataRequestDto.getTestData();
        String foodIds = null;

        //기존 TestData 삭제
        userTasteTestDataRepository.deleteAll();

        //foodId 검증
        for (int i = 0; i < testData.size(); i++) {
            for (int j = 0; j < testData.get(i).getFoodIds().size(); j++) {
                BigInteger foodId = testData.get(i).getFoodIds().get(j);
                Food food = foodRepository.findById(foodId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));

                //image가 없는 food 걸러내기
                if (food.getImages().isEmpty()){
                    throw new ApiException(ExceptionEnum.NOT_FOUND_FOOD_IMAGE);
                }
            }

            //UserTasteTestData에 저장
            foodIds = testData.get(i).getFoodIds().toString().substring(1, testData.get(i).getFoodIds().toString().length() -1);
            UserTasteTestData userTasteTestData = UserTasteTestData.builder()
                    .foodIds(foodIds)
                    .page(testData.get(i).getPageNum())
                    .build();

            UserTasteTestData saveResult = userTasteTestDataRepository.save(userTasteTestData);
            if (saveResult.getId() == null){
                return "저장 실패...";
            }
        }

        return "저장에 성공했습니다!";
    }

    @Override
    public String updateTestData(UpdateTestDataRequestDto updateTestDataRequestDto) {
        //foodId 검증
        List<UpdateTestData> updateTestDataList = updateTestDataRequestDto.getUpdateTestDataList();
        String foodIds = null;
        for (int i = 0; i < updateTestDataList.size(); i++) {
            for (int j = 0; j < updateTestDataList.get(i).getTestData().getFoodIds().size(); j++) {
                BigInteger foodId = updateTestDataList.get(i).getTestData().getFoodIds().get(j);
                foodRepository.findById(foodId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));
            }
            //UserTasteTestData에 수정 반영
            foodIds = updateTestDataList.get(i).getTestData().getFoodIds().toString().substring(1,updateTestDataList.get(i).getTestData().getFoodIds().toString().length() -1);
            BigInteger testDataId = updateTestDataList.get(i).getTestDataId();
            Integer page = updateTestDataRequestDto.getUpdateTestDataList().get(i).getTestData().getPageNum();
            long updateResult = qUserTasteTestDataRepository.updateTestData(foodIds, testDataId, page);

            //결과가 1이 아닌경우는 실패
            if (updateResult != 1){
                return "수정 실패...";
            }
        }

        return "수정에 성공했습니다!";
    }

    @Override
    public String deleteTestData(DeleteTestDataRequestDto deleteTestDataRequestDto) {

        //testIdList가 비어있는지 체크
        if (deleteTestDataRequestDto.getTestDataIdList().isEmpty()){
            throw new ApiException(ExceptionEnum.NOT_FOUND_TEST_DATA_ID);
        }

        //testId 존재하는지 체크
        for (BigInteger id : deleteTestDataRequestDto.getTestDataIdList()){
            userTasteTestDataRepository.findById(id).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_MATCHED_TEST_DATA_ID));
            //존재하면 삭제
            userTasteTestDataRepository.deleteById(id);
        }

        return "테스트 데이터 삭제 성공!";
    }
}
