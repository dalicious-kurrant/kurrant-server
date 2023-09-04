package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.kurrant.app.admin_api.dto.user.*;
import co.kurrant.app.admin_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Tag(name = "4.User")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/users")
@RestController
public class UserController {

    private final UserService userService;

    @ControllerMarker(ControllerType.USER)
    @Operation(summary = "유저조회", description = "유저 목록을 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all")
    public ResponseMessage getUserList(@RequestParam Map<String, Object> parameters,
                                       @RequestParam(required = false, defaultValue = "50") Integer limit, @RequestParam Integer page) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(((long) limit * (page - 1)), limit, Sort.unsorted());
        return ResponseMessage.builder()
                .data(userService.getUserList(parameters, pageable))
                .message("유저 목록 조회")
                .build();
    }

    @ControllerMarker(ControllerType.USER)
    @Operation(summary = "유저 정보 엑셀 내보내기", description = "유저 전체 정보 엑셀 내보내기를 한다")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/excel")
    public ResponseMessage getUserListAll() {
        return ResponseMessage.builder()
                .data(userService.getUserListAll())
                .message("유저 목록 조회")
                .build();
    }

    @ControllerMarker(ControllerType.USER)
    @Operation(summary = "선택 유저 탈퇴처리", description = "선택한 유저를 탈퇴처리한다")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("")
    public ResponseMessage deleteMember(@RequestBody DeleteMemberRequestDto deleteMemberRequestDto){
        userService.deleteMember(deleteMemberRequestDto);
        return ResponseMessage.builder()
                .message("선택한 유저를 탈퇴처리했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.USER)
    @Operation(summary = "저장하기", description = "수정사항을 저장한다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("")
    public ResponseMessage saveUserList(@RequestBody List<SaveUserListRequestDto> saveUserListRequestDtoList){
        userService.saveUserList(saveUserListRequestDtoList);
        return ResponseMessage.builder()
                .message("저장에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.USER)
    @Operation(summary = "비밀번호 리셋하기", description = "비밀번호를 리셋한다")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reset/password")
    public ResponseMessage resetPassword(@RequestBody UserResetPasswordRequestDto passwordResetDto){
        userService.resetPassword(passwordResetDto);
        return ResponseMessage.builder()
                .message("비밀번호가 리셋되었습니다.")
                .build();
    }

    @Operation(summary = "테스트 데이터 조회")
    @GetMapping("/test/data")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMessage getTestData(){
        return ResponseMessage.builder()
                .message("TestData 조회 성공!")
                .data(userService.getTestData())
                .build();
    }


    @ControllerMarker(ControllerType.USER)
    @Operation(summary = "테스트 데이터 입력")
    @PostMapping("/test/data")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMessage saveTestData(@RequestBody SaveTestDataRequestDto saveTestDataRequestDto){
        String message = userService.saveTestData(saveTestDataRequestDto);
        return ResponseMessage.builder()
                .message(message)
                .build();
    }

    @Operation(summary = "테스트 데이터 수정")
    @PatchMapping("/test/data")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMessage updateTestData(@RequestBody UpdateTestDataRequestDto updateTestDataRequestDto){
        String message = userService.updateTestData(updateTestDataRequestDto);
        return ResponseMessage.builder()
                .message(message)
                .build();
    }

    @Operation(summary = "테스트 데이터 삭제")
    @DeleteMapping("/test/data")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMessage deleteTestData(@RequestBody DeleteTestDataRequestDto deleteTestDataRequestDto){
        String message = userService.deleteTestData(deleteTestDataRequestDto);
        return ResponseMessage.builder()
                .message(message)
                .build();
    }
}
