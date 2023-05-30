package co.kurrant.app.client_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.dto.ClientExcelSaveDtoList;
import co.dalicious.domain.client.dto.ClientUserWaitingListSaveRequestDto;
import co.dalicious.domain.client.dto.ClientUserWaitingListSaveRequestDtoList;
import co.dalicious.domain.client.dto.ImportExcelWaitingUserListResponseDto;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.kurrant.app.client_api.dto.DeleteWaitingMemberRequestDto;
import co.kurrant.app.client_api.dto.MemberIdListDto;
import co.kurrant.app.client_api.dto.MemberListResponseDto;
import co.kurrant.app.client_api.dto.MemberWaitingListResponseDto;
import co.kurrant.app.client_api.model.SecurityUser;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface MemberService {


    List<MemberListResponseDto> getUserList(SecurityUser securityUser);

    List<MemberWaitingListResponseDto> getWaitingUserList(SecurityUser securityUser);

    void deleteMember(SecurityUser securityUser, MemberIdListDto deleteMemberRequestDto);

    void insertMemberListByExcel(SecurityUser securityUser, ClientUserWaitingListSaveRequestDtoList clientUserWaitingListSaveRequestDtoList);

    void deleteWaitingMember(SecurityUser securityUser, DeleteWaitingMemberRequestDto deleteWaitingMemberRequestDto);
}
