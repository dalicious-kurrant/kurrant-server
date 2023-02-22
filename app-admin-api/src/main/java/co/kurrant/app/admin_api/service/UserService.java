package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.kurrant.app.admin_api.dto.user.SaveAndUpdateUserList;

import java.math.BigInteger;

public interface UserService {

    Object getUserList(OffsetBasedPageRequest pageable);

    void deleteMember(DeleteMemberRequestDto deleteMemberRequestDto);

    void saveUserList(SaveAndUpdateUserList saveAndUpdateUserList);

    void resetPassword(BigInteger userId);
}
