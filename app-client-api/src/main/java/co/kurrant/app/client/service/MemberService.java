package co.kurrant.app.client.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.client.dto.ImportExcelWaitingUserListResponseDto;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.kurrant.app.client.dto.MemberListResponseDto;
import co.kurrant.app.client.dto.MemberWaitingListResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MemberService {


    List<MemberListResponseDto> getUserList(String code, OffsetBasedPageRequest pageable);

    List<MemberWaitingListResponseDto> getWaitingUserList(String code, OffsetBasedPageRequest pageable);

    void deleteMember(DeleteMemberRequestDto deleteMemberRequestDto);

    List<ImportExcelWaitingUserListResponseDto> importExcelForWaitingUserList(MultipartFile file) throws IOException;
}
