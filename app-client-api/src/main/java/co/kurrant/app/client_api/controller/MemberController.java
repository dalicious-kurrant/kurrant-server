package co.kurrant.app.client_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.client.dto.ClientExcelSaveDto;
import co.dalicious.domain.client.dto.ClientExcelSaveDtoList;
import co.dalicious.domain.client.dto.ClientUserWaitingListSaveRequestDto;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.kurrant.app.client_api.dto.DeleteWaitingMemberRequestDto;
import co.kurrant.app.client_api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
  public ResponseMessage getUserList(@RequestParam String code) {
    return ResponseMessage.builder()
            .data(memberService.getUserList(code))
            .message("유저 목록 조회")
            .build();
  }


  @Operation(summary = "가입 대기 유저조회", description = "가입 대기 유저 목록을 조회한다.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/waiting")
  public ResponseMessage getWaitingUserList(@RequestParam String code){
    return ResponseMessage.builder()
            .data(memberService.getWaitingUserList(code))
            .message("가입대기 유저 목록 조회")
            .build();
  }


  @Operation(summary = "선택 가입 대기 유저 탈퇴처리", description = "선택한 유저를 가입 대기 유저를 탈퇴처리한다")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/waiting")
  public ResponseMessage deleteWaitingMember(@RequestBody DeleteWaitingMemberRequestDto deleteWaitingMemberRequestDto){
    memberService.deleteWaitingMember(deleteWaitingMemberRequestDto);
    return ResponseMessage.builder()
            .message("선택한 유저를 탈퇴처리했습니다.")
            .build();
  }

  @Operation(summary = "선택 유저 탈퇴처리", description = "선택한 유저를 탈퇴처리한다")
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("")
  public ResponseMessage deleteMember(@RequestBody DeleteMemberRequestDto deleteMemberRequestDto){
    memberService.deleteMember(deleteMemberRequestDto);
    return ResponseMessage.builder()
            .message("선택한 유저를 탈퇴처리했습니다.")
            .build();
  }

  @Operation(summary = "저장하기", description = "수정사항을 저장한다.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("")
  public ResponseMessage insertMemberList(@RequestBody ClientUserWaitingListSaveRequestDto clientUserWaitingListSaveRequestDto){
    memberService.insertMemberList(clientUserWaitingListSaveRequestDto);
    return ResponseMessage.builder()
            .message("저장에 성공하였습니다.")
            .build();
  }


  @Operation(summary = "엑셀 저장하기", description = "엑셀로 받아온 수정사항을 저장한다.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/excel")
  public ResponseMessage insertMemberListByExcel(@RequestBody ClientExcelSaveDtoList clientExcelSaveDtoList){
    memberService.insertMemberListByExcel(clientExcelSaveDtoList);
    return ResponseMessage.builder()
            .message("저장에 성공하였습니다.")
            .build();
  }




    /*
  @Operation(summary = "엑셀 불러오기", description = "엑셀 파일을 불러온다.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/waiting/excels/import")
  public ResponseMessage importExcelForWaitingUserList(@RequestParam("file") MultipartFile file) throws IOException {
    return ResponseMessage.builder()
            .data(memberService.importExcelForWaitingUserList(file))
            .message("엑셀 불러오기 성공")
            .build();
  }
  */
  /*
  @Operation(summary = "엑셀 내보내기", description = "엑셀 파일을 내보낸다.")
  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/downloadExcelFile", method = RequestMethod.POST)
  public ResponseEntity<InputStreamResource> exportExcelForWaitingUserList(HttpServletResponse response,
                                                                           @RequestBody ExportExcelWaitngUserListRequestDto exportExcelWaitngUserListRequestDto, Model model) throws IOException {
    return memberService.exportExcelForWaitingUserList(response, exportExcelWaitngUserListRequestDto);
  }
  */
}
