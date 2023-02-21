package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;

public interface UserService {

    Object getUserList(OffsetBasedPageRequest pageable);

    void deleteMember(DeleteMemberRequestDto deleteMemberRequestDto);
}
