package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.repository.QCorporationRepository;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserHistory;
import co.dalicious.domain.user.mapper.UserHistoryMapper;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.UserHistoryRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.admin_api.dto.user.SaveUserListRequestDto;
import co.kurrant.app.admin_api.dto.user.UserInfoResponseDto;
import co.kurrant.app.admin_api.mapper.UserMapper;
import co.kurrant.app.admin_api.service.UserService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserHistoryMapper userHistoryMapper;

    private final UserHistoryRepository userHistoryRepository;
    private final QUserRepository qUserRepository;
    private final QUserGroupRepository qUserGroupRepository;

    @Override
    public ListItemResponseDto<UserInfoResponseDto> getUserList(OffsetBasedPageRequest pageable) {

        Page<User> users = userRepository.findAll(pageable);

        users.stream().filter(user -> user.getUserStatus().getCode() != 0)
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

        List<UserInfoResponseDto> userInfoResponseDtoList = users.get()
                .map(userMapper::toDto).toList();

        return ListItemResponseDto.<UserInfoResponseDto>builder().items(userInfoResponseDtoList)
                .total(users.getTotalElements()).count(users.getNumberOfElements())
                .limit(pageable.getPageSize()).offset(pageable.getOffset()).build();
    }

    @Override
    public void deleteMember(DeleteMemberRequestDto deleteMemberRequestDto) {

        List<BigInteger> userIdList = deleteMemberRequestDto.getUserIdList();

        //code로 CorporationId 찾기 (=GroupId)
        BigInteger groupId = deleteMemberRequestDto.getGroupId();

        if (userIdList.size() == 0) throw new ApiException(ExceptionEnum.BAD_REQUEST);

        for (BigInteger userId : userIdList) {
            User deleteUser = qUserRepository.findByUserId(userId);

            UserHistory userHistory = userHistoryMapper.toEntity(deleteUser, groupId);

             userHistoryRepository.save(userHistory);

            Long deleteResult = qUserGroupRepository.deleteMember(userId, groupId);
            if (deleteResult != 1) throw new ApiException(ExceptionEnum.USER_PATCH_ERROR);
        }

    }

    @Override
    public void saveUserList(List<SaveUserListRequestDto> saveUserListRequestDtoList) {

        //TODO 내일하자..

    }
}
