package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.kurrant.app.admin_api.dto.user.SaveAndUpdateUserList;
import co.kurrant.app.admin_api.dto.user.SaveUserListRequestDto;
import co.kurrant.app.admin_api.dto.user.UserResetPasswordRequestDto;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface UserService {

    Object getUserList(Map<String, Object> parameters);

    long deleteMember(DeleteMemberRequestDto deleteMemberRequestDto);

    void saveUserList(List<SaveUserListRequestDto> saveUserListRequestDtoList);

    void resetPassword(UserResetPasswordRequestDto passwordResetDto);
}
