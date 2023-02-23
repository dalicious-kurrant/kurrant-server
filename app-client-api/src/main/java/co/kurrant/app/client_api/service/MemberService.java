package co.kurrant.app.client_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.dto.ClientExcelSaveDtoList;
import co.dalicious.domain.client.dto.ClientUserWaitingListSaveRequestDto;
import co.dalicious.domain.client.dto.ImportExcelWaitingUserListResponseDto;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.kurrant.app.client_api.dto.MemberListResponseDto;
import co.kurrant.app.client_api.dto.MemberWaitingListResponseDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface MemberService {


    ListItemResponseDto<MemberListResponseDto> getUserList(String code, OffsetBasedPageRequest pageable);

    ListItemResponseDto<MemberWaitingListResponseDto> getWaitingUserList(String code, OffsetBasedPageRequest pageable);

    void deleteMember(DeleteMemberRequestDto deleteMemberRequestDto);

    List<ImportExcelWaitingUserListResponseDto> importExcelForWaitingUserList(MultipartFile file) throws IOException;

    ResponseEntity<InputStreamResource> exportExcelForWaitingUserList(HttpServletResponse response, ClientUserWaitingListSaveRequestDto exportExcelWaitngUserListRequestDto) throws IOException;

    void insertMemberList(ClientUserWaitingListSaveRequestDto clientUserWaitingListSaveRequestDto);

    void insertMemberListByExcel(ClientExcelSaveDtoList clientExcelSaveDtoList);
}
