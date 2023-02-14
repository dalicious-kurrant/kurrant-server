package co.kurrant.app.client.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.kurrant.app.client.dto.MemberListResponseDto;
import co.kurrant.app.client.dto.MemberWaitingListResponseDto;

import java.util.List;

public interface MemberService {


    List<MemberListResponseDto> getUserList(String code, OffsetBasedPageRequest pageable);

    List<MemberWaitingListResponseDto> getWaitingUserList(String code, OffsetBasedPageRequest pageable);
}
