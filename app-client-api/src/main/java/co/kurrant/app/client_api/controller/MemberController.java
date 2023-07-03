package co.kurrant.app.client_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.client.dto.ClientExcelSaveDto;
import co.dalicious.domain.client.dto.ClientExcelSaveDtoList;
import co.dalicious.domain.client.dto.ClientUserWaitingListSaveRequestDto;
import co.dalicious.domain.client.dto.ClientUserWaitingListSaveRequestDtoList;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.kurrant.app.client_api.dto.DeleteWaitingMemberRequestDto;
import co.kurrant.app.client_api.dto.MemberIdListDto;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.MemberService;
import co.kurrant.app.client_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Tag(name = "1. Member")
@Slf4j
@RequestMapping(value = "/v1/client/members")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "유저조회", description = "유저 목록을 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ResponseMessage getUserList(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(memberService.getUserList(securityUser))
                .message("유저 목록 조회")
                .build();
    }


    @Operation(summary = "가입 대기 유저조회", description = "가입 대기 유저 목록을 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/waiting")
    public ResponseMessage getWaitingUserList(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(memberService.getWaitingUserList(securityUser))
                .message("가입대기 유저 목록 조회")
                .build();
    }


    @Operation(summary = "선택 가입 대기 유저 탈퇴처리", description = "선택한 유저를 가입 대기 유저를 탈퇴처리한다")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/waiting")
    public ResponseMessage deleteWaitingMember(Authentication authentication, @RequestBody DeleteWaitingMemberRequestDto deleteWaitingMemberRequestDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        memberService.deleteWaitingMember(securityUser, deleteWaitingMemberRequestDto);
        return ResponseMessage.builder()
                .message("선택한 유저를 탈퇴처리했습니다.")
                .build();
    }

    @Operation(summary = "선택 유저 탈퇴처리", description = "선택한 유저를 탈퇴처리한다")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("")
    public ResponseMessage deleteMember(Authentication authentication, @RequestBody MemberIdListDto deleteMemberRequestDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        memberService.deleteMember(securityUser, deleteMemberRequestDto);
        return ResponseMessage.builder()
                .message("선택한 유저를 탈퇴처리했습니다.")
                .build();
    }

    @Operation(summary = "엑셀 저장하기", description = "엑셀로 받아온 수정사항을 저장한다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/excel")
    public ResponseMessage insertMemberListByExcel(Authentication authentication, @RequestBody ClientUserWaitingListSaveRequestDtoList clientUserWaitingListSaveRequestDtoList) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        memberService.insertMemberListByExcel(securityUser, clientUserWaitingListSaveRequestDtoList);
        return ResponseMessage.builder()
                .message("저장에 성공하였습니다.")
                .build();
    }
}
